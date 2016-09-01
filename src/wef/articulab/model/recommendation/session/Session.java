package wef.articulab.model.recommendation.session;


import org.json.JSONArray;
import org.json.JSONObject;
import wef.articulab.control.util.Utils;
import wef.articulab.model.recommendation.RecommendationInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by oscarr on 4/20/16.
 */
public class Session implements RecommendationInterface{

    private String id;
    private String type;
    private String keyword;
    private String description;
    private String fee;
    private String currencyIsoCode;
    private Boolean spouseAllowed;
    private Boolean pressAllowed;
    private Boolean withMeal;
    private Boolean signupRequired;
    private Boolean cancelled;
    private Integer capacity;
    private String roomId;
    private String votingUrl;
    private String feedbackUrl;
    private String programmeName;
    private Date startDatetime;
    private Date endDatetime;
    private String startDatetimeString;
    private String endDatetimeString;
    private String name;
    private String number;
    private String title;
    private String sessionStructure;
    private Integer preparatoryDiscussionDurationInMinutes;
    private String webcastUrl;
    private String statusUrl;
    private Boolean showAttendees;
    private Integer revision;
    private String thumbnail;
    private String photo;
    private List<String> topics;
    private List<Contributor> contributors;
    private List<String> tracks;
    private List<String> insights;

    public String getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = (String)id;
    }

    public String getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = (String)type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(Object keyword) {
        this.keyword = (String)keyword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = (String)description;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(Object fee) {
        this.fee = (String)fee;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(Object currencyIsoCode) {
        this.currencyIsoCode = (String)currencyIsoCode;
    }

    public Boolean getSpouseAllowed() {
        return spouseAllowed;
    }

    public void setSpouseAllowed(Object spouseAllowed) {
        this.spouseAllowed = (Boolean)spouseAllowed;
    }

    public Boolean getPressAllowed() {
        return pressAllowed;
    }

    public void setPressAllowed(Object pressAllowed) {
        this.pressAllowed = (Boolean)pressAllowed;
    }

    public Boolean getWithMeal() {
        return withMeal;
    }

    public void setWithMeal(Object withMeal) {
        this.withMeal = (Boolean)withMeal;
    }

    public Boolean getSignupRequired() {
        return signupRequired;
    }

    public void setSignupRequired(Object signupRequired) {
        this.signupRequired = (Boolean)signupRequired;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Object cancelled) {
        this.cancelled = (Boolean)cancelled;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Object capacity) {
        this.capacity = (Integer)capacity;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(Object roomId) {
        this.roomId = (String)roomId;
    }

    public String getVotingUrl() {
        return votingUrl;
    }

    public void setVotingUrl(Object votingUrl) {
        this.votingUrl = (String)votingUrl;
    }

    public String getFeedbackUrl() {
        return feedbackUrl;
    }

    public void setFeedbackUrl(Object feedbackUrl) {
        this.feedbackUrl = (String)feedbackUrl;
    }

    public String getProgrammeName() {
        return programmeName;
    }

    public void setProgrammeName(Object programmeName) {
        this.programmeName = (String)programmeName;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Object startDatetime) {
        this.startDatetime = new Date((String)startDatetime);
    }

    public Date getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Object endDatetime) {
        this.endDatetime = new Date((String)endDatetime);
    }

    public String getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = (String)name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(Object number) {
        this.number = (String)number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(Object title) {
        this.title = (String)title;
    }

    public String getSessionStructure() {
        return sessionStructure;
    }

    public void setSessionStructure(Object sessionStructure) {
        this.sessionStructure = (String)sessionStructure;
    }

    public Integer getPreparatoryDiscussionDurationInMinutes() {
        return preparatoryDiscussionDurationInMinutes;
    }

    public void setPreparatoryDiscussionDurationInMinutes(Object preparatoryDiscussionDurationInMinutes) {
        this.preparatoryDiscussionDurationInMinutes = (Integer)preparatoryDiscussionDurationInMinutes;
    }

    public String getWebcastUrl() {
        return webcastUrl;
    }

    public void setWebcastUrl(Object webcastUrl) {
        this.webcastUrl = (String)webcastUrl;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(Object statusUrl) {
        this.statusUrl = (String)statusUrl;
    }

    public Boolean getShowAttendees() {
        return showAttendees;
    }

    public void setShowAttendees(Object showAttendees) {
        this.showAttendees = (Boolean)showAttendees;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Object revision) {
        this.revision = (Integer)revision;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Object thumbnail) {
        this.thumbnail = (String)thumbnail;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(Object photo) {
        this.photo = (String)photo;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }

    public List<String> getInsights() {
        return insights;
    }

    public void setInsights(List<String> insights) {
        this.insights = insights;
    }

    public String getStartDatetimeString() {
        return startDatetimeString;
    }

    public void setStartDatetimeString(Object startDatetimeString) {
        this.startDatetimeString = (String)startDatetimeString;
    }

    public String getEndDatetimeString() {
        return endDatetimeString;
    }

    public void setEndDatetimeString(Object endDatetimeString) {
        this.endDatetimeString = (String)endDatetimeString;
    }

    public static Session parse(JSONObject object){
        try{
            Session session = new Session();
            session.setId( object.get("id"));
            session.setType(object.get("type"));
            session.setKeyword(object.get("keyword"));
            session.setDescription(object.get("description"));
            session.setCurrencyIsoCode(object.get("currencyIsoCode"));
            session.setSpouseAllowed(object.get("spouseAllowed"));
            session.setPressAllowed(object.get("pressAllowed"));
            session.setWithMeal(object.get("withMeal"));
            session.setSignupRequired(object.get("signupRequired"));
            session.setCancelled(object.get("cancelled"));
            session.setCapacity(object.get("capacity"));
            session.setRoomId(object.get("roomId"));
            session.setProgrammeName(object.get("programmeName"));
            session.setStartDatetime(object.get("startDatetime"));
            session.setStartDatetimeString(object.get("startDatetime"));
            session.setEndDatetime(object.get("endDatetime"));
            session.setEndDatetimeString(object.get("endDatetime"));
            session.setName(object.get("name"));
            session.setTitle(object.get("title"));
            session.setPreparatoryDiscussionDurationInMinutes(object.get("preparatoryDiscussionDurationInMinutes"));
            session.setStatusUrl(object.get("statusUrl"));
            session.setShowAttendees(object.get("showAttendees"));
//            session.setRevision(Long.valueOf(object.get("revision");

            //insights
            JSONArray insightsJson = object.getJSONArray("insights");
            List<String> insights = new ArrayList<>();
            int size = insightsJson.length();
            for( int i = 0; i < size; i++ ){
                JSONObject insightJson = insightsJson.getJSONObject(i);
                insights.add( insightJson.getString("id") );
            }
            session.setInsights(insights);

            //topics
            JSONArray topicsJson = object.getJSONArray("topics");
            List<String> topics = new ArrayList<>();
            size = topicsJson.length();
            for( int i = 0; i < size; i++ ){
                topics.add( topicsJson.getString(i) );
            }
            session.setTopics(topics);


            //tracks
            JSONArray tracksJson = object.getJSONArray("tracks");
            List<String> tracks = new ArrayList<>();
            size = tracksJson.length();
            for( int i = 0; i < size; i++ ){
                tracks.add( tracksJson.getString(i) );
            }
            session.setTracks(tracks);

            //contributors
            JSONArray contributorsJson = object.getJSONArray("contributors");
            List<Contributor> contributors = new ArrayList<>();
            size = contributorsJson.length();
            for( int i = 0; i < size; i++ ){
                JSONObject contributorJson = contributorsJson.getJSONObject(i);
                Contributor contributor = new Contributor();
                contributor.setParticipantId( contributorJson.getString("participantId") );
                contributor.setType(contributorJson.getString("type"));
                contributors.add( contributor );
            }
            session.setContributors(contributors);

            return session;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Session> parse(JSONArray sessionsArray){
        List<Session> sessions = new ArrayList<>();
        try {
            int size = sessionsArray.length();
            for (int i = 0; i < size; i++) {
//                JSONObject sessionJson = sessionsArray.getJSONObject(i);
//                sessions.add(parse(sessionJson));
                String sessionJson = sessionsArray.getString(i);
                sessions.add( Utils.fromJsonString(sessionJson, Session.class) );
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return sessions;
        }
    }
}
