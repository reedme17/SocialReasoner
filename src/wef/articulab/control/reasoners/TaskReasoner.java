package wef.articulab.control.reasoners;

import org.json.JSONArray;
import wef.articulab.control.DMmain;
import wef.articulab.control.util.Utils;
import wef.articulab.control.vht.VHTConnector;
import wef.articulab.model.Constants;
import wef.articulab.model.blackboard.Blackboard;
import wef.articulab.model.nlu.*;
import wef.articulab.model.recommendation.RecommendationInterface;
import wef.articulab.model.recommendation.food.Food;
import wef.articulab.model.recommendation.participant.Participant;
import wef.articulab.model.recommendation.party.Party;
import wef.articulab.model.recommendation.session.Session;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oscarr on 5/31/16.
 */

public class TaskReasoner {
    private Model model;
    private Lock lock;
    private String input = "";
    private DMmain DMmain;
    private Blackboard blackboard;
    private Condition modelModifiedByTR;
    public static String inputFromUser;
    private VHTConnector vhtConnector;
    private HashMap<String, RecommendationInterface> alreadyRecommended;
    private List<State> systemIntentsCollection;
    private static TaskReasoner instance;
    private double confidenceThreshold;
    private int indexGoals = 0;
    private boolean isMultigoal = false;
    private List<Intent> goals;
    public List<RecommendationInterface> results;
    private boolean shouldWaitForRecomResults = false;
    private Scanner sc = new Scanner( System.in );

    // recommendations:
    private List<Session> sessions;
    private List<Participant> people;
    private List<Food> food;
    private List<Party> parties;
    private boolean hasUserSaidHisGoals;
    private boolean recommendAgain = true;
    private boolean flagPrintOutUserIntent = false;
    private HashMap<String, Value> likes;                       //likes
    private HashMap<String, String> work_interest;              //work_interest
    private HashMap<String, String> food_type;                  //food_type
    private HashMap<String, String> food_distance;              //food_distance
    private HashMap<String, Value> dislikes;
    private HashMap<String, String> selfNaming;                 //selfnaming
    private HashMap<String, String> interestPerson;             //interestPerson
    public String interestPersonName;
    private HashMap<String, String> interest;                   //想要推荐的interest
    private boolean shouldCheckWaitForResults = true;
    private List resultsRecommendation;
    private int breakLoopCount;
    private boolean shouldIncreaseGoalIdx;
    private String lastIntent = "";
    private double[] updatedScore = new double[1];      //8 is an important number.
    private String query;
    public double highScore;
    public int counter = 0;
    public int counterSession = 0;

    private TaskReasoner(Lock lock, Condition modelModifiedByTR, DMmain DMmain) {
        this.model = Model.createNodes();
        this.model.current = model.get( Constants.START );
        this.lock = lock;
        this.modelModifiedByTR = modelModifiedByTR;
        this.DMmain = DMmain;
        blackboard = Blackboard.getInstance();
        alreadyRecommended = new HashMap<>();
        systemIntentsCollection = new ArrayList<>();
        confidenceThreshold = Double.valueOf(DMmain.properties.getProperty("confidenceThreshold"));
        goals = new ArrayList<>();
        likes = new HashMap<>();
        work_interest = new HashMap<>();
        dislikes = new HashMap<>();
        interest = new HashMap<>();
        selfNaming = new HashMap<>();
        food_type = new HashMap<>();
        food_distance = new HashMap<>();
        interestPerson = new HashMap<>();
    }

    public static TaskReasoner getInstance(Lock lock, Condition modelModifiedByTR, DMmain DMmain) {
        if( instance == null ){
            instance = new TaskReasoner( lock, modelModifiedByTR, DMmain);
        }
        return instance;
    }

    public static TaskReasoner getInstance(){
        return instance;
    }

    public void setVhtConnector(){
        vhtConnector = VHTConnector.getInstance();
    }

    public static void reset(){
        instance.alreadyRecommended.clear();
        instance.blackboard.removeMessages( Constants.REMOVE_ALL );
        instance.likes.clear();
        instance.dislikes.clear();
        instance.systemIntentsCollection.clear();
        instance.indexGoals = 0;
        instance.isMultigoal = false;
        if( instance.goals != null ) {
            instance.goals.clear();
        }
        if( instance.results != null ) {
            instance.results.clear();
        }
        instance.shouldWaitForRecomResults = false;
        instance = null;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }



    /**
     * 将user intent, 即参数, 转换为system intent, 即model.current, 用到reasonRecursively
     * @param intent
     * @return
     */
    public State reason(String intent) {
        if( model.current != null ) {
            model.storeInHistory(intent);
            model.current = reasonRecursively(intent);
        }
        return model.current;
    }                                               //其实是找下一个state,intent是刺激
    private State reasonRecursively(String intent){     //about backup_word。 生成system intent
        State result;
        while( model.current.name.equals(Constants.BACKUP_WORD) || model.current.name.equals(Constants.ASK_FOR_YES_OR_NO) ||
                model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_1P) ||
                        model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_1P) ||
                        model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_MP) ||
                        model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_MP) ||
                        model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_1S) ||
                        model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_1S) ||
                        model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_MS) ||
                        model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_MS)){     //model.current是fsm里面的199个state
            model.current = model.current.previousState;
            model.current.previousState = (model.current.previousState).previousState;

            model.current = model.current.previousState;
        }




                                                 //返回的是当前state的next里,intention等于intent的的那个state
        String validated = model.current.validate(intent);     //或者match_any_intent的state
        if( validated != null && !validated.equals( Constants.RESET )){
            result = model.get(validated);              //result是上面得到的next对应的state, validated是state的name, result是state格式的state
            result.previousState = model.current;
            return result;                          //下一个
        }
        result = model.get(intent);         //返回名字叫做intent的state
        if( result == null ){
            String newIntent = model.extractSystemIntent(intent, false);    //输入是backup_word,返回null?
            result = model.get(newIntent);
            if( result == null ){
                result = model.get(model.extractSystemIntent(newIntent, false));
                if( result == null ) {
                    result = model.get(model.extractSystemIntent( Constants.BACKUP_WORD, false));
                }
            }
            if( result != null){
                result.previousState = model.current;
                return result;
            }
        }
        if( Constants.BACKUP_WORD.equals(intent) ){
            if( result == null ) {
                result = model.get(Constants.BACKUP_WORD);
                result.name = model.current.name;
            }
            result.phase = model.current.phase;     //result复制了一个current,在current的后面
            result.previousState = model.current;
            result.nextTransitionsStates = model.current.nextTransitionsStates;
            return result;
        }
        if( result != null && ( (!DMmain.useTRNotWoZ && DMmain.useWoZFlag) || model.current.contains(intent)
                || model.getPhaseStates().contains(intent) || model.current.contains(Constants.BACKUP_WORD)) ){
            if(model.current.phase != null && ( result.changePhase || result.phase == null) ){
                result.phase = model.current.phase;
                result.changePhase = true;
            }
            if( result.nextTransitionsStates == null ){
                model.current = result;
                result = reasonRecursively(result.name);
            }
            return result;
        }

        if( model.current.name.equals("do_attendance_elicitation") &&
                !((Constants.POSITIVE_CONFIRMATION).equals(intent) || (Constants.NEGATIVE_CONFIRMATION).equals(intent))) {
            model.current = model.get("start_goal_elicitation");
            result = model.current;
            return result;
        }
        return reasonRecursively(Constants.BACKUP_WORD);
    }



    /**
     * 在线程中运行
     */
    public void executeTaskReasoner() {
        if(model.current != null) {
            if (model.current.typeNextIntent.equals(Constants.USER_INTENT)) {
                input = processUserIntent();        //input = input
                if( DMmain.flagReset ){
                    DMmain.flagResetTR = true;
                    return;
                }
                if (Constants.STOP.equals(input)) {
                    DMmain.flagStop = true;
                    return;
                }
            } else {        //ignore
                String temp = extractIntent(input);
                if (temp.equals(input)) {
                    input = model.extractSystemIntent(model.currentIntent, true);
                } else {
                    input = temp;
                }
            }
            if( DMmain.useWoZFlag == false) {
                processIntent();            //system intent comes from here
            }else{
                DMmain.useWoZFlag = false;
            }
        }
    }

    public void processIntent() {
        lock.lock();
        try {
            while( DMmain.queue.size() > 0 ){
                modelModifiedByTR.await();
            }
            if( input.contains("multiple_goal") ){
                System.currentTimeMillis();
            }
            if( DMmain.useWoZFlag ){        //ignore
                State temp = model.current;
                model.current = model.get(inputFromUser);
                if( model.current == null ){
                    model.current = temp;
                }else{
                    model.current.previousState = temp;
                }
            }else{                                                      //执行这个
                checkSendMessageTopLink();                              //执行这个
                input = extractIntent( input );                         //执行这个 //same
                input = getUserIntentFromGoal( input );                 //执行这个 //基本还是user intent, 只是1_goal, multiple_goal
                model.current = reason( input );                        //执行这个      //model.current是一个state, system intent
            }                                                           //执行这个

            if (model.current != null) {
                addIntentToQueue();
            } else {
                System.out.println("There are no more states....");
            }
            DMmain.checkSyncSequence(Constants.SYNC_AFTER_TR_DECISION);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    public void addIntentToQueue() {
        DMmain.queue.add(model.current);
        blackboard.removeStatesAndPhases(model.getStates());
        blackboard.setStatesString(model.current.name + (model.current.phase != null ? ":" +
                model.current.phase : ""), "DMmain");
    }

    private void checkSendMessageTopLink() {
        Pattern pattern = Pattern.compile("end_[a-z]*_recommendation_[a-z0-9]*_goal_[a-z0-9]*_time_yes");
        Matcher matcher = pattern.matcher( input );
        if( matcher.matches() && results != null && !results.isEmpty() ){
            vhtConnector.sendMessageToTopLink( results.get(0) );
        }
    }

    private String processUserIntent() {
        while ( DMmain.noInputFlag && !DMmain.flagReset ) {
            if( DMmain.useManualMode) {     //false
                inputFromUser = sc.nextLine();
                inputFromUser = validateInputFromScanner();
                DMmain.addToLog( "User intent: " + inputFromUser );
                createDummyGoals(inputFromUser);
                DMmain.noInputFlag = false;
            }else {
                if (!model.scenarios.isEmpty()) {
                    inputFromUser = model.scenarios.remove(0);
                    DMmain.addToLog( "User intent: " + inputFromUser );
                    createDummyGoals(inputFromUser);
                    DMmain.noInputFlag = false;
                }
            }
            Utils.sleep(DMmain.delayUserIntent);
        }
        if( DMmain.flagReset ){
            return null;
        }
        input = inputFromUser;
        while( !flagPrintOutUserIntent ){
            Utils.sleep( 50 );
        }
        flagPrintOutUserIntent = false;
        DMmain.noInputFlag = true;
        return input;
    }

    private String validateInputFromScanner() {
        if( inputFromUser.equals("s") ){
            return Constants.SELF_NAMING;
        }
        if( inputFromUser.equals("p") ){
            return Constants.POSITIVE_CONFIRMATION;
        }
        if( inputFromUser.equals("n") ){
            return Constants.NEGATIVE_CONFIRMATION;
        }
        if( inputFromUser.equals("m") ){
            return Constants.MULTIPLE_GOALS;
        }
        if( inputFromUser.equals("rs") ){
            return Constants.REQUEST_SESSION_RECOMMENDATION;
        }
        if( inputFromUser.equals("rp") ){
            return Constants.REQUEST_PERSON_RECOMMENDATION;
        }
        if( inputFromUser.equals("rf") ){
            return Constants.REQUEST_FOOD_RECOMMENDATION;
        }
        return inputFromUser;
    }

    public State processSystemIntents(){
        State state = DMmain.queue.poll();
        if( !state.internalValidation ){
            state.conversationalStrategy = DMmain.conversationalStrategies;
            systemIntentsCollection.add( state.clone() );
            if( state.name.contains( "food" ) ){
                System.currentTimeMillis();
            }
        }
        if( shouldCheckWaitForResults ) {
            resultsRecommendation = checkWaitForResults(systemIntentsCollection);
            if( !resultsRecommendation.isEmpty() ) {
                shouldCheckWaitForResults = false;
            }
        }
        if( state.typeNextIntent.equals(Constants.USER_INTENT) ){
            sendSystemIntents();
        }



        DMmain.addToLog( "System intent: " + state.name + " phase: " + state.phase + " strategy: " +
                Arrays.toString(DMmain.conversationalStrategies) );
        return model.current;
    }



    /**
     * 这个method之前整合在上一个method里
     */
    public void sendSystemIntents() {
        if( !systemIntentsCollection.isEmpty() ) {
            if (DMmain.useVHTConnnector && DMmain.useTRNotWoZ && DMmain.sendMessagesToNLG) {
                vhtConnector.sendToNLG(systemIntentsCollection, resultsRecommendation);
                System.err.println("Sending message to NLG: " + Arrays.toString(systemIntentsCollection.toArray()));
//                System.err.println(VHTConnector.userModel.firstName);
//                System.err.println(VHTConnector.userModel.lastName);
//                System.err.println(VHTConnector.userModel.workField);
//                System.err.println(query);

                if( (Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_session_recommendation_1_goal_1st_time")
                        || (Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_session_recommendation_1_goal_2nd_time")
                    || (Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_person_recommendation_1_goal_1st_time")
                    ||(Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_person_recommendation_1_goal_2nd_time")
                        || (Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_session_recommendation_multiple_goal_1st_time")
                        || (Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_session_recommendation_multiple_goal_2nd_time")
                        || (Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_person_recommendation_multiple_goal_1st_time")
                        ||(Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_person_recommendation_multiple_goal_2nd_time")
                        ||(Arrays.toString(systemIntentsCollection.toArray())).contains("feedback_start_food_recommendation_1_goal")
                        ||(Arrays.toString(systemIntentsCollection.toArray())).contains("feedback_start_food_recommendation_multiple_goal")
                        ||(Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_food_recommendation_1_goal")
                        ||(Arrays.toString(systemIntentsCollection.toArray())).contains("elicit_feedback_food_recommendation_multiple_goal")) {

                    work_interest.clear();
                    interest.clear();
                    food_type.clear();
                    food_distance.clear();
                    interestPerson.clear();
                    interestPersonName = null;
                    VHTConnector.userModel.setInterest(null);
                    VHTConnector.userModel.setWorkField(null);
                    VHTConnector.userModel.setFoodType(null);
                    VHTConnector.userModel.setFoodDistance(null);
                    VHTConnector.userModel.setInterestPerson(null);
                }





            }
            systemIntentsCollection.clear();
            recommendAgain = true;
            flagPrintOutUserIntent = true;
            shouldCheckWaitForResults = true;
        }
    }

    /**
     * 在likes和dislikes兩個hashmap裏添加<entity, value>的鍵值對       ,只有like的時候才有用
     *
     * "entity": "artificial intelligence"
     *
     * "value":
     * [
     *{
     * "entity": "artificial intelligence",
     *"type": "person::work_field",
     * "score": 0.947696269
     * }
     * ]
     *
     * @param nluOutput
     */
    private void extractLikesDislikes(NLUOutput nluOutput) {
//        for (Intent intent : nluOutput.getIntents()) {
//            if (intent.getIntent().equals(Constants.LIKE) || intent.getIntent().equals(Constants.DISLIKE) ||
//                    intent.getIntent().equals(Constants.REQUEST_SESSION_RECOMMENDATION) ||
//                    intent.getIntent().equals(Constants.REQUEST_PERSON_RECOMMENDATION)) {
//                for (Action action : intent.getActions()) {      //其实只有一个action
//                    if (action.isTriggered()) {
//                        if (action.getParameters() != null) {
//                            for (Parameter parameter : action.getParameters()) {
//                                if (parameter.getValue() != null) {
//                                    //                      if( parameter.getValue() != null ) {
//                                    for (Value value : parameter.getValue()) {
//
//                                        if (intent.getIntent().equals(Constants.LIKE) && intent.getScore() > 0.5) {
//                                            likes.put(value.getEntity(), value);
//                                            like.put(value.getType(), value.getEntity());     //这个用来保存到user model
//                                            VHTConnector.userModel.workField = like.get("person::work_field");
//
//                                    //                                            } else if (intent.getIntent().equals(Constants.DISLIKE)) {
//                                    //                                                dislikes.put(value.getEntity(), value);
//
////                                            } else if (intent.getIntent().equals(Constants.REQUEST_PERSON_RECOMMENDATION) && intent.getScore() > 0.5) {
////                                                likes.put(value.getEntity(), value);
////                                                like.put(value.getType(), value.getEntity());     //这个用来保存到user model
////                                                VHTConnector.userModel.workField = like.get("person::keywords");
////
////                                            } else if (intent.getIntent().equals(Constants.REQUEST_SESSION_RECOMMENDATION) && intent.getScore() > 0.5) {
////                                                likes.put(value.getEntity(), value);
////                                                like.put(value.getType(), value.getEntity());     //这个用来保存到user model
////                                                VHTConnector.userModel.workField = like.get("session::topic");
//
////                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    // }
//                }
//            }
//        }
//      改成了从entities里提取,而不是intents。显得更加

//        /**
//         * OLD LUIS
//         */
//        for (Entity entity : nluOutput.getEntities()) {
//            if (entity.getType().equals("person::work_field")) {          //like, people
//                like.put(entity.getType(), entity.getEntity());
//                VHTConnector.userModel.workField = like.get("person::work_field");
//            } else if (entity.getType().equals("person::keywords")) {     //like
//                like.put(entity.getType(), entity.getEntity());
//                VHTConnector.userModel.workField = like.get("person::keywords");
//            }
//        }
//
//        for (Entity entity : nluOutput.getEntities()) {
//            if (entity.getType().equals(Constants.PERSON) || entity.getType().equals(Constants.SESSION)) {
//                interest.put(entity.getType(), entity.getEntity());
//                if (entity.getType().equals(Constants.PERSON)) {
//                    VHTConnector.userModel.interest = interest.get("person");       //sessions  -.-!
//                } else {
//                    VHTConnector.userModel.interest = interest.get("session");
//                }
//            }
//        }

        /**
         * NEW LUIS     两个hashmap ---- like和interest里的所有元素加入state.keywords
         */
        if( TaskReasoner.inputFromUser.equals(Constants.WORK_INTEREST) ||
                nluOutput.getIntents().get(0).toString().equals(Constants.WORK_INTEREST) ) {      //like 职业 领域
            for (Entity entity : nluOutput.getEntities()) {                     //since new LUIS extracts all keywords as "keyword", we save all the keywords
                if (entity.getType().equals("keyword")) {                       //no matter from session/person recommendation or lie,
                    work_interest.put(entity.getEntity(), entity.getEntity());             //in the interest(HashMap) and userModel.interest
                    VHTConnector.userModel.workField = work_interest.get(entity.getEntity());
                }
            }
        } else {    //recommendation, 保存的是推荐session, person的interest
            for (Entity entity : nluOutput.getEntities()) {
                if (entity.getType().equals("keyword")) {
                    interest.put(entity.getEntity(), entity.getEntity());             //in the interest(HashMap) and userModel.interest
                    VHTConnector.userModel.interest = interest.get(entity.getEntity());
                }
                if (entity.getType().equals("person::firstname")) {
                    interestPerson.put("first_name", entity.getEntity());
                }
                if (entity.getType().equals("person::lastname")) {
                    interestPerson.put("last_name", entity.getEntity());
                }
                if (entity.getType().equals("food::type")) {
                    food_type.put(entity.getEntity(), entity.getEntity());
                    VHTConnector.userModel.foodType = food_type.get(entity.getEntity());
                }
                if (entity.getType().equals("food::distance")) {
                    food_distance.put(entity.getEntity(), entity.getEntity());
                    VHTConnector.userModel.foodDistance = food_distance.get(entity.getEntity());
                }
            }
        }
        if ( !interestPerson.isEmpty() ) {
            if (!interestPerson.isEmpty()) {
                interestPersonName = interestPerson.get("first_name");
                if (!interestPerson.get("last_name").isEmpty()) {
                    interestPersonName += (" " + interestPerson.get("last_name"));
                }
            }
        }
        if( !(work_interest.isEmpty() && interest.isEmpty() && interestPerson.isEmpty()) ) {
            String variable;
            variable = "is_keyword_null";
            model.setVariable(variable, "false");
        } else {
            String variable;
            variable = "is_keyword_null";
            model.setVariable(variable, "true");
        }


        if ( VHTConnector.getFlagKeyword().endsWith("[]") ){
            String variable;
            variable = "is_keyword_null";
            model.setVariable(variable, "true");
        }

    }

    /**
     * SelfNaming
     * @param nluOutput
     */
    private void extractSelfNaming(NLUOutput nluOutput) {
        for( Intent intent : nluOutput.getIntents() ) {
            if( intent.getIntent().equals(Constants.SELF_NAMING)) {
                for( Action action : intent.getActions() ) {
                    if( action.isTriggered() ) {
                        if( action.getParameters() != null ) {
                            for (Parameter parameter : action.getParameters()) {
                                if ( parameter.getValue() != null ) {
                                    for ( Value value : parameter.getValue()) {
                                        if ( intent.getIntent().equals(Constants.SELF_NAMING) && intent.getScore() == highScore ) {
                                            selfNaming.put(value.getType(), value.getEntity());
                                            VHTConnector.userModel.firstName = selfNaming.get("person::firstname");
                                            VHTConnector.userModel.lastName = selfNaming.get("person::lastname");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
//
//
//    private void extractFood(NLUOutput nluOutput) {
//        if (nluOutput.getIntents().get(0).getIntent().toString().contains("food")) {
//            for (Entity entity : nluOutput.getEntities()) {
//                if (entity.getType().equals("food::type")) {
//                    food_type.put(entity.getEntity(), entity.getEntity());
//                    VHTConnector.userModel.foodType = food_type.get(entity.getEntity());
//                }
//            }
//        }
//    }




    private List checkWaitForResults(List<State> systemIntentsCollection) {
        State state = null;
        String intent = null;
        List filteredResults = new ArrayList<>();
        String recommendationType = "session";

        boolean isRecommendation = false;
        if( recommendAgain ) {
            for (State st : systemIntentsCollection) {
//                intent = model.extractSystemIntent(st.name);
                if ( checkRecommendationRequired(st)) { //intent != null ||
                    state = st;
                    if (intent == null) {
                        intent = state.name;
                    }
                    recommendAgain = false;
                    break;
                }
            }
        }
        if( state != null ){        //有需要推荐的话,state就不是空
            shouldWaitForRecomResults = true;
            extractKeywords(state);     //add keywords to states.keywords

            if( DMmain.useVHTConnnector ) {
                recommendationType = model.mapUserIntent(intent);
                vhtConnector.sendRecommendationRequest(recommendationType, state.keywords);
            }else{
                shouldWaitForRecomResults = false;
            }
        }
        breakLoopCount = 0;
        while( shouldWaitForRecomResults ){// || breakLoopCount < 20
            Utils.sleep(50);
            isRecommendation = true;
            breakLoopCount++;
            if( breakLoopCount == 80 ){
                System.err.println("WEF Connector is not retrieving results. Check whether it is running.");
                if( recommendationType.contains("session") ){
                    state.keywords.add("ai");
                }else {
                    state.keywords.add("Justine");
                }
                vhtConnector.sendRecommendationRequest( recommendationType, state.keywords);
                breakLoopCount = 0;
                //shouldWaitForRecomResults = false;
            }
        }
        if( isRecommendation ){
            if( results == null || results.isEmpty() ){
                boolean temp = DMmain.useDummyGoals;
                DMmain.useDummyGoals = true;
                createDummyGoals(model.mapUserIntent(intent));
                DMmain.useDummyGoals = temp;
            }
            if( results != null ) {
                for (RecommendationInterface result : results) {
                    if (result != null  && (alreadyRecommended.get(result.getId()) == null || results.size() == 1)) {
                        filteredResults.add(result);
                        alreadyRecommended.put(result.getId(), result);
                        return filteredResults;
                    }
                }
            }
        }
        return filteredResults;
    }

    private boolean checkRecommendationRequired(State st) {
        if( st.name.startsWith(Constants.POPULAR_SESSIONS) || st.name.startsWith(Constants.FIND_PERSON)
                || st.name.startsWith("outcome_person_recommendation")
                || st.name.startsWith("outcome_session_recommendation")
                || st.name.startsWith("outcome_food_recommendation")
                || st.name.startsWith("outcome_party_recommendation") ){
            return true;
        }
        return false;
    }



    /**
     * about the keywords
     * @param state
     */
    //TODO we need to validate this with Amanda
    private void extractKeywords(State state) {
        if( state.keywords == null ){
            state.keywords = new ArrayList<>();
        }
        state.keywords.clear();
//        if( goals.size() > indexGoals ) {       //反正从这里开始state.keywords都是空的,goal如果是request就是3,否则0
//            Intent intent = goals.get( indexGoals );          //intent是goals裏的第一個
//            if(intent != null) {
//                Action action = intent.getActions().get(0);      //action里有triggered, name, parameters
//                    if (action.getParameters() != null) {       //!!
//                        for (Parameter parameter : action.getParameters()) {
//                            if (parameter.getValue() != null) {
//                                for (Value value : parameter.getValue()) {
//                                    if (!value.getEntity().equals(Constants.NONE)) {
//                                        state.keywords.add(value.getEntity());      //!!
//                                    }
//                                }
//                            }
//                        }
//                    }
//            }
//        }

        //extract keywords from user model (likes/dislikes)
//        for( Value value : likes.values() ){
//            if( !state.keywords.contains(value.getEntity()) ) {
//                state.keywords.add(value.getEntity());
//            }
//        }
//        for( Value value : dislikes.values() ){
//            if( !state.keywords.contains(value.getEntity()) ) {
//                state.keywords.add(value.getEntity());
//            }
//        }

        if ( !(work_interest.isEmpty() && interest.isEmpty() &&
                food_type.isEmpty() && food_distance.isEmpty() && interestPerson.isEmpty())) {
            for (String value : work_interest.values()) {       //hashmap
                if (!state.keywords.contains(value)) {
                    state.keywords.add(value);
                }
            }
            for (String value : interest.values()) {
                if (!state.keywords.contains(value)) {
                    state.keywords.add(value);
                }
            }
            if (!state.keywords.contains(interestPersonName)) {
                state.keywords.add(interestPersonName);
            }

        } else if ( model.current.name.contains("person")){
            if (counter%4 == 0) { state.keywords.add("Tanmay Sinha"); counter++; }                  //Justine Cassell
            else if (counter%4 == 1) { state.keywords.add("Yoichi Matsuyama"); counter++; }
            else if (counter%4 == 2) { state.keywords.add("Ran Zhao"); counter++; }
            else if (counter%4 == 3) { state.keywords.add("Tanmay Sinha"); counter++; }

        } else if ( model.current.name.contains("session")){
            if (counterSession%2 == 0) { state.keywords.add("Personal Assistant"); counterSession++; }
            else if (counterSession%2 == 1) { state.keywords.add("Recognition"); counterSession++; }
        }

        if( !state.keywords.contains(VHTConnector.userModel.getInterest()) ) {
            state.keywords.add(VHTConnector.userModel.getInterest());
        }
        if( !state.keywords.contains(VHTConnector.userModel.getWorkField()) ) {
            state.keywords.add(VHTConnector.userModel.getWorkField());
        }
        if( !state.keywords.contains(VHTConnector.userModel.getFoodType()) ) {
            state.keywords.add(VHTConnector.userModel.getFoodType());
        }
        if( !state.keywords.contains(VHTConnector.userModel.getFoodDistance()) ) {
            state.keywords.add(VHTConnector.userModel.getFoodDistance());
        }


  //      System.out.println("the keywords are: " + state.keywords.toString());


        state.keywords.remove("people");
        state.keywords.remove("person");
        state.keywords.remove("session");
        state.keywords.remove("sessions");
        state.keywords.remove("food");
    }





    /************************************************************************************************************************/
    /************************************************************************************************************************/

    //TODO we need to implement a proper Dialogue State Tracker.
    public void processNLUOutput(NLUOutput nluOutput) {
        highScore = 0;

        System.out.print("\n");

        Intent winner = null;
        List<Intent> intents = new ArrayList<>();

        for ( /**Intent intent : nluOutput.getIntents()**/int i = 0; i < nluOutput.getIntents().size(); i++) {
            if (nluOutput.getIntents().get(i).getIntent().equalsIgnoreCase(Constants.NONE)) {
                continue;
            }
            if (nluOutput.getIntents().get(i).getScore() > highScore) {
                highScore = nluOutput.getIntents().get(i).getScore();
                winner = nluOutput.getIntents().get(i);
            }
            boolean matches = checkMatchWithRecommendation(nluOutput.getIntents().get(i).getIntent());
            if (matches) {
                intents.add(nluOutput.getIntents().get(i));  //intents包括 phaseStates 里所有的与nluOutput.getIntents()里重复的
            }                                                  //intents是 user intent
        }


//        for( int i = 0; i < updatedScore.length && i < intents.size(); i++ ) {
//            updatedScore[i] += intents.get(i).getScore();       //sum up
//            if(updatedScore[i] > updatedHighScore) {
//                updatedHighScore = updatedScore[i];
//            }
//        }
        updatedScore[0] += highScore;






        /*
         * half_real_farewell
         */
        if (model.current.name.equals("half_real_farewell")) {
            winner = updateDialogueState(Constants.FAREWELL);
            intents.clear();
            intents.add(winner);
            resetDialogueState();

        /*
         * farewell
         */
        } else if (nluOutput.getIntents().get(0).getIntent().equals(Constants.FAREWELL)) {
            winner = updateDialogueState(Constants.FAREWELL);
            intents.clear();
            intents.add(winner);
            model.current = model.get("farewell");



        } else {


            /**
             * dialogue tracker
             */

            if (model.current.name.equals(Constants.ASK_FOR_YES_OR_NO) || model.current.name.equals(Constants.BACKUP_WORD)) {
                model.current = model.current.previousState;
            }

            if (model.current.name.equals(Constants.FEEDBACK_GOAL_NOT_IDENTIFIED)) {
                State temp1, temp2;
                temp1 = model.current.previousState.previousState;
                temp2 = model.current.previousState;
                model.current = temp2;
                model.current.previousState = temp1;
            }

            if (model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_1P) ||
                    model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_1P) ||
                    model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_MP) ||
                    model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_MP) ||
                    model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_1S) ||
                    model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_1S) ||
                    model.current.name.equals(Constants.SYSTEM_ASK_FOR_CONFIRMATION_MS) ||
                    model.current.name.equals(Constants.SYSTEM_DONT_UNDERSTAND_MS)) {
                State temp1, temp2;
                temp1 = model.current.previousState.previousState;
                temp2 = model.current.previousState;
                model.current = temp2;
                model.current.previousState = temp1;
            }

            //以上几行纯属卖萌      //其实也不是,tracker的第二轮会用到


            //the four states that use dialogue tracker (dont_understand)
//            if (model.current.typeNextIntent.equals("user_intent") && (
//                    (model.current.name.equals("do_interest_elicitation_person_recommendation_1_goal_")) ||
//                            (model.current.name.equals("do_interest_elicitation_person_recommendation_multiple_goal_")) ||
//                            (model.current.name.equals("do_interest_elicitation_session_recommendation_1_goal_")) ||
//                            (model.current.name.equals("do_interest_elicitation_session_recommendation_multiple_goal_")))
//                    ) {
//                if (updatedScore[0] < 0.01) {
//                    winner = updateDialogueState("dont_understand");
//                    intents.clear();
//                    intents.add(winner);      //intents里只有dont_understand, 即winner
//
////            } else if (updatedScore[0] > 0.2 && updatedScore[0] < 0.5) {
////                winner = updateDialogueState("ask_for_confirmation");
////                intents.clear();
////                intents.add(winner);
////
////
////                extractAskForConfirmation( nluOutput );
////                query = nluOutput.getQuery();    //只有ask_for_confirmation时才把用户的话存到query,存到user model
//
//                } else {
//                    resetDialogueState();       //reset
//                }
//




        /*
         * pre_closing
         */
            if (model.current.name.equals("pre_closing")) {
                if (nluOutput.getIntents().get(0).getIntent().equals(Constants.REQUEST_PERSON_RECOMMENDATION) ||
                        nluOutput.getIntents().get(0).getIntent().equals(Constants.REQUEST_SESSION_RECOMMENDATION) ||
                        nluOutput.getIntents().get(0).getIntent().equals(Constants.REQUEST_FOOD_RECOMMENDATION)) {
                    resetDialogueState();
                } else {
                    winner = updateDialogueState(Constants.FAREWELL);
                    intents.clear();
                    intents.add(winner);
                    resetDialogueState();
                }




        /*
         * food
         */

            } else if (model.current.name.equals("feedback_start_food_recommendation_1_goal") ||
                       model.current.name.equals("feedback_start_food_recommendation_multiple_goal")) {
                winner = updateDialogueState(Constants.REQUEST_FOOD_RECOMMENDATION);
                intents.clear();
                intents.add(winner);
                resetDialogueState();
                extractLikesDislikes(nluOutput);
            } else if (model.current.name.equals("start_food_recommendation_not_1st_time_request_no_continuation")) {
                if (nluOutput.getIntents().get(0).getIntent().equals(Constants.NEGATIVE_CONFIRMATION)) {
                    winner = updateDialogueState(Constants.NEGATIVE_CONFIRMATION);
                    intents.clear();
                    intents.add(winner);
                    resetDialogueState();
                } else if (nluOutput.getIntents().get(0).getIntent().equals(Constants.REQUEST_FOOD_RECOMMENDATION)) {
                    winner = updateDialogueState(Constants.REQUEST_FOOD_RECOMMENDATION);
                    intents.clear();
                    intents.add(winner);
                    resetDialogueState();
                } else {
                    winner = updateDialogueState(Constants.POSITIVE_CONFIRMATION);
                    intents.clear();
                    intents.add(winner);
                    resetDialogueState();
                }
                extractLikesDislikes(nluOutput);


        /*
         *一
         */
            } else if (model.current.name.equals("greeting")) {       //四个不管说什么都继续的特例,去掉
                winner = updateDialogueState("self_naming");            //情况二
                intents.clear();
                intents.add(winner);
                resetDialogueState();
                extractSelfNaming(nluOutput);
            } else if (
                    model.current.name.equals("elicit_feedback_food_recommendation_multiple_goal") ||
                            model.current.name.equals("elicit_feedback_food_recommendation_1_goal")) {  //四个不管说什么都继续的特例
                winner = updateDialogueState("positive_confirmation");                           //情况二
                intents.clear();
                intents.add(winner);
                resetDialogueState();



        /*
         *二
         */
            } else if (model.current.name.equals("do_goal_elicitation") ||
                    model.current.name.equals("do_goal_elicitation_by_interest") ||
                    model.current.name.equals("feedback_present_work")) {
                if (!nluOutput.getIntents().get(0).getIntent().equals(Constants.REQUEST_PERSON_RECOMMENDATION) &&
                        !nluOutput.getIntents().get(0).getIntent().equals(Constants.REQUEST_SESSION_RECOMMENDATION) &&
                        !nluOutput.getIntents().get(0).getIntent().equals(Constants.REQUEST_FOOD_RECOMMENDATION) &&
                        !nluOutput.getIntents().get(0).getIntent().equals(Constants.PRESENT_WORK) &&
                        !nluOutput.getIntents().get(0).getIntent().equals(Constants.WORK_INTEREST) &&
                        !nluOutput.getIntents().get(0).getIntent().equals("goal_not_identified")) {
                    winner = updateDialogueState(Constants.REQUEST_PERSON_RECOMMENDATION);                           //情况三
                    //intents.clear();
                    intents.add(winner);
                    resetDialogueState();
                } else {
                    extractLikesDislikes(nluOutput);
                    //     extractSelfNaming(nluOutput);
                    resetDialogueState();
                } //情况三




        /*
         *二.5
         */
                // prefer "work_interest"
                // those that only accept "1_goal", "multiple_goals", "work_interest", "goal_not_identified"
            } else if (              //以下都是新加的(每一轮都问一次兴趣)
                            (model.current.name.equals("do_interest_elicitation_person_recommendation_1_goal_")) ||
                            (model.current.name.equals("start_person_recommendation_1_goal_2nd_time_if_prior_feedback_no")) ||
                            (model.current.name.equals("start_person_recommendation_1_goal_3rd_time_if_prior_feedback_no")) ||

                            (model.current.name.equals("do_interest_elicitation_person_recommendation_multiple_goal_")) ||
                            (model.current.name.equals("start_person_recommendation_multiple_goal_2nd_time_if_prior_feedback_no")) ||
                            (model.current.name.equals("start_person_recommendation_multiple_goal_3rd_time_if_prior_feedback_no")) ||

                            (model.current.name.equals("do_interest_elicitation_session_recommendation_1_goal_")) ||
                            (model.current.name.equals("do_interest_elicitation_session_recommendation_multiple_goal_")) ||

                            (model.current.name.equals("feedback_start_person_recommendation_1_goal_2nd_time_yes")) ||  //√ //一下全是tell me more about your interest
                            (model.current.name.equals("feedback_start_person_recommendation_1_goal_3rd_time_yes")) ||  //√
                            (model.current.name.equals("feedback_start_person_recommendation_multiple_goal_2nd_time_yes")) ||   //√
                            (model.current.name.equals("feedback_start_person_recommendation_multiple_goal_3rd_time_yes")) ||   //√


                //下面的要加入no的选项
                            (model.current.name.equals("start_session_recommendation_1_goal_2nd_time_if_prior_feedback_yes")) ||    //√
                            (model.current.name.equals("start_session_recommendation_1_goal_2nd_time_if_prior_feedback_no")) ||     //√
                            (model.current.name.equals("start_session_recommendation_1_goal_3rd_time_if_prior_feedback_yes")) ||    //√
                            (model.current.name.equals("start_session_recommendation_1_goal_3rd_time_if_prior_feedback_no")) ||     //√
                            (model.current.name.equals("start_session_recommendation_multiple_goal_2nd_time_if_prior_feedback_yes")) || //√
                            (model.current.name.equals("start_session_recommendation_multiple_goal_2nd_time_if_prior_feedback_no")) ||  //√
                            (model.current.name.equals("start_session_recommendation_multiple_goal_3rd_time_if_prior_feedback_yes")) || //√
                            (model.current.name.equals("start_session_recommendation_multiple_goal_3rd_time_if_prior_feedback_no"))) { //√

                extractLikesDislikes(nluOutput);
                //      extractSelfNaming(nluOutput);   //放进hashmap, usermodel

                if (model.current.name.contains("session")) {     //情况二
                    winner = updateDialogueState(Constants.REQUEST_SESSION_RECOMMENDATION);
                    intents.clear();
                    intents.add(winner);
                } else if (model.current.name.contains("person")) {      //情况二
                    winner = updateDialogueState(Constants.REQUEST_PERSON_RECOMMENDATION);
                    intents.clear();
                    intents.add(winner);
                }

                if (!(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.REQUEST_PERSON_RECOMMENDATION)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.REQUEST_SESSION_RECOMMENDATION)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.REQUEST_FOOD_RECOMMENDATION)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.ONE_GOAL)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.MULTIPLE_GOALS)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.WORK_INTEREST)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.PRESENT_WORK)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.SELF_NAMING))) {
                    winner = updateDialogueState("dont_understand");
                    intents.clear();
                    intents.add(winner);      //intents里只有dont_understand, 即winner  //情况二
                }

                if (updatedScore[0] < 0.1) {        //情况二
                    winner = updateDialogueState("dont_understand");
                    intents.clear();
                    intents.add(winner);      //intents里只有dont_understand, 即winner

                }

                resetDialogueState();
                //most dangerous states



        /*
         *三
         */
                //你还想要推荐其他人来match你的interest吗? Shall I find some other people who match your interests?
                //yes/no, request_person_recommendation
            } else if (model.current.name.equals("start_person_recommendation_1_goal_2nd_time_if_prior_feedback_yes") ||
                    model.current.name.equals("start_person_recommendation_1_goal_3rd_time_if_prior_feedback_yes") ||
                    model.current.name.equals("start_person_recommendation_multiple_goal_2nd_time_if_prior_feedback_yes") ||
                    model.current.name.equals("start_person_recommendation_multiple_goal_3rd_time_if_prior_feedback_yes")

                    ) {
                if ((!(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.POSITIVE_CONFIRMATION)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.WORK_INTEREST)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.REQUEST_PERSON_RECOMMENDATION))) ||
                        (nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.REQUEST_SESSION_RECOMMENDATION)) ||
                        (nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.NONE))) {
                    winner = updateDialogueState(Constants.NEGATIVE_CONFIRMATION);  //情况二
                    intents.clear();
                    intents.add(winner);
                } else if (nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.POSITIVE_CONFIRMATION)) {
                    winner = updateDialogueState(Constants.POSITIVE_CONFIRMATION);  //情况二
                    intents.clear();
                    intents.add(winner);
                } else {
                    winner = updateDialogueState(Constants.WORK_INTEREST);  //情况二
                    intents.clear();
                    intents.add(winner);
                    extractLikesDislikes(nluOutput);
                }
                resetDialogueState();   //否则情况三


                //Shall we move on to finding some interesting sessions for you to attend?
                //yes/no, no other. [D]No
            } else if (model.current.name.equals("start_session_recommendation_not_1st_time_request_no_continuation") ||
                    model.current.name.equals("start_person_recommendation_not_1st_time_request_no_continuation")) {
                if ((!(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.POSITIVE_CONFIRMATION)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.WORK_INTEREST)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.REQUEST_PERSON_RECOMMENDATION)) &&
                        !(nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.REQUEST_SESSION_RECOMMENDATION))) ||
                        (nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.NONE))) {
                    winner = updateDialogueState(Constants.NEGATIVE_CONFIRMATION);  //情况二
                    intents.clear();
                    intents.add(winner);
                } else {
                    winner = updateDialogueState(Constants.POSITIVE_CONFIRMATION);  //情况二
                    intents.clear();
                    intents.add(winner);
                }

        /*
         *四
         */
                //只有yes/no回答的states [D]Yes
                //yes/no questions 最后搞。除非是negative,其他都算yes      如果negative最大,那么就negative, 其他情况除了最大小于0.2以外,都是positive
            } else { //情况二
                if (nluOutput.getIntents().get(0).getIntent().equalsIgnoreCase(Constants.NEGATIVE_CONFIRMATION)) {
                    winner = updateDialogueState(Constants.NEGATIVE_CONFIRMATION);
                    intents.clear();
                    intents.add(winner);
                } else if (updatedScore[0] < 0.01) {                 //very very very low
                    winner = updateDialogueState("ask_for_yes/no");
                    intents.clear();
                    intents.add(winner);
                } else { //情况二
                    winner = updateDialogueState(Constants.POSITIVE_CONFIRMATION);
                    intents.clear();
                    intents.add(winner);

                }

                resetDialogueState();       //reset


            }
        }







        //三种情况

        if (!intents.contains(winner)) {
            intents.clear();
        }

        //情况一
        //包括小于0.7的几轮加起来的情况
        if (intents.isEmpty()) {           //recommendation以外的intents,因为没有winner,先执行了上面的clear,并且大于0.7
            TaskReasoner.inputFromUser = extractIntent(winner.getIntent());             //原始的intent和extract后的inputFromUser不一样
            DMmain.addToLog("User intent: " + winner.getIntent());
            indexGoals = 0;
            goals.add(winner);
            prioritizeGoals(intents);
            extractLikesDislikes(nluOutput);



        //情况二       不getUserIntentFromGoal,不会变成1_goal
        //小于0.7,包括两个tracker
        } else if (intents.size() == 1) {     //intents的唯一元素是小于0.7时的winner
            hasUserSaidHisGoals = false;    //why false? Because lt 0.7       /*这里可能需要分0.3, 0.7讨论一下*/
            indexGoals = 0;
            isMultigoal = false;
            goals = new ArrayList<>(intents);
//            TaskReasoner.inputFromUser = getUserIntentFromGoal( intents.get(0).getIntent() );       //原始的intent和extract后的inputFromUser不一样(只有recommendation才不一样)
            TaskReasoner.inputFromUser = intents.get(0).getIntent();    //直接把intents里的唯一一个给inputFromUser
            DMmain.addToLog("User intent: " + TaskReasoner.inputFromUser);



        //情况三       变1_goal
        //most common
        } else {                              //recommendation的intents,并且大于0.7
            hasUserSaidHisGoals = true;
            ArrayList<Intent> removedIntents = new ArrayList();
            for (Intent it : intents) {
                if (it.getScore() < 0.2) {
                    removedIntents.add(it);
                }
            }
            for (Intent it : removedIntents) {
                intents.remove(it);
            }
            //把小于0.2的都去掉,剩下的再判断是不是multigoals,

            indexGoals = 0;
            prioritizeGoals(intents);
            if (goals.size() > 1) {             //goals是prioritizeGoals的输出
                isMultigoal = true;
            } else {
                isMultigoal = false;
            }

            TaskReasoner.inputFromUser = getUserIntentFromGoal(goals.get(indexGoals).getIntent()); //变成1_goal

            DMmain.addToLog("User intent: " + TaskReasoner.inputFromUser);
            extractLikesDislikes(nluOutput);

        }


        DMmain.noInputFlag = false;

    }








    /************************************************************************************************************************/
    /************************************************************************************************************************/

    private void resetDialogueState() {
        for( int i = 0; i < updatedScore.length; i++ ){
            updatedScore[i] = 0;
        }
    }

    private Intent updateDialogueState(String name) {
        Intent winner = new Intent();
        winner.setScore(1.0);
        winner.setIntent(name);
//        for( int i = 0; i < updatedScore.length && i < intents.size(); i++ ){
//            updatedScore[i] += intents.get(i).getScore();
//        }
        return winner;
    }

    /************************************************************************************************************************/
    /************************************************************************************************************************/






    /**
     * this method is important for generating system intent.
     * @param intent
     * @return
     */

    //TODO check the returned values
    private String extractIntent(String intent) {       //directly returns the intent
        Pattern pattern = Pattern.compile("exit_node_[a-zA-Z]*_recommendation_multiple_goal");
        Matcher matcher = pattern.matcher(intent);
        if (matcher.matches() && indexGoals < goals.size() && !lastIntent.equals(intent)) {
            shouldIncreaseGoalIdx = true;
        }
        if (intent.equals(Constants.RESET) || intent.contains(Constants.REAL_FAREWELL)) {
            DMmain.storeLog();
            sendSystemIntents();
            DMmain.flagReset = true;
            return intent;
        }
        if (intent.startsWith("exit_node_") && indexGoals < goals.size()) { //
            if (!shouldIncreaseGoalIdx) {
                return intent;
            }
            indexGoals++;
            shouldIncreaseGoalIdx = false;
            if (indexGoals >= goals.size()) {
                indexGoals = 0;
                model.setVariable(Constants.NEXT_GOAL, Constants.NO_GOAL);
                return intent;
            }
            String currentGoal = goals.get(indexGoals).getIntent();
            lastIntent = intent;
            model.setVariable(Constants.NEXT_GOAL, currentGoal);
            return intent;
        }
        if (intent.equals(Constants.MULTIPLE_GOALS) && !goals.isEmpty()) {
            return "feedback_goal_elicitation_multiple_goal";
        }
        if (intent.equals(Constants.ONE_GOAL) && !goals.isEmpty()) {
            return "feedback_goal_elicitation_1_goal";
        }
        if ((intent.equals("feedback_goal_elicitation_1_goal") || intent.equals("feedback_goal_elicitation_multiple_goal"))
                && !goals.isEmpty()) {
            String it = goals.get(indexGoals).getIntent();
            String prefix = intent.substring(25);
            if (it.equals(Constants.REQUEST_PERSON_RECOMMENDATION)) {
                return "start_node_person_recommendation" + prefix;
            } else if (it.equals(Constants.REQUEST_SESSION_RECOMMENDATION) || it.equals("request_session_recommendation")) {
                return "start_node_session_recommendation" + prefix;
            } else if (it.equals(Constants.REQUEST_FOOD_RECOMMENDATION)) {
                return "start_node_food_recommendation" + prefix;
            } else if (it.equals("recommend_party") || it.equals("recommend_parties")) {
                return "start_node_party_recommendation"; //  + prefix
            }
        }
        if (intent.equals(Constants.NEGATIVE_CONFIRMATION) && model.current.name.contains("request_no_continuation")) {
            String variable = "";
            if (model.current.name.contains("person")) {
                variable = "is_person_recommendation_covered";
            } else if (model.current.name.contains("session")) {
                variable = "is_session_recommendation_covered";
            } else if (model.current.name.contains("food")) {
                variable = "is_food_recommendation_covered";
            }
            model.setVariable(variable, "true");
        }
        String variable;
        if ( (work_interest.isEmpty() && interest.isEmpty() && interestPerson.isEmpty()) || VHTConnector.getFlagKeyword().endsWith("[]") ) {
            variable = "is_keyword_null";       //outcome是default
            model.setVariable(variable, "true");
        }


        return intent;
    }


    /**
     * 把user intent变成1_goal, multiple_goals
     * @param intentString
     */
    //TODO check values for start_node_XXX
    private String getUserIntentFromGoal( String intentString ) {       //输入是prioritize后的第一个,
        if( hasUserSaidHisGoals ) {                                     //person, session, food
            if( intentString.equals(Constants.REQUEST_PERSON_RECOMMENDATION) || intentString.equals(Constants.REQUEST_SESSION_RECOMMENDATION)
                    || intentString.equals("request_person_recommendation") || intentString.equals(Constants.REQUEST_FOOD_RECOMMENDATION) ){
                if (isMultigoal) {
                    intentString = Constants.MULTIPLE_GOALS;
                } else {
                    intentString = Constants.ONE_GOAL;
                }

                //下面的貌似都没用


            } else if (intentString.equals(Constants.FEEDBACK_GOAL_NOT_IDENTIFIED) || intentString.equals("do_goal_elicitation")) {
                if (isMultigoal) {
                    intentString = "feedback_goal_elicitation_multiple_goal";
                } else {
                    intentString = "feedback_goal_elicitation_1_goal";
                }


            } else if (intentString.equals(Constants.REQUEST_PERSON_RECOMMENDATION)) {
                if (isMultigoal) {
                    intentString = "start_node_person_recommendation_multiple_goal";
                } else {
                    intentString = "start_node_person_recommendation_1_goal";
                }
            } else if (intentString.equals(Constants.REQUEST_SESSION_RECOMMENDATION) || intentString.equals("request_session_recommendation")) {
                if (isMultigoal) {
                    intentString = "start_node_session_recommendation_multiple_goal";
                } else {
                    intentString = "start_node_session_recommendation_1_goal";
                }
            } else if (intentString.equals("recommend_party") || intentString.equals(Constants.REQUEST_PARTY_RECOMMENDATION)) {
                if (isMultigoal) {
                    intentString = "start_node_party_recommendation_multiple_goal";
                } else {
                    intentString = "start_node_party_recommendation_1_goal";
                }
            } else if (intentString.equals(Constants.REQUEST_FOOD_RECOMMENDATION) || intentString.equals("request_food_recommendation")) {
                if (isMultigoal) {
                    intentString = "start_node_food_recommendation_multiple_goal";
                } else {
                    intentString = "start_node_food_recommendation_1_goal";
                }
//            } else if (intentString.equals(Constants.FEEDBACK_GOAL_ELICITATION_ONE_GOAL) && VHTConnector.userModel.workField != null) {
//                intentString = "feedback_interest_elicitation_session_recommendation_1_goal_";
//            } else if (intentString.equals(Constants.FEEDBACK_GOAL_ELICITATION_MULTIPLE_GOAL) && VHTConnector.userModel.workField != null) {
//                intentString = "feedback_interest_elicitation_session_recommendation_multiple_goal_";
            }
        }
        hasUserSaidHisGoals = false;
        return intentString;
    }

    //TODO we need to check whether priorities array matches the user intents sent by NLU, e.g.: party? or recommend_parties?
    private void prioritizeGoals( List<Intent> unsortedGoals) {
        List<Intent> temp = new ArrayList<>();
        String[] priorities = new String[]{
            Constants.REQUEST_PERSON_RECOMMENDATION,
            //Constants.FIND_PERSON,
            Constants.REQUEST_SESSION_RECOMMENDATION,
            //Constants.FIND_SESSION_DETAIL,
            Constants.REQUEST_FOOD_RECOMMENDATION,
            //Constants.RECOMMEND_PARTY
        };

        external : for( int i = 0; i < priorities.length; i++ ){
            for( int j = 0; j < unsortedGoals.size(); j++ ){
                if( unsortedGoals.get(j).getIntent().equals( priorities[i] ) ){
                    temp.add( unsortedGoals.get(j) );
                    continue  external;
                }
            }
        }
        goals = new ArrayList<>(temp);
    }


    //TODO include all the cases that triggers start recommendation
    private boolean checkMatchWithRecommendation(String intent) {
        for( String[] phaseState : model.getPhaseStates() ) {
            if( phaseState[0].equals( intent ) || phaseState[1].equals(intent) ){
                return true;
            }
        }
        if( intent.equals(Constants.REQUEST_PERSON_RECOMMENDATION) ){
            return true;
        }
        return false;
    }



    /**
     * EXTRACTRECOMENDATIONS
     * @param response
     * @return void
     */
    public void extractRecommendationResults(String response) {
        try {
            String recommendationType = response.substring(0, response.indexOf(" "));
            String json = response.substring(response.indexOf(" ") + 1);
            if( recommendationType.equals(Constants.REQUEST_SESSION_RECOMMENDATION) || recommendationType.equals(Constants.POPULAR_SESSIONS) ) {
                sessions = Session.parse(new JSONArray(json));
                results = new ArrayList( sessions );
            }else if( recommendationType.equals(Constants.REQUEST_PERSON_RECOMMENDATION) || recommendationType.equals(Constants.FIND_PERSON)
                    || recommendationType.equals(Constants.SEARCH_PEOPLE)) {
                people = Participant.parse(new JSONArray(json));
                results = new ArrayList( people );
            }else if( recommendationType.equals(Constants.REQUEST_FOOD_RECOMMENDATION) || recommendationType.contains("food")) {
                food = Food.parse(new JSONArray(json));
                results = new ArrayList( food );
//                results = new ArrayList( );
            }else if( recommendationType.equals(Constants.REQUEST_PARTY_RECOMMENDATION)) {
                parties = Party.parse(new JSONArray(json));
                results = new ArrayList( parties );
            }
            shouldWaitForRecomResults = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void extractRecommResultsFromOscarWEFConn(String response) {
        try {
            String recommendationType = response.substring(0, response.indexOf(" "));
            String json = response.substring(response.indexOf(" ") + 1);
            if( recommendationType.equals(Constants.REQUEST_SESSION_RECOMMENDATION) || recommendationType.equals(Constants.POPULAR_SESSIONS)
                    || recommendationType.equals(Constants.REQUEST_SESSION_RECOMMENDATION) || recommendationType.contains("session")) {
                sessions = Session.parse(new JSONArray(json));
                results = new ArrayList( sessions );
            }else if( recommendationType.equals(Constants.REQUEST_PERSON_RECOMMENDATION) || recommendationType.equals(Constants.FIND_PERSON)
                    || recommendationType.equals(Constants.SEARCH_PEOPLE) || recommendationType.contains("people")) {
                people = Participant.parse(new JSONArray(json));
                results = new ArrayList( people );
            }else if( recommendationType.equals(Constants.REQUEST_FOOD_RECOMMENDATION) || recommendationType.contains("food")) {
                food = Food.parse(new JSONArray( json ));
                results = new ArrayList( food );
//                results = new ArrayList( );
            }else if( recommendationType.equals(Constants.RECOMMEND_PARTY) || recommendationType.contains("party")) {
                parties = Party.parse(new JSONArray(json));
                results = new ArrayList( parties );
            }
            shouldWaitForRecomResults = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createDummyGoals(String userInput) {
        if( DMmain.useDummyGoals ) {
            boolean isLike = userInput.equals(Constants.WORK_INTEREST);
            boolean isDislike = userInput.equals(Constants.DISLIKE);
            boolean isRecommendation = userInput.contains("recommend_") || userInput.startsWith(Constants.MULTIPLE_GOALS);
            boolean isSelfNaming = userInput.equals(Constants.SELF_NAMING);

            if (isRecommendation || isLike || isDislike) {
                boolean twoGoals = false;
                if (userInput.startsWith(Constants.MULTIPLE_GOALS)) {
                    twoGoals = true;
                    userInput = userInput.replace(Constants.MULTIPLE_GOALS, Constants.REQUEST_SESSION_RECOMMENDATION);
                }
                String[] splitted = userInput.split(" ");
                String name = splitted[0];
                NLUOutput nluOutput = new NLUOutput();
                nluOutput.setQuery(name);
                List<Intent> intents = new ArrayList();
                Intent intent = new Intent();
                ArrayList<Action> actions = new ArrayList<>();
                ArrayList<Parameter> parameters = new ArrayList<>();
                Parameter parameter = new Parameter();
                parameter.setName("Topic");
                parameter.setValueString( splitted.length > 1? splitted[1] : "business");
                parameters.add(parameter);
                Action action = new Action();
                action.setName(name);
                action.setParameters(parameters);
                action.setTriggered(true);
                actions.add(action);
                intent.setActions(actions);
                intent.setIntent(name);
                intent.setScore(0.65);
                intents.add(intent);
                if (twoGoals) {
                    intent = new Intent();
                    parameter = new Parameter();
                    parameter.setName("Keywords");
                    parameter.setValueString( splitted.length > 2? splitted[2] : "John"); //"Justine"
                    parameters = new ArrayList<>();
                    parameters.add(parameter);
                    action = new Action();
                    action.setName(Constants.REQUEST_PERSON_RECOMMENDATION);
                    action.setParameters(parameters);
                    action.setTriggered(true);
                    actions = new ArrayList<>();
                    actions.add(action);
                    intent.setActions(actions);
                    intent.setIntent(Constants.REQUEST_PERSON_RECOMMENDATION);
                    intent.setScore(0.65);
                    intents.add(intent);
                }
                nluOutput.setIntents(intents);
                processNLUOutput(nluOutput);
            }
        }
    }
}
