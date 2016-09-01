package wef.articulab.model.recommendation.topic;

import org.json.JSONObject;
import wef.articulab.control.util.Utils;

/**
 * Created by oscarr on 5/4/16.
 */
public class Topic {
    private String id;
    private String title;
    private String subtitle;
    private String description;
    private String photoUrl;
    private String thumbnailUrl;
    private long revision;

    public String getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = (String)id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(Object title) {
        this.title = (String)title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Object subtitle) {
        this.subtitle = (String)subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = (String)description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Object photoUrl) {
        this.photoUrl = (String)photoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(Object thumbnailUrl) {
        this.thumbnailUrl = (String)thumbnailUrl;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(Object revision) {
        this.revision = (Long)revision;
    }

    public static Topic parse(JSONObject object) {
        try {
            Topic topic = new Topic();
            topic.setId(object.get("id"));
            topic.setTitle(object.get("title"));
            topic.setSubtitle(object.get("subtitle"));
            topic.setDescription(object.get("description"));
            topic.setPhotoUrl(object.get("photoUrl"));
            topic.setThumbnailUrl(object.get("thumbnailUrl"));
            topic.setRevision(object.get("revision"));
            return topic;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
