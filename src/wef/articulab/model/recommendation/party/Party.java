package wef.articulab.model.recommendation.party;

import org.json.JSONArray;
import org.json.JSONObject;
import wef.articulab.control.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by oscarr on 6/2/16.
 */
public class Party {
    private String type;
    private String id;
    private String startDatetimeString;
    private String endDatetimeString;
    private String address;
    private String description;
    private String keywords;
    private Date startDatetime;
    private Date endDatetime;

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Date getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Date endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDatetimeString() {
        return startDatetimeString;
    }

    public void setStartDatetimeString(String startDatetimeString) {
        this.startDatetimeString = startDatetimeString;
    }

    public String getEndDatetimeString() {
        return endDatetimeString;
    }

    public void setEndDatetimeString(String endDatetimeString) {
        this.endDatetimeString = endDatetimeString;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }


    public static Party parse(JSONObject partyObject){
        Party party = new Party();
        try {
            party.setId(partyObject.getString("id"));
            party.setType(partyObject.getString("type"));
            party.setKeywords(partyObject.getString("keywords"));
            party.setDescription(partyObject.getString("description"));
            party.setAddress(partyObject.getString("address"));
            party.setStartDatetime(Utils.getDate(partyObject.getString("startDatetime")));
            party.setStartDatetimeString(partyObject.getString("startDatetime"));
            party.setEndDatetime(Utils.getDate(partyObject.getString("endDatetime")));
            party.setEndDatetimeString(partyObject.getString("endDatetime"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return party;
    }

    public static List<Party> parse(JSONArray partyArray){
        List<Party> parties = new ArrayList<>();
        try {
            int size = partyArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject partyJson = partyArray.getJSONObject(i);
                parties.add(parse(partyJson));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return parties;
        }
    }
}
