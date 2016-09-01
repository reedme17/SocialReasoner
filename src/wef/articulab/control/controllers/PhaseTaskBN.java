package wef.articulab.control.controllers;

import wef.articulab.model.blackboard.BlackboardListener;
import wef.articulab.model.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by oscarr on 4/29/16.
 */
public class PhaseTaskBN extends BehaviorNetworkController implements BlackboardListener {

    private ArrayList<String> users = new ArrayList();
    private String userName = "";

//    public PhaseTaskBN(){
//        network = new BehaviorNetwork();
//    }
//
//
//    @Override
//    public BehaviorNetworkInterface createBN() {
//        name = "Phase and Task BN";
//
//        modules.add( new Behavior( "GW", "Greeting Word",
//                new String[]{Constants.START_CONVERSATION},
//                new String[]{Constants.GREETING_WORD},
//                new String[]{Constants.START_CONVERSATION} ));
//
//        modules.add( new Behavior( "PACT", "Pleasure at the coming together",
//                new String[]{Constants.GREETING_WORD, Constants.USER_SELF_INTRODUCTION},
//                new String[]{Constants.PLEASURE_COMING_TOGETHER},
//                new String[]{Constants.GREETING_WORD}) );
//
//        modules.add(new Behavior("WOI", "Who One Is",
//                new String[]{Constants.PLEASURE_COMING_TOGETHER},
//                new String[]{Constants.WHO_ONE_IS, Constants.PHASE_INTRODUCTION, Constants.SELF_INTRODUCTION},
//                new String[]{Constants.PLEASURE_COMING_TOGETHER, Constants.USER_SELF_INTRODUCTION}));
//
//        modules.add(new Behavior("AFTA", "Ask First Time Attending WEF",
//                new String[]{Constants.WHO_ONE_IS},
//                    new String[]{Constants.ASK_FIRST_TIME_ATTENDING},
//                new String[]{Constants.WHO_ONE_IS, Constants.SELF_INTRODUCTION}));
//
//        modules.add( new Behavior( "SGE", "Start Goal Elicitation",
//                new String[]{Constants.USER_RESPONDS_FIRST_TIME_ATTENDING},
//                new String[]{Constants.START_GOAL_ELICITATION},
//                new String[]{Constants.ASK_FIRST_TIME_ATTENDING, Constants.USER_RESPONDS_FIRST_TIME_ATTENDING}) );
//
//        modules.add( new Behavior( "EG", "Elicit Goal",
//                new String[]{Constants.START_GOAL_ELICITATION},
//                new String[]{Constants.ELICIT_GOALS},
//                new String[]{Constants.START_GOAL_ELICITATION}) );
//
//        modules.add( new Behavior( "GAK", "Goals Acknowlegedement",
//                new String[]{Constants.ELICIT_GOALS, Constants.USER_RESPONDS_ELICIT_GOALS_PEOPLE},
//                new String[]{Constants.FEEDBACK_FOR_GOALS},
//                new String[]{Constants.ELICIT_GOALS, Constants.USER_RESPONDS_ELICIT_GOALS_PEOPLE}) );
//
//        modules.add( new Behavior( "SPR", "Start Person Recommendation",
//                new String[]{Constants.FEEDBACK_FOR_GOALS},
//                new String[]{Constants.START_PERSON_PRE_RECOMMEND_NOTICE},
//                new String[]{Constants.FEEDBACK_FOR_GOALS}) );
//
//        modules.add( new Behavior( "QAB", "Question About Work",
//                new String[]{Constants.START_PERSON_PRE_RECOMMEND_NOTICE},
//                new String[]{Constants.QUESTION_ABOUT_WORK},
//                new String[]{Constants.START_PERSON_PRE_RECOMMEND_NOTICE}) );
//
//        modules.add( new Behavior( "RAK", "Response Acknowledgement",
//                new String[]{Constants.QUESTION_ABOUT_WORK, Constants.USER_RESPONDS_QUESTION_WORK},
//                new String[]{Constants.FEEDBACK_USER_WORK},
//                new String[]{Constants.QUESTION_ABOUT_WORK, Constants.USER_RESPONDS_QUESTION_WORK}) );
//
//        modules.add( new Behavior( "OR", "Overwhelming Recommendations",
//                new String[]{Constants.FEEDBACK_USER_WORK},
//                new String[]{Constants.START_RECOMMENDATION_NOTICE},
//                new String[]{Constants.FEEDBACK_USER_WORK}) );
//
//        modules.add( new Behavior( "LL", "Let me Look",
//                new String[]{Constants.START_RECOMMENDATION_NOTICE},
//                new String[]{Constants.LET_ME_LOOK},
//                new String[]{Constants.START_RECOMMENDATION_NOTICE}) );
//
//        modules.add( new Behavior( "RMP", "Recommend Participant",
//                new String[]{Constants.LET_ME_LOOK, Constants.USER_REPONDS_LET_ME_LOOK},
//                new String[]{Constants.RECOMMEND_PERSON},
//                new String[]{Constants.LET_ME_LOOK, Constants.USER_REPONDS_LET_ME_LOOK}) );
//
//        modules.add( new Behavior( "ASM", "Ask Somebody to Meet",
//                new String[]{Constants.RECOMMEND_PERSON},
//                new String[]{Constants.ASK_SOMEBODY_TO_MEET},
//                new String[]{Constants.RECOMMEND_PERSON}) );
//
//        modules.add( new Behavior( "SMA", "Suggest Meeting Actions",
//                new String[]{Constants.ASK_SOMEBODY_TO_MEET, Constants.USER_REPONDS_SOMEBODY_TO_MEET},
//                new String[]{Constants.SUGGEST_MEETING_ACTIONS},
//                new String[]{Constants.ASK_SOMEBODY_TO_MEET, Constants.USER_REPONDS_SOMEBODY_TO_MEET}) );
//
//        modules.add( new Behavior( "SMR", "Send a Meeting Request",
//                new String[]{Constants.SUGGEST_MEETING_ACTIONS, Constants.USER_ASK_SEND_MEETING_REQ},
//                new String[]{Constants.SEND_MEETING_REQ},
//                new String[]{Constants.SUGGEST_MEETING_ACTIONS, Constants.USER_ASK_SEND_MEETING_REQ}) );
//
//        modules.add( new Behavior( "YW", "Your Welcome",
//                new String[]{Constants.SEND_MEETING_REQ, Constants.USER_RESPONDS_SEND_MEETING_REQ},
//                new String[]{Constants.YOUR_WELCOME},
//                new String[]{Constants.SEND_MEETING_REQ, Constants.USER_RESPONDS_SEND_MEETING_REQ}) );
//
//        modules.add( new Behavior( "MTD", "Meet more People",
//                new String[]{Constants.YOUR_WELCOME},
//                new String[]{Constants.ASK_MEET_MORE_PEOPLE},
//                new String[]{Constants.YOUR_WELCOME}) );
//
//        modules.add( new Behavior( "RI", "Recommend Industry",
//                new String[]{Constants.ASK_MEET_MORE_PEOPLE, Constants.USER_RESPONDS_CONTINUE_RECOMMENDATIONS},
//                new String[]{Constants.START_RECOMMEND_INDUSTRY},
//                new String[]{Constants.ASK_MEET_MORE_PEOPLE, Constants.USER_RESPONDS_CONTINUE_RECOMMENDATIONS}) );
//
//        modules.add( new Behavior( "SFI", "Session Fashion Industry",
//                new String[]{Constants.START_RECOMMEND_INDUSTRY},
//                new String[]{Constants.ASK_SESSION_INDUSTRY},
//                new String[]{Constants.START_RECOMMEND_INDUSTRY}) );
//
//        modules.add( new Behavior( "WD", "Way Dressed",
//                new String[]{Constants.ASK_SESSION_INDUSTRY, Constants.USER_RESPONDS_FASHION_INDUSTRY},
//                new String[]{Constants.USER_DOESNT_LIKE_SUGGESTION},
//                new String[]{Constants.ASK_SESSION_INDUSTRY, Constants.USER_RESPONDS_FASHION_INDUSTRY}) );
//
//        modules.add( new Behavior( "ECS", "Enjoy Collaborative Session",
//                new String[]{Constants.USER_DOESNT_LIKE_SUGGESTION, Constants.USER_RESPONDS_WAY_DRESSED},
//                new String[]{Constants.RECOMMEND_SESSION_BY_TOPIC},
//                new String[]{Constants.USER_DOESNT_LIKE_SUGGESTION, Constants.USER_RESPONDS_WAY_DRESSED}) );
//
//        modules.add( new Behavior( "DSG", "Description Session Gesture",
//                new String[]{Constants.RECOMMEND_SESSION_BY_TOPIC, Constants.USER_RESPONDS_COLLABORATIVE_SESSION},
//                new String[]{Constants.PRESENT_SESSION_DESCRIPTION},
//                new String[]{Constants.RECOMMEND_SESSION_BY_TOPIC, Constants.USER_RESPONDS_COLLABORATIVE_SESSION}) );
//
//        modules.add( new Behavior( "ALH", "Pre-farewell",
//                new String[]{Constants.PRESENT_SESSION_DESCRIPTION, Constants.USER_RESPONDS_SESSION_GESTURE},
//                new String[]{Constants.PRE_CLOSING},
//                new String[]{Constants.PRESENT_SESSION_DESCRIPTION, Constants.USER_RESPONDS_SESSION_GESTURE}) );
//
//        modules.add( new Behavior( "FFCB", "Feel Free to Come Back",
//                new String[]{Constants.PRE_CLOSING, Constants.USER_RESPONDS_PRE_FAREWELL},
//                new String[]{Constants.TERMINAL_EXCHANGE},
//                new String[]{Constants.PRE_CLOSING, Constants.USER_RESPONDS_PRE_FAREWELL}) );
//
//        NUM_BEHAVIORS = modules.size();
//        NUM_VARIABLES = NUM_BEHAVIORS + 1; // +2 includes the threshold line and behavior activated
//
//        start();
//
//        title = "Conversational Strategy BN";
//        int size = modules.size();
//        series = new String[size+1];
//        for( int i = 0; i < size; i++ ){
//            series[i] = modules.get(i).getName();
//        }
//        series[size] = "Activation";
//        return network;
//    }


    @Override
    public void updateModel(CopyOnWriteArrayList<String> states) {
        network.setState( states );
    }


    public String extractState( String state ){
        String extracted = "";
        if( !userName.equals("") ){
            extracted = Constants.USER_SELF_INTRODUCTION;
            users.add(userName);
            userName = "";
        }
//        if( state.equals( Constants.START_CONVERSATION) ){
//            if( !users.contains( state) ){
//                extracted += (extracted.isEmpty()? "" : ":") + Constants.FIRST_TIME_INTERACION;
//            }
//        }else if( state.equals( Constants.SELF_INTRODUCTION ) ){
//            extracted += (extracted.isEmpty()? "" : ":") + Constants.USER_KNOWS_CHIP;
//        }else if( state.equals( Constants.USER_GREETING_WORD ) ){
//            extracted += (extracted.isEmpty()? "" : ":") + Constants.START_CONVERSATION;
//        }else if( state.equals(Constants.RESET) ){
//            extracted += (extracted.isEmpty()? "" : ":") + Constants.USER_RESPONDS_FIRST_TIME_ATTENDING;
////            reset();
//        }
        return extracted;
    }

    public String removeState( String state ){
        String remove = null;
//        if( state.equals( Constants.PHASE_GREETING ) ){
//            remove = Constants.PHASE_INTRODUCTION + ":" + Constants.PHASE_TASK + ":" + Constants.PHASE_FAREWELL;
//        }else if( state.equals( Constants.PHASE_INTRODUCTION ) ){
//            remove = Constants.PHASE_GREETING + ":" + Constants.PHASE_TASK + ":" + Constants.PHASE_FAREWELL;
//        }else if( state.equals( Constants.PHASE_TASK ) ){
//            remove = Constants.PHASE_INTRODUCTION + ":" + Constants.PHASE_GREETING + ":" + Constants.PHASE_FAREWELL;
//        }else if( state.equals( Constants.PHASE_FAREWELL ) ){
//            remove = Constants.PHASE_INTRODUCTION + ":" + Constants.PHASE_GREETING + ":" + Constants.PHASE_TASK;
//        }
        return remove;
    }

    private void start(){
        // Initial state and goals
        states = new CopyOnWriteArrayList<>( Arrays.asList(new String[]{Constants.ASN_SYSTEM}));
        goals = new Vector( Arrays.asList(new String[]{Constants.GOAL_FINISH_GREETING, Constants.GOAL_FINISH_INTRODUCTION, Constants.GOAL_FINISH_FAREWELL, Constants.GOAL_FINISH_TASK}));

        network.setGoals(goals);
        network.setPi(20);
        network.setTheta(15);
        network.setInitialTheta(15);
        network.setPhi(50);
        network.setGamma(40);
        network.setDelta(50);

        network.setModules(modules, NUM_VARIABLES);
        network.setState(states);
        network.setGoalsR(new Vector<>());

//        reset();
    }

//    private void reset(){
//        for(BehaviorInterface beh : modules){
//            beh.setActivation(0);
//            beh.setActivated(false);
//        }
//    }
}
