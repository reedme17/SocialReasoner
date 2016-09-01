package wef.articulab.control.reasoners;

/**
 * Created by Ting on 8/15/16.
 */
public class UserModel {

    public String firstName;
    public String lastName;

    //maybe unnecessary
    public String title;
    public String affiliation;
    public String nationality;

    public String interest;         //每一次问你想干什么的时候set
    public String interestPerson;
    public String workField;        //在一开始问你的工作/领域的时候set


    public String foodType;
    public String foodDistance;

    private static UserModel userModel;

    public UserModel() {}

    public static UserModel getInstance(){
        if(userModel == null){
            userModel = new UserModel();
        }
        return userModel;
    }

    public static UserModel getUserModel() {
        return userModel;
    }

    public String getFirstName(){
        return this.firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getLastName(){
        return this.lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getAffiliation(){
        return this.affiliation;
    }
    public void setAffiliation(String affiliation){
        this.affiliation = affiliation;
    }
    public String getNationality(){
        return this.nationality;
    }
    public String getInterest() {
        return interest;
    }
    public void setInterest(String interest) {
        this.interest = interest;
    }
    public void setNationality(String nationality){
        this.nationality = nationality;
    }
    public String getWorkField(){
        return this.workField;
    }
    public void setWorkField(String workField){
        this.workField = workField;
    }
    public String getFoodType() {
        return foodType;
    }
    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }
    public String getFoodDistance() {
        return foodDistance;
    }
    public void setFoodDistance(String foodDistance) {
        this.foodDistance = foodDistance;
    }
    public String getInterestPerson() {
        return interestPerson;
    }
    public void setInterestPerson(String interestPerson) {
        this.interestPerson = interestPerson;
    }


}
