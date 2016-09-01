package wef.articulab.model.recommendation.food;

import org.json.JSONArray;
import org.json.JSONObject;
import wef.articulab.control.util.Utils;
import wef.articulab.model.recommendation.RecommendationInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 6/2/16.
 */
public class Food implements RecommendationInterface {

    private String address;
    private String image;
    private String keywords;
    private String name;
    private String type;
    private String id;
    private String phone;
    private String rating;
    private String distance;

    public void setDistance(String _distance) {
        this.distance = _distance;
    }

    public String getDistance() {
        return this.distance;
    }

    public void setPhone(String _phone) {
        phone = _phone;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setRating(String _rating) {
        this.rating = _rating;
    }

    public String getRating() {
        return this.rating;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getId() {
        return this.id;
    }

    public void setAddress(String _address) {
        this.address = _address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setImage(String _image) {
        this.image = _image;
    }

    public String getImage() {
        return this.image;
    }

    public void setKeywords(String _keywords) {
        this.keywords = _keywords;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getName() {
        return this.name;
    }

    public void setType(String _type) {
        this.type = _type;
    }

    public String getType() {
        return this.type;
    }


    public static Food parse(JSONObject object) {

        try {
            Food food = new Food();
            food.setAddress((String) Utils.getJsonProperty(object, "address", String.class));
            food.setKeywords((String) Utils.getJsonProperty(object, "keywords", String.class));
            food.setImage((String) Utils.getJsonProperty(object, "image", String.class));
            food.setName((String) Utils.getJsonProperty(object, "name", String.class));
            food.setType((String) Utils.getJsonProperty(object, "type", String.class));
            food.setId((String) Utils.getJsonProperty(object, "id", String.class));
            food.setRating((String) Utils.getJsonProperty(object, "rating", String.class));
            food.setPhone((String) Utils.getJsonProperty(object, "phone", String.class));
            food.setDistance((String) Utils.getJsonProperty(object, "distance", String.class));
            return food;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<Food> parse(JSONArray foodArray) {
        List<Food> foods = new ArrayList<>();
        try {
            int size = foodArray.length();

            for (int i = 0; i < size; i++) {
                JSONObject foodJson = foodArray.getJSONObject(i);
                foods.add(parse(foodJson));
            }
            return foods;
        } catch (Exception e) {
            e.printStackTrace();
            return foods;
        } finally {
            ;
        }
    }
}
