package wef.articulab.control.reasoners;

/**
 * Created by oscarr on 5/24/16.
 */

import com.google.gson.Gson;
import wef.articulab.model.Constants;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Model{
    private HashMap<String, State> states;
    private ArrayList<String[]> phaseStates;
    private static Gson gson = new Gson();
    private static boolean saveModel = false;
    private static boolean loadModel = true;
    private ArrayList<State> history = new ArrayList<>();
    public ArrayList<String> scenarios;
    public String triggeredBy = Constants.START;
    public String currentIntent = Constants.START;
    static boolean sessionRecommendation = false;
    static boolean foodRecommendation = false;
    static boolean personRecommendation = false;
    static boolean partiesRecommendation = false;
    private boolean runScenario;
    private static Model model;
    public transient State current;
    private String initialUserConvStrat;
    private String initialRapportLevel;
    private String[][] vars;
    private transient HashMap<String, Object> variables;


    public Model(){
        states = new HashMap<>();
        phaseStates = new ArrayList<>();
    }

    public static Model getInstance(){
        if( model == null ){
            model = new Model();
        }
        return model;
    }

    public Collection<String> getStates() {
        return states.keySet();
    }

    public ArrayList<String[]> getPhaseStates() {
        return phaseStates;
    }

    public State get(String intention){
        return states.get(intention);
    }

    public void saveModel(Model model){
        try {
            String json = gson.toJson(model);
            PrintWriter out = new PrintWriter("central_model_fsm.json");
            out.println(json);
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Model loadModel() {
        try{
            Scanner sc = new Scanner(new File("central_model_fsm.json"));
            String json = "";
            while (sc.hasNextLine()) {
                json += sc.nextLine();
            }
            Model model = gson.fromJson( json, Model.class);
            for( String key : model.states.keySet() ){
                model.states.get(key).name = key;
            }
            if( !model.runScenario ){
                if( model.scenarios != null ){
                    model.scenarios.clear();
                }
            }
            model.variables = new HashMap<>();
            for( String[] v : model.vars ){
                model.setVariable( v[0], v[1], v[2]);
            }
            validateModel(model);
            return model;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static void validateModel(Model model) {
        System.out.println("Start validating the Central Model (JSON file)...");
        try {
            for (State state : model.states.values()) {
                if (state.typeNextIntent.equals("system_intent")) {
                    for (TransitionState ts : state.nextTransitionsStates) {
                        if (!ts.intention.equals(state.name) && !ts.intention.equals("any")) {
                            throw new Exception("The TransitionState: " + ts.state + " has an intention (" + ts.intention +
                                    ") which does not match with state name: " + state.name);
                        }
                    }
                }
            }
            for (State state : model.states.values()) {
//                String phase = extractPhase( state.phase );
                for (TransitionState ts : state.nextTransitionsStates) {
                    if( model.get( ts.state ) == null && !ts.state.equals("any") && !ts.state.equals(Constants.RESET)){
                        throw new Exception("TransitionState: " + ts.state + " is not a valid state");
                    }
                    if( ts.operation != null && model.getValue( ts.varName ).equals("") ){
                        throw new Exception("TransitionState: " + ts.state + " has a varName (" + ts.varName + ") which " +
                                "is not a valid variable");
                    }
//                    if( (!state.name.contains(phase) || !state.phase.contains(phase) || !ts.state.contains(phase)
//                            || !ts.intention.contains(phase) ) && ( !state.name.equals("pre_closing")
//                            && !ts.state.equals("pre_closing") && !ts.intention.equals(Constants.POSITIVE_CONFIRMATION)
//                            && !ts.intention.equals(Constants.NEGATIVE_CONFIRMATION))
//                            && !state.name.equals("greeting") && !state.name.equals("pleasure_coming_together")
//                            && !state.name.equals("")){
//                        throw new Exception("State name(" + state.name + "), phase name(" + phase + "), transition state" +
//                                "(" + ts.state + ") and transition intention(" + ts.intention + "), should contain the word: " + phase );
//                    }
                }
            }
            System.out.println("The Central Model has been successfully validated!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String extractPhase(String name) {
        if( name.contains("session") ) return "session";
        if( name.contains("sessions") ) return "sessions";
        if( name.contains("people") ) return "people";
        if( name.contains("person") ) return "person";
        if( name.contains("food") ) return "food";
        if( name.contains("party") ) return "party";
        if( name.contains("parties") ) return "parties";
        if( name.contains("farewell") ) return "farewell";
        if( name.contains("goal") ) return "goal";
        if( name.contains("greetings") ) return "greetings";
        return name;
    }

    public void setVariable(String name, String type, String value){
        if( type.equals("boolean") ){
            variables.put( name, Boolean.valueOf( value ) );
        }else if( type.equals("integer") ){
            variables.put( name, Integer.valueOf( value ));
        }else if( type.equals("double") ){
            variables.put( name, Double.valueOf( value ) );
        }else if( type.equals("string") ){
            variables.put( name, value );
        }
    }

    public void setVariable(String name, String value){
        setVariable(name, getType(name), value);
    }


    public static Model createNodes(){
        if( loadModel ){
            model = loadModel();
        }else {
            model = new Model();
        }
        return model;
    }

    public State createNodeUser(String name, String[][] tranStates, String systemUtterance){
        State state = new State(name, tranStates, systemUtterance);
        state.typeNextIntent = Constants.USER_INTENT ;
        return state;
    }

    public State createNode(String name, String[][] transitionStates, String systemUtterance){
        State state = new State(name, transitionStates, systemUtterance);
        state.typeNextIntent = Constants.SYSTEM_INTENT;
        return state;
    }

    public String processSystemUtterance() {
        if( current.systemUtterance != null ) {
            String[] utterance = current.systemUtterance.split("]");
            for (int i = 0; i < utterance.length; i++) {
                utterance[i] = utterance[i].replace("[", "").replace("]", "").trim();
            }
            if (utterance.length == 1) {
                return utterance[0];
            }
            return utterance[getPos(utterance, current.phase)];
        }
        return "";
    }

    public int getPos(String[] array, String element){
        for(int i = 0; i < array.length; i++){
            if( array[i].equals(element) ){
                return i + 1;
            }
        }
        return 0;
    }

    public String extractSystemIntent(String intent) {
        for (String[] phase : phaseStates) {
            if (intent.startsWith(phase[1]) && !intent.startsWith("pre_closing") && !intent.startsWith("farewell")) {
                return phase[0];
            }
        }
        return null;
    }

    public String mapUserIntent(String intent){
        if( Constants.REQUEST_SESSION_RECOMMENDATION.equals( intent ) || intent.startsWith( "outcome_session")
                || Constants.FIND_SESSION_DETAIL.equals( intent )) {
            return "request_session_recommendation";
        }else if( Constants.REQUEST_PERSON_RECOMMENDATION.equals(intent) || intent.startsWith( "outcome_person")){
            return Constants.REQUEST_PERSON_RECOMMENDATION;
        }else if( Constants.FIND_PERSON.equals( intent )){
            return Constants.SEARCH_PEOPLE;
        }else if( Constants.REQUEST_FOOD_RECOMMENDATION.equals(intent) || intent.startsWith( "outcome_food")){
            return Constants.REQUEST_FOOD_RECOMMENDATION;
//        }else if( Constants.RECOMMEND_PARTY.equals( intent ) || intent.startsWith( "outcome_party")){
//            return Constants.RECOMMEND_PARTY;
        }else if ( Constants.WORK_INTEREST.equals( intent )){
            return model.current.previousState.toString();
        }
        return intent;
    }

    public String extractSystemIntent(String intent, boolean extractStateName) {
        for( String[] phase : phaseStates ){
            if( phase[0].equals(intent) ){
                return phase[1];
            }
        }
        for( TransitionState state : current.nextTransitionsStates){
            if( state.intention.equals( intent ) ){
                if( state.operation == null ){
                    return state.state;
                }
                return intent;
            }
        }
        for( TransitionState state : current.nextTransitionsStates){
            if( state.intention.equals(Constants.GOAL_NOT_SUPPORTED) && !extractStateName && state.operation == null){
                return state.intention;
            }
        }
        if( extractStateName ) {
            return current.name;
        }
        return "";
    }

    public void storeInHistory(String input) {
        if( current.typeNextIntent.equals(Constants.USER_INTENT ) ){
            history.add( new State(input, true) );
        }else{
            history.add( current );
        }
        triggeredBy = input;
    }

    public String getInitialUserConvStrat() {
        return initialUserConvStrat;
    }

    public String getInitialRapportLevel() {
        return initialRapportLevel;
    }

    public String getType(String varName) {
        for( String[] v : vars ){
            if( v[0].equals(varName) ){
                return v[1];
            }
        }
        return "";
    }

    public String getValue(String varName) {
        Object result = variables.get( varName );
        if( result != null ){
            return result.toString();
        }
        return "";
    }

    public static void reset(){
        model.states.clear();
        model.phaseStates.clear();
        model.history.clear();
        model.scenarios.clear();
        model.variables.clear();
        model = null;
        getInstance();
    }
}

