package mobi.storedot.newsapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link StoryAdapter} knows how to create a list item layout for each Story
 * in the data source (a list of {@link Story} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */

public class StoryAdapter extends ArrayAdapter<Story> {

    /**
     * Constructs a new {@link StoryAdapter}.
     * @param context of the app
     * @param news is the list of news, which is the data source of the adapter
     */

    public StoryAdapter(Context context, List<Story>
            news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information
     * about the story at the given position
     * in the list of news.
     */

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        // Check if there is an existing list item view
        // (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        //getContext() is used to get Context from super class above.

        final ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.news_list_item, parent, false);

            holder = new ViewHolder();

            // Find the TextView with view ID sectionName
            holder.topicTextView = (TextView) convertView.findViewById(R.id.sectionName);

            // Find the TextView with view ID webTitle
            holder.titleTextView = (TextView) convertView.findViewById(R.id.webTitle);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Find the story at the given position in the list of news
        Story currentStory = getItem(position);

        if(currentStory != null) {
        // Display the topic of the current story in that TextView
        holder.topicTextView.setText(currentStory.getSection());
        // Display the web title of the current story in that TextView
        holder.titleTextView.setText(currentStory.getTitle());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView topicTextView;
        TextView titleTextView;
    }
}