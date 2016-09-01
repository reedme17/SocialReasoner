package wef.articulab.model.recommendation.participant;

import org.json.JSONArray;
import org.json.JSONObject;
import wef.articulab.control.util.Utils;
import wef.articulab.model.recommendation.RecommendationInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 4/11/16.
 */
public class Participant implements RecommendationInterface {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String mobilePhone;
    private String phone;
    private String profile;
    private String countryOfNationality;
    private String[][] socialAccounts;
    private String publicFigure;
    private String organizationId;
    private String organizationName;
    private String organizationType;
    private String topLevelOrganizationId;
    private String position_title;
    private List<ForumNetwork> forumNetworks;
    private String thumbnailUrl;
    private String photoUrl;
    private List<Contribution> contributions;
    private Boolean inBooklet;
    private Integer revision;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
        this.lastName = (String)lastName;
    }

    public String getCountryOfNationality() {
        return countryOfNationality;
    }

    public void setCountryOfNationality(Object countryOfNationality) {
        this.countryOfNationality = (String)countryOfNationality;
    }

    public String getPublicFigure() {
        return publicFigure;
    }

    public void setPublicFigure(Object publicFigure) {
        this.publicFigure = (String)publicFigure;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(Object organizationName) {
        this.organizationName = (String)organizationName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(Object profile) {
        this.profile = (String)profile;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(Object fullName) {
        this.fullName = (String)fullName;
    }

    public String getPosition_title() {
        return position_title;
    }

    public void setPosition_title(Object position_title) {
        this.position_title = (String)position_title;
    }

    public boolean isInBooklet() {
        return inBooklet;
    }

    public void setInBooklet(Object inBooklet) {
        this.inBooklet = (Boolean)inBooklet;
    }

    public String getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = (String)id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = (String)firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = (String)email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(Object mobilePhone) {
        this.mobilePhone = (String)mobilePhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = (String)phone;
    }

    public String[][] getSocialAccounts() {
        return socialAccounts;
    }

    public void setSocialAccounts(String[][] socialAccounts) {
        this.socialAccounts = (String[][])socialAccounts;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Object organizationId) {
        this.organizationId = (String)organizationId;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Object organizationType) {
        this.organizationType = (String)organizationType;
    }

    public String getTopLevelOrganizationId() {
        return topLevelOrganizationId;
    }

    public void setTopLevelOrganizationId(Object topLevelOrganizationId) {
        this.topLevelOrganizationId = (String)topLevelOrganizationId;
    }

    public List<ForumNetwork> getForumNetworks() {
        return forumNetworks;
    }

    public void setForumNetworks(List<ForumNetwork> forumNetworks) {
        this.forumNetworks = forumNetworks;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(Object thumbnailUrl) {
        this.thumbnailUrl = (String)thumbnailUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Object photoUrl) {
        this.photoUrl = (String)photoUrl;
    }

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public Boolean getInBooklet() {
        return inBooklet;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Object revision) {
        this.revision = (Integer)revision;
    }

    public static Participant parse(JSONObject object){
        try {
            Participant participant = new Participant();
            participant.setId(object.get("id"));
            participant.setFirstName(object.get("firstName"));
            participant.setLastName(object.get("lastName"));
            participant.setFullName(object.get("fullName"));
            participant.setProfile(object.get("profile"));
            participant.setCountryOfNationality(object.get("countryOfNationality"));
            try{
                participant.setEmail( object.get("email") );
                participant.setMobilePhone(object.get("mobilePhone") );
                participant.setPhone(object.get("phone"));
            }catch (Exception e){}

            //socialAccounts
//            JSONObject socialAccountsJson = object.getJSONObject("socialAccounts");
//            String[][] socialAccounts = new String[socialAccountsJson.length()][2];
//            Iterator ite = socialAccountsJson.keys();
//            int pos = 0;
//            while( ite.hasNext() ){
//                socialAccounts[pos][0] = (String) ite.next();
//                socialAccounts[pos][1] = socialAccountsJson.getString( socialAccounts[pos][0] );
//                pos++;
//            }
//            participant.setSocialAccounts(socialAccounts);
            participant.setPublicFigure(object.get("publicFigure"));
            participant.setOrganizationId(object.get("organizationId"));
            participant.setOrganizationName(object.get("organizationName"));
            participant.setOrganizationType(object.get("organizationType"));
            participant.setTopLevelOrganizationId(object.get("topLevelOrganizationId"));
            participant.setPosition_title(object.get("position_title"));

            //forumNetworks
            JSONArray forumNetworksJson = object.getJSONArray("forumNetworks");
            List<ForumNetwork> forumNetworks = new ArrayList<>();
            int size = forumNetworksJson.length();
            for( int i = 0; i < size; i++ ){
                JSONObject forumNetJson = forumNetworksJson.getJSONObject(i);
                ForumNetwork forumNetwork = new ForumNetwork();
                forumNetwork.setForumCommunity( forumNetJson.get("forumCommunity"));
                forumNetwork.setNetwork(forumNetJson.get("network"));
                forumNetworks.add( forumNetwork );
            }
            participant.setForumNetworks( forumNetworks );
            participant.setThumbnailUrl(object.get("thumbnailUrl"));
            participant.setPhotoUrl(object.get("photoUrl"));
            participant.setInBooklet(object.get("inBooklet"));

            //contributions
            JSONArray contributionsJson = object.getJSONArray("contributions");
            List<Contribution> contributions = new ArrayList<>();
            size = contributionsJson.length();
            for( int i = 0; i < size; i++ ){
                JSONObject contributionJson = contributionsJson.getJSONObject(i);
                Contribution contribution = new Contribution();
                contribution.setSessionId(contributionJson.getString("sessionId"));
                contribution.setType(contributionJson.getString("type"));
                contributions.add( contribution );
            }
            participant.setContributions( contributions );
//            participant.setRevision(object.get("revision"));
            return participant;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Participant> parse(JSONArray peopleArray){
        List<Participant> people = new ArrayList<>();
        try {
            int size = peopleArray.length();
            for (int i = 0; i < size; i++) {
//                JSONObject peopleJson = peopleArray.getJSONObject(i);
//                people.add(parse(peopleJson));
                String peopleJson = peopleArray.getString(i);
                people.add( Utils.fromJsonString(peopleJson, Participant.class));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return people;
        }
    }
}
