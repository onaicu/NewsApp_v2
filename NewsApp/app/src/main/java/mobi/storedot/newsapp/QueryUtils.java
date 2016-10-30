package mobi.storedot.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from Guardian API.
 */

public final class QueryUtils {


    /**
     * Helper methods related to requesting and receiving
     * news data from Guardian API).
     */

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * Suppressing the constructor; however,  also prevent internal instantiation
     * by throwing an assertionerror.
     */

    private QueryUtils() {
        throw new AssertionError("No QueryUtils instances for you!");
    }

    /**
     * Query the Guardian dataset and return a list of {@link Story} objects.
     */

    public static List<Story> fetchStoryData(String requestUrl) {
        /**
         * forcing the background thread to pause execution and wait for 2 seconds (which is 2000 milliseconds),
         * before proceeding to execute the rest of lines of code in this method.
         */

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        /** Perform HTTP request to the URL and receive a JSON response back*/

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        /** Extract relevant fields from the JSON response and create a list of {@link Story}s*/

        List<Story> news = extractItemsFromJson(jsonResponse);

        /** Return the list of {@link Story}s*/
        return news;
    }

    /**
     *Returns new URL object from the given string URL.
     */

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    private static String makeHttpRequest(URL url) throws
            IOException {String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection)
                    url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds */);

            urlConnection.setConnectTimeout(15000 /*milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200)
            // then read the input stream and parse the response.

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " +
                        urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the story JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                /** Closing the input stream could throw an IOException, which is why
                 the makeHttpRequest(URL url) method signature specifies than an IOException could be thrown*/
                inputStream.close();
            }

        } return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
     */

    private static String readFromStream(InputStream
                                                 inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new
                    InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new
                    BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    /**Return a list of {@link Story} objects that has been built up from
     * parsing the given JSON response */

    private static List<Story> extractItemsFromJson(String storyJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(storyJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<Story> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON is formatted,
        // a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {
            // Create a JSONObject from the JSON response string

            JSONObject baseJsonResponse = new JSONObject(storyJSON);

            /**Extract the JSONObject associated with the key called "response"
                    */
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // For a given story, extract the JSONArray associated with the
            // key called "results", which represents a list of all info for that story.

            JSONArray results = response.getJSONArray("results");

            for(int i = 0 ; i < results.length() ; i++){

                // Get a single story at position i within the list of news
                JSONObject currentStory = results.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String section = currentStory.optString("sectionName");

                // Extract the value for the key called "webTitle"
                String title = currentStory.optString("webTitle");

                // Extract the value for the key called "webUrl"
                String url = currentStory.optString("webUrl");

                // Create a new {@link Story} object with the webtitle, section Name,
                // and url from the JSON response.
                Story story = new Story(section, title, url);

                // Add the new {@link Story} to the list of news.
                news.add(story);
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of news
        return news;
    }

}
