package livewind.example.andro.liveWind.Notifications;

/**
 * Created by JGJ on 25/04/19.
 * New content handling - when notification with special code is received from firebase cloud
 * This object will be created and showed by {@MyFirebaseMessagingService} {@showNewContentNotification} method on CatalogActivity
 */
public class NewContentNotification {
    private String id;
    private String title;
    private String description;
    private String actionTitle;
    private String actionLink;
    private String date;

    /**
     * Firebase NewContentNotification keys
     */
     public final class NewContentNotificationEntry {
         //Contract firebase access constants
        public static final String NEW_CONTENT_TITLE = "title";
        public static final String NEW_CONTENT_DESCRIPTION = "description";
        public static final String NEW_CONTENT_ACTION_TITLE = "actionTitle";
        public static final String NEW_CONTENT_ACTION_LINK = "actionLink";
        public static final String NEW_CONTENT_DATE = "date";

        //Topics names
        public static final String NEW_CONTENT_TOPIC_POLISH = "new_content_polish";
        public static final String NEW_CONTENT_TOPIC_ENGLISH = "new_content_english";

    }

    public NewContentNotification(){}

    public NewContentNotification(String title, String description, String actionTitle, String actionLink, String date){
        this.title=title;
        this.description=description;
        this.actionTitle=actionTitle;
        this.actionLink=actionLink;
        this.date=date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActionLink(String actionLink) {
        this.actionLink = actionLink;
    }

    public void setActionTitle(String actionTitle) {
        this.actionTitle = actionTitle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getActionLink() {
        return actionLink;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
