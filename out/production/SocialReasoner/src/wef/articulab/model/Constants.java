package wef.articulab.model;

/**
 * Created by oscarr on 4/28/16.
 */
public class Constants {
    public static final String SD_USER = "SD_user";
    public static final String SE_USER = "SE_user";
    public static final String LOW_RAPPORT = "low_rapport";
    public static final String NO_PR_USER = "no_PR_user";
    public static final String PR_USER = "PR_user";
    public static final String QESD_USER = "QESD_user";
    public static final String ASN_USER = "ASN_user";
    public static final String BC_USER = "BC_user";
    public static final String VSN_USER = "VSN_user";

    public static final String HIGH_RAPPORT = "high_rapport";
    public static final String INCREASE_RAPPORT = "increase_rapport";


    public static final String SELF_INTRODUCTION = "SELF_INTRODUCTION";
    public static final String USER_SELF_INTRODUCTION = "introduction";
    public static final String USER_KNOWS_CHIP = "USER_KNOWS_CHIP";
    public static final String RECOMMEND_SESSION = "recommend_session";
    public static final String USER_RESPONDS_FIRST_TIME_ATTENDING = "USER_RESPONDS_FIRST_TIME_ATTENDING";
    public static final String USER_RESPONDS_ELICIT_GOALS_PEOPLE = "USER_RESPONDS_ELICIT_GOALS_PEOPLE";
    public static final String START_GOAL_ELICITATION = "START_GOAL_ELICITATION";
    public static final String START_PERSON_PRE_RECOMMEND_NOTICE = "START_PERSON_PRE_RECOMMEND_NOTICE";
    public static final String USER_RESPONDS_QUESTION_WORK = "USER_RESPONDS_QUESTION_WORK";
    public static final String RECOMMEND_PERSON = "recommend_people";
    public static final String USER_REPONDS_SOMEBODY_TO_MEET = "USER_REPONDS_SOMEBODY_TO_MEET";
    public static final String YOUR_WELCOME = "YOUR_WELCOME";
    public static final String START_RECOMMEND_INDUSTRY = "START_RECOMMEND_INDUSTRY";
    public static final String USER_RESPONDS_FASHION_INDUSTRY = "USER_RESPONDS_FASHION_INDUSTRY";
    public static final String USER_RESPONDS_SESSION_GESTURE = "USER_RESPONDS_SESSION_GESTURE";
    public static final String USER_RESPONDS_PRE_FAREWELL = "USER_RESPONDS_PRE_FAREWELL";
    public static final String GOAL_FINISH_TASK = "GOAL_FINISH_TASK";
    public static final String NOT_BC_HISTORY = "not_BC_history";
    public static final String RESET = "reset";
    public static final String REMOVE_ALL = "REMOVE_ALL";
    public static final String POSITIVE_CONFIRMATION = "positive_confirmation";
    public static final String NEGATIVE_CONFIRMATION = "negative_confirmation";
    public static final String FIND_PERSON = "find_person";
    public static final String SEARCH_PEOPLE = "search_people";
    public static final String USER_TELLS_ABOUT_WORK = "user_tells_about_work";
    public static final String GRATITUDE = "gratitude";
    public static final String LIKE = "like";
    public static final String WORK_INTEREST = "work_interest";
    public static final String PRESENT_WORK = "present_work";
    public static final String DISLIKE = "dislike";
    public static final String PERSON = "person";
    public static final String SESSION = "session";
    public static final String FEEDBACK_LIKE_WORK = "feedback_like_work";
    public static final String FEEDBACK_DISLIKE_WORK = "feedback_dislike_work";
    public static final String MULTIPLE_GOALS = "multiple_goals";
    public static final String MEDIUM_RAPPORT = "medium_rapport";
    public static final String SELF_DISCLOSURE = "Self-Disclosure";
    public static final String ADHERE_SOCIAL_NORM = "Adhere Social Norm";
    public static final String QUESTION_ELICIT_SD = "Question to Elicit SD";
    public static final String SHARED_EXPERIENCES = "Shared Experiences";
    public static final String PRAISE = "Praise";
    public static final String VIOLATION_SOCIAL_NORM = "Violation Social Norm";
    public static final String BACK_CHANNEL = "Back-Channel";
    public static final String FAREWELL = "farewell";
    public static final String PRE_FAREWELL = "pre_farewell";
    public static final String REAL_FAREWELL = "real_farewell";

    // synchronization between TR and SR
    public static final int SYNC_SEQUENCE_START = 1;
    public static final int SYNC_AFTER_TR_DECISION = 2;
    public static final int SYNC_AFTER_UPDATE_NETWORK_STATE = 3;
    public static final int SYNC_AFTER_SELECT_BEHAVIOR = 4;

    public static final String SYSTEM_INTENT = "system_intent";
    public static final String USER_INTENT = "user_intent";
    public static final String GOAL_NOT_SUPPORTED = "goal_not_identified";
    public static final String MATCH_ANY_INTENT = "match_any_intent";
    public static final String BACKUP_WORD = "backup_word";

    public static final String FIND_SESSION_DETAIL = "find_session_detail";
    public static final String RECOMMEND_PEOPLE = "recommend_people";
    public static final String RECOMMEND_FOOD = "recommend_food";
    public static final String RECOMMEND_PARTY = "recommend_party";
    public static final String POPULAR_SESSIONS = "popular_sessions";
    public static final String SELF_NAMING = "self_naming";
    public static final String START = "start_greeting";
    public static final String THANKS = "thanks";
    public static final String STOP = "stop";
    public static final String NONE = "none";
    public static final String FEEDBACK_GOAL_NOT_IDENTIFIED = "feedback_goal_not_identified";
    public static final String RECOMMEND_SESSIONS = "recommend_sessions";
    public static final String NEXT_GOAL = "next_goal";
    public static final java.lang.String NO_GOAL = "no_goal";
    public static final String ONE_GOAL = "1_goal";
    public static final String FEEDBACK_GOAL_ELICITATION_ONE_GOAL = "feedback_goal_elicitation_1_goal";
    public static final String FEEDBACK_GOAL_ELICITATION_MULTIPLE_GOAL = "feedback_goal_elicitation_multiple_goal";
    public static final String ASK_FOR_YES_OR_NO = "system_ask_for_yes/no";

    /**
     * newly changed ... from RECOMMEND_PEOPLE to REQUEST_PERSON_RECOMMENDATION
     */
    public static final String REQUEST_PERSON_RECOMMENDATION = "request_person_recommendation";
    public static final String REQUEST_SESSION_RECOMMENDATION = "request_session_recommendation";
    public static final String REQUEST_FOOD_RECOMMENDATION = "request_food_recommendation";
    public static final String REQUEST_PARTY_RECOMMENDATION = "request_party_recommendation";

    public static final String DONT_UNDERSTAND = "dont_understand";
    public static final String ASK_FOR_CONFIRMATION = "ask_for_confirmation";
    public static final String SYSTEM_DONT_UNDERSTAND = "system_dont_understand";
    public static final String SYSTEM_ASK_FOR_CONFIRMATION = "system_ask_for_confirmation";

    public static final String SYSTEM_DONT_UNDERSTAND_1P = "system_dont_understand_1p";
    public static final String SYSTEM_ASK_FOR_CONFIRMATION_1P = "system_ask_for_confirmation_1p";
    public static final String SYSTEM_DONT_UNDERSTAND_MP = "system_dont_understand_mp";
    public static final String SYSTEM_ASK_FOR_CONFIRMATION_MP = "system_ask_for_confirmation_mp";
    public static final String SYSTEM_DONT_UNDERSTAND_1S = "system_dont_understand_1s";
    public static final String SYSTEM_ASK_FOR_CONFIRMATION_1S = "system_ask_for_confirmation_1s";
    public static final String SYSTEM_DONT_UNDERSTAND_MS = "system_dont_understand_ms";
    public static final String SYSTEM_ASK_FOR_CONFIRMATION_MS = "system_ask_for_confirmation_ms";



    // Phases
    public static String PHASE_GREETING =  "phase_greetings";
    public static String PHASE_GOAL_ELICITATION = "phase_goal_elicitation";

    // Greeting Phase
    public static String GREETING_WORD =  "greeting";
    public static String FIRST_TIME_INTERACION =  "FIRST_TIME_INTERACION";
    public static final String WHO_ONE_IS = "WHO_ONE_IS";
    public static final String PLEASURE_COMING_TOGETHER = "pleasure_coming_together";


    // Phases Goals
    public static String GOAL_FINISH_GREETING =  "GOAL_FINISH_GREETING";
    public static String GOAL_FINISH_INTRODUCTION =  "GOAL_FINISH_INTRODUCTION";
    public static String GOAL_FINISH_FAREWELL =  "GOAL_FINISH_FAREWELL";




    // Conversational Strategy SYSTEM
    public static String SD_SYSTEM =  "SD_system";
    public static String QESD_SYSTEM =  "QESD_system";
    public static String RSE_SYSTEM =  "RSE_system";
    public static String PR_SYSTEM =  "PR_system";
    public static String VSN_SYSTEM =  "VSN_system";
    public static String ASN_SYSTEM =  "ASN_system";
    public static String BC_SYSTEM =  "BC_system";

    // Social History
    public static String SD_HISTORY =  "SD_history";
    public static String QESD_HISTORY =  "QESD_history";
    public static String RSE_HISTORY =  "RSE_history";
    public static String PR_HISTORY =  "PR_history";
    public static String VSN_HISTORY =  "VSN_HISTORY";
    public static String ASN_HISTORY =  "ASN_history";
    public static String BC_HISTORY =  "BC_history";
    public static String SEVERAL_ASN_HISTORY =  "several_ASN_HISTORY";

    // Acoustic Features
    // Jitter
    // Shimmer
    public static String HIGH_SHIMMER = "high_shimmer";
    public static String HIGH_JITTER = "high_jitter";


    public static String AVAILABLE_SHARED_EXPERIENCES = "available_shared_experiences";

    
}
