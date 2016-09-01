package wef.articulab.control.reasoners;

import wef.articulab.model.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 5/24/16.
 */
public class State implements Comparable{
    public String id;
    public String name;
    public String phase;
    public String typeNextIntent;
    public ArrayList<TransitionState> nextTransitionsStates;
    public String systemUtterance;
    public boolean internalValidation;
    public String[] conversationalStrategy;
    public List<String> keywords;
    boolean isUserIntent;
    transient boolean changePhase;
    public transient int order;
    public transient State previousState;

    public State(){}

    public State(String name, String[][] nextTransitionsStates, String systemUtterance) {
        this(name, nextTransitionsStates, Constants.SYSTEM_INTENT, systemUtterance, false);
    }

    public State(String name, String[][] nextTransitionsStates, String typeNextIntent, String systemUtterance, boolean isUserIntent) {
        this.nextTransitionsStates = createTransitionStates(nextTransitionsStates);
        this.typeNextIntent = typeNextIntent;
        this.systemUtterance = systemUtterance;
        this.name = name;
        this.isUserIntent = isUserIntent;
    }

    public State(String name, boolean isUserIntent) {
        this(name, null, null, null, isUserIntent);
    }

    public State(String name, String phase) {
        this.name = name;
        this.phase = phase;
    }

    private ArrayList<TransitionState> createTransitionStates(String[][] nextTransitionsStates) {
        if( nextTransitionsStates == null ) return null;
        ArrayList<TransitionState> transitionStates = new ArrayList<>();
        for( String[] tranState : nextTransitionsStates ){
            transitionStates.add( new TransitionState(tranState) );
        }
        return transitionStates;
    }

    public boolean contains(String intent) {
        if( nextTransitionsStates == null ){
            return true;
        }
        for(TransitionState ts : nextTransitionsStates){
            if( ts.state.equals(intent) || ts.intention.equals(intent) ){
                return true;
            }
        }
        return false;
    }

    public String validate(String transition){
        String result = null;
        int size = nextTransitionsStates.size();
        if( nextTransitionsStates != null ) {
            for (TransitionState ts : nextTransitionsStates) {              //所有的do_interest_elicitation
                if (ts.intention.equals(transition) || ts.intention.equals(Constants.MATCH_ANY_INTENT)) {
                    if ( (result = ts.validate()) != null ) {
                        return result;      //就是ts的state
                    }else if( nextTransitionsStates.indexOf( ts ) == size - 1 ){
                        return ts.state;
                    }
                }
                if( ts.intention.equals(Constants.MATCH_ANY_INTENT) ){  //这一行也没用
                    return ts.state;
                }
//                if(  ts.intention.equals(Constants.BACKUP_WORD) ){  //这一行没用
//                    return ts.intention;
//                }
            }
        }
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if( o instanceof State){
            return Integer.compare( this.order, ((State) o).order);
        }
        throw new IllegalStateException("Trying to add a non-State object to the queue");
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public State clone(){
        State st = new State();
        if( this.name.contains("__")){
            System.currentTimeMillis();
        }
//        int idx = this.name.indexOf("__");
//        st.name = this.name.substring(0, idx >= 0? idx : this.name.length());
        st.name = this.name;
        st.conversationalStrategy = this.conversationalStrategy;
        st.phase = this.phase;
        return st;
    }
}

class TransitionState{
    String state;
    String intention;
    String operation;
    String varName;
    String varValue;

    public TransitionState(String state, String intention) {
        this.state = state;
        this.intention = intention;
    }

    public TransitionState(String[] transState){
        this.intention = transState[0];
        this.state = transState[1];
    }

    public String validate(){
        Model model = Model.getInstance();
        if( operation == null ){
            return null;
        }else{
            if( operation.equals("set") ){
                model.setVariable( varName, varValue );
                return state;
            }else if( operation.equals(">=") ){
                if( model.getType( varName).equals("integer") && Integer.valueOf( model.getValue( varName) )
                        >= Integer.valueOf( varValue) ){
                    return state;
                }
                if( model.getType( varName).equals("double") && Double.valueOf( model.getValue( varName) )
                        >= Double.valueOf( varValue) ){
                    return state;
                }
            }else if( operation.equals("<") ){
                if( model.getType( varName).equals("integer") && Integer.valueOf( model.getValue( varName) )
                        < Integer.valueOf( varValue) ){
                    return state;
                }
                if( model.getType( varName).equals("double") && Double.valueOf( model.getValue( varName) )
                        < Double.valueOf( varValue) ){
                    return state;
                }
            }else if( operation.equals("=") ){
                if( model.getType( varName).equals("integer") && Integer.valueOf( model.getValue( varName) )
                        == Integer.valueOf( varValue) ){
                    return state;
                }
                if( model.getType( varName).equals("double") && Double.valueOf( model.getValue( varName) )
                        == Double.valueOf( varValue) ){
                    return state;
                }
                if( model.getType( varName).equals("boolean") && Boolean.valueOf( model.getValue( varName) )
                        == Boolean.valueOf( varValue) ){
                    return state;
                }
            }else if( operation.equals("equals") ){
                if( model.getType( varName).equals("string") && model.getValue( varName).equals( varValue ) ){
                    return state;
                }else if( varValue.equals("isSessionNotCovered") && !Model.sessionRecommendation ){
                    return state;
                }else if( varValue.equals("isFoodNotCovered") && !Model.foodRecommendation ){
                    return state;
                }else if( varValue.equals("isPersonNotCovered") && !Model.personRecommendation ){
                    return state;
                }else if( varValue.equals("isPartiesNotCovered") && !Model.partiesRecommendation ){
                    return state;
                }else if( varValue.equals("areAllCovered") && Model.sessionRecommendation && Model.foodRecommendation
                        && Model.personRecommendation && Model.partiesRecommendation ){
                    return state;
                }
            }
        }
        return null;
    }
}
