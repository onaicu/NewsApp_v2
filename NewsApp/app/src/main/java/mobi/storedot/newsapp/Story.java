package mobi.storedot.newsapp;

/**
 * An {@link Story} object contains information related to a single Story.
 */

public class Story {

    /**
     * Section name of the story
     */
    private String mSection;

    /**
     * Title of the story
     */
    private String mTitle;

    /**
     * Website URL of the story
     */
    private String mUrl;

    /**
     * Constructs a new {@link Story} object.
     *
     * @param section  is the name of the section to which the article/story belong to
     * @param title  is the given title of the story
     * @param url    is the website URL to find more details about the story
     */
    public Story (String section, String title, String url) {
        mSection = section;
        mTitle = title;
        mUrl = url;
    }

    /**
     * Returns the section name of the story
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Returns the title of the story.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the website URL to find more information about the Story.
     */
    public String getUrl() {
        return mUrl;
    }
}