package mobi.storedot.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity implements the LoaderCallbacks interface,
 * along with a generic parameter specifying what the loader will return (in this case an Story).
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * let's modify the value of the GUARDIAN_BASE_REQUEST_URL constant in the EarthquakeActivity class to the base URI.
     * Later we’ll use UriBuilder.appendQueryParameter() methods to add additional parameters
     * to the URI (such as JSON response format, 10 news requested, section name value, and sort order).
     */

    private static final String GUARDIAN_BASE_REQUEST_URL =
            "http://content.guardianapis.com/search?q=&api-key=test";

    /**
     * Constant value for the story loader ID. We can choose any integer.
     */
    private static final int STORY_LOADER_ID = 1;
    /**
     * Adapter for the list of news
     */
    private StoryAdapter mAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "onCreate");

        /**
         * Get a reference to the ConnectivityManager to check state of network connectivity
         */

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        /**
         * Get details on the currently active default data
         network
         */

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data

        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).

            loaderManager.initLoader(STORY_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Find a reference to the {@link ListView} in the layout
        ListView storyListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        storyListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news(stories) as input
        mAdapter = new StoryAdapter(this, new ArrayList<Story>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        storyListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected story.

        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current story that was clicked on
                Story currentStory = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri storyUri = null;
                if (currentStory != null) {
                    storyUri = Uri.parse(currentStory.getUrl());
                }

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, storyUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

    }

    /**
     * We need onCreateLoader(), for when the LoaderManager has determined that the loader
     * with our specified ID isn't running, so we should create a new one.
     */

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {

        /** Then we can replace the body of onCreateLoader() method to read the user’s latest preferences
         * for the minimum magnitude, construct a proper URI with their preference, and then create a new
         * Loader for that URI.
         */

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String selectSection = sharedPrefs.getString(
                getString(R.string.settings_sectionName_key),
                getString(R.string.settings_sectionName_default));

        /**we need to look up the user’s preferred sort order when we build the URI for making the HTTP request.
         * Read from SharedPreferences and check for the value associated with the key: getString(R.string.settings_order_by_key).
         */

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        Uri baseUri = Uri.parse(GUARDIAN_BASE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("pageSize","10");
        uriBuilder.appendQueryParameter("sectionName", selectSection);

        /**
        * When building the URI and appending query parameters, instead of hardcoding the “orderBy” parameter to be “newest”,
        * we will use the user’s preference (stored in the orderBy variable).
        */
        uriBuilder.appendQueryParameter("order-by", orderBy);

        // Create a new loader for the given URL
        return new StoryLoader(this, uriBuilder.toString());
    }

    /**
     * We need onLoadFinished(), where we'll do exactly what we did in onPostExecute(),
     * and use the story data to update our UI - by updating the dataset in the adapter.
     */

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> news) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);


        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Story}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    /**
     * And we need onLoaderReset(), we're being informed that the data from our loader is no longer valid.
     * This isn't actually a case that's going to come up with our simple loader,
     * but the correct thing to do is to remove all the story data from our UI
     * by clearing out the adapter’s data set.
     */

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    /**
     * And override a couple methods in MainActivity.java to inflate the menu, and respond when users click on our menu item
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}