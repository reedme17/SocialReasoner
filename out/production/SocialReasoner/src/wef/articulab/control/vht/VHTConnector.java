package wef.articulab.control.vht;

import edu.usc.ict.vhmsg.MessageEvent;
import edu.usc.ict.vhmsg.MessageListener;
import edu.usc.ict.vhmsg.VHMsg;
import wef.articulab.control.DMmain;
import wef.articulab.control.reasoners.State;
import wef.articulab.control.reasoners.TaskReasoner;
import wef.articulab.control.reasoners.UserModel;
import wef.articulab.control.util.Utils;
import wef.articulab.model.Constants;
import wef.articulab.model.SocialReasonerOutput;
import wef.articulab.model.blackboard.Blackboard;
import wef.articulab.model.messages.DMOutputMessage;
import wef.articulab.model.nlu.NLUOutput;
import wef.articulab.model.recommendation.RecommendationInterface;
import wef.articulab.model.recommendation.session.Session;
import wef.articulab.view.emulators.InputController;

import java.util.List;

import static wef.articulab.model.Constants.REQUEST_FOOD_RECOMMENDATION;


public class VHTConnector implements MessageListener{
    private VHMsg sender;
    private VHMsg receiver;
    private DMmain DMmain;
    private InputController inputEmulator;
    private Blackboard blackboard;

    private static String sendMsgToNLG;
    private static String sendMsgToClassifier;
    private static String sendMsgReqRecom;
    private static String sendMsgTopLink;
    private static String sendRequestOscarWEFConnector;
    private static String sendMsgActivations;
    private static String receiveRapEst;
    private static String receiveConvStrat;
    private static String receiveUserIntent;
    private static String receiveASR;
    private static String receiveTRControl;
    private static String receiveRecomResults;
    private static String receiveTRExecutionControl;
    private static String receiveResultFromOscarWEFConn;
    private static String receiveNLU;
    public static UserModel userModel;                             //UserModel
    public static String flagKeyword;
    public static String getFlagKeyword() {
        return  flagKeyword;
    }

    private static VHTConnector instance;
    public static String serverIP;
    private String recommendationType;

    private VHTConnector(){
        loadProperties();
        blackboard = Blackboard.getInstance();
        userModel = new UserModel();
        sender = new VHMsg();
        receiver = new VHMsg();
        boolean ret = sender.openConnection(serverIP);
        if ( !ret ){
            System.out.println( "Connection error!" );
            return;
        }
        receiver.openConnection(serverIP);
        receiver.enableImmediateMethod();
        receiver.addMessageListener( this );
        subscribe(receiveRapEst);
        subscribe(receiveConvStrat);
//        subscribe(receiveASR);
        subscribe(receiveUserIntent);
        subscribe(receiveTRControl);
        subscribe(receiveRecomResults);
        subscribe(receiveNLU);
        subscribe(receiveTRExecutionControl);
        subscribe(receiveResultFromOscarWEFConn);
    }

    public static void setInputController(InputController inputController) {
        instance.inputEmulator = inputController;
    }

    private void loadProperties() {
        sendMsgToNLG = DMmain.properties.getProperty("sendMsgToNLG");
        sendMsgToClassifier = DMmain.properties.getProperty("sendMsgToClassifier");
        sendMsgReqRecom = DMmain.properties.getProperty("sendMsgReqRecom");
        sendMsgTopLink = DMmain.properties.getProperty("sendMsgTopLink");
        sendRequestOscarWEFConnector = DMmain.properties.getProperty("sendRequestOscarWEFConnector");
        sendMsgActivations = DMmain.properties.getProperty("sendMsgActivations");
        receiveRapEst = DMmain.properties.getProperty("receiveRapEst");
        receiveConvStrat = DMmain.properties.getProperty("receiveConvStrat");
        receiveUserIntent = DMmain.properties.getProperty("receiveUserIntent");
        receiveASR = DMmain.properties.getProperty("receiveASR");
        receiveTRControl = DMmain.properties.getProperty("receiveTRControl");
        receiveRecomResults = DMmain.properties.getProperty("receiveRecomResults");
        receiveNLU = DMmain.properties.getProperty("receiveNLU");
        receiveTRExecutionControl = DMmain.properties.getProperty("receiveTRExecutionControl");
        receiveResultFromOscarWEFConn = DMmain.properties.getProperty("receiveResultFromOscarWEFConn");
        serverIP = DMmain.properties.getProperty("vhtIPAddress");
    }

    public static VHTConnector getInstance(){
        if( instance == null && wef.articulab.control.DMmain.useVHTConnnector){
            instance = new VHTConnector();
        }
        return instance;
    }

    public void setDMmain(DMmain DMmain) {
        this.DMmain = DMmain;
    }

    public void subscribe(String typeMessage){
        receiver.subscribeMessage( typeMessage );
    }


    public void sendToNLG(List<State> states, List recoResults){
        //String intention = model.transformSystemIntent(state.name);
        State state = states.get( states.size() -1);
        String phase = state.phase.substring( state.phase.indexOf("_") + 1 );
        sender.sendMessage(sendMsgToNLG + " " +createJsonFormat( states, phase, recoResults) );
        sender.sendMessage(sendMsgToClassifier + " 0 " + DMmain.conversationalStrategies[0]);
    }

    public void sendActivations(SocialReasonerOutput output){
        String json = Utils.toJson( output );
        sender.sendMessage( sendMsgActivations + " 0 " + json);
    }

    public void sendTest( String type, String message ){
        sender.sendMessage( type + " " + message );
    }

    //输入到JLogger里的是event
   public void messageAction( MessageEvent event ){
       String message = event.toString();
       flagKeyword = event.toString();

       if( !DMmain.flagReset && DMmain.flagStart ) {
           if (message.startsWith(receiveRapEst)) {
               extractRapportEstimator(message);
           } else if (message.startsWith(receiveASR)) {
               extractASRoutput(message.substring(receiveASR.length() + 1));
           } else if (message.startsWith(receiveUserIntent)) {
               extractUserIntent(message.substring(receiveUserIntent.length() + 1));
           } else if (message.startsWith(receiveConvStrat)) {
               extractConversationalStrategy(message.substring(receiveConvStrat.length() + 1));
           } else if (message.startsWith(receiveTRControl)) {
               extractTaskReasonerControl(message.substring(receiveTRControl.length() + 1));
           } else if (message.startsWith(receiveRecomResults)) {
               extractRecommendationResults(message.substring(receiveRecomResults.length() + 1));
           } else if (message.startsWith(receiveNLU)) {                     //这个if成立!!
               extractNLUOutput(message.substring(receiveNLU.length() + 1)); //这method会输出System intent
           } else if (message.startsWith(receiveResultFromOscarWEFConn)) {
               extractRecommResultsOscarWEFConn(message.substring(receiveResultFromOscarWEFConn.length() + 1));
               System.err.println(event.toString());
           }
       }
       if (message.startsWith(receiveTRExecutionControl)) {
           extractExecutionControl(message.substring(receiveTRExecutionControl.length() + 1));
       }
   }

    private void extractRecommResultsOscarWEFConn(String response) {
        TaskReasoner.getInstance().extractRecommResultsFromOscarWEFConn(response);
    }

    private void extractExecutionControl(String message) {
        if( message.contains("start") ){
            DMmain.flagStart = true;
        }else if( message.contains( Constants.RESET ) ){
            TaskReasoner.getInstance().sendSystemIntents();
            DMmain.flagReset = true;
        }else if( message.contains("stop") ){
            DMmain.stop = true;
        }
    }

    private void extractNLUOutput(String nluOutputString) {
        if( !DMmain.useFakeNLU ) {                                  //用的是真的NLU
            String agentId = nluOutputString.substring(0, 1);
            String json = nluOutputString.substring(2);
            if( json.contains("vrNlu") ){       //ignore
                json = json.substring(json.lastIndexOf("vrNlu") + 8);
            }
            NLUOutput nluOutput = Utils.fromJsonString(json, NLUOutput.class);
            TaskReasoner.getInstance().processNLUOutput(nluOutput);
//            receiver.unsubscribeMessage(receiveASR);


        }else{
            receiver.unsubscribeMessage(receiveNLU);
        }

    }

    private void extractRecommendationResults(String response) {
        TaskReasoner.getInstance().extractRecommendationResults(response);
    }

    private void extractASRoutput(String message) {
        if( DMmain.useFakeNLU ) {
            message = message.replace(".", "");
            message = message.substring(2);
            if( DMmain.useSRPlot ) {
                inputEmulator.extractInputa(message);
            }
        }
    }

    private void extractTaskReasonerControl(String message){
        System.out.println("TaskReasonerControl message: " + message);
        String[] input = message.split(" ");
        DMmain.useTRNotWoZ = Boolean.valueOf(input[1]);
        if( DMmain.useTRNotWoZ ) { //input.length > 2 && !input[2].equals("-")
            TaskReasoner.inputFromUser = input[2];
            System.out.println("Wozing: " + TaskReasoner.inputFromUser);
            DMmain.queue.clear();
            DMmain.useWoZFlag = true;
            TaskReasoner.getInstance().processIntent();
            DMmain.noInputFlag = false;
        }
    }

    private void extractUserIntent(String message) {
        message = message.replace(".", "");
        TaskReasoner.inputFromUser = message;
        DMmain.noInputFlag = false;
    }

    private void extractRapportEstimator(String message) {
        String[] values = message.split(" ");
        String agentId = values[1];
        DMmain.calculateRapScore(values[2]);
//        DMmain.addToLog("Rapport score: " + DMmain.rapportScore);
        blackboard.setStatesString( DMmain.rapportLevel, "RapportEstimator");
        if( DMmain.useSRPlot ) {
            inputEmulator.getInputEmulator().rapportScoreTA.setText(DMmain.rapportScore + " : " + DMmain.rapportLevel);
        }
    }

    private void extractConversationalStrategy(String message) {
        String[] values = message.split(" ");
        String agentId = values[1];
        System.err.println("User's CS: " + message);
        boolean sd = Boolean.parseBoolean( values[3] );
        boolean se = Boolean.parseBoolean( values[6] );
        boolean pr = Boolean.parseBoolean( values[9] );
        boolean vsn = Boolean.parseBoolean( values[12] );

        blackboard.setStatesString( (sd? Constants.SD_USER +":" : "") +
                (se? Constants.SE_USER +":" : "") +
                (pr? Constants.PR_USER +":" : Constants.NO_PR_USER +":") +
                (vsn? Constants.VSN_USER+":" : ""),
                "ConversationalStrategyClassifier");
        String winner = "NONE";
        double max = 0;
        for(int i = 3; i < 11; i=i+3) {
            if (Double.valueOf(values[i]) > max) {
                max = Double.valueOf(values[i]);
                winner = values[i];
            }
            if (DMmain.useSRPlot){
                inputEmulator.getInputEmulator().ucsTextArea.setText(winner);
            }
        }
    }

    public void sendRecommendationRequest(String recommendationType, List<String> args){        //args = state.keywords
        this.recommendationType = recommendationType;
        String sufix = "userId=0&limit=10";
        String keywords = "";
        String criteria = "";

        if( recommendationType.equals(Constants.REQUEST_SESSION_RECOMMENDATION) ) {
            if( args != null ) {
                sufix += "&topic=";
                for (String tp : args) {
                    sufix += tp + ",";
                    keywords += tp + ";";
                }
                sufix = sufix.substring(0, sufix.length() - 1);
            }
            criteria = "sessions";
            recommendationType = Constants.REQUEST_SESSION_RECOMMENDATION;

        }else if( recommendationType.equals(Constants.REQUEST_PERSON_RECOMMENDATION) ) {
            if( args != null) {
                sufix += "&topic=";
                for (String tp : args) {
                    sufix += tp + ";";
                    keywords += tp + ";";
                }
                sufix = sufix.substring(0, sufix.length() - 1);
            }
            criteria = "people";

        }else if( recommendationType.equals(REQUEST_FOOD_RECOMMENDATION) ) {
            if( args != null) {
                sufix += "&topic=";
                for (String tp : args) {
                    sufix += tp + ";";
                    keywords += tp + ";";
                }
                sufix = sufix.substring(0, sufix.length() - 1);
            }
            criteria = "food";

        }else if( recommendationType.equals(Constants.WORK_INTEREST)) {              //work_interest
            if (args != null) {
                sufix += "&topic=";
                for (String tp : args) {
                    sufix += tp + ";";
                    keywords += tp + ";";
                }
                sufix = sufix.substring(0, sufix.length() - 1);
            }
            criteria = "people";
        }


        keywords = keywords.replaceAll("null;", "");
        keywords = keywords.replaceAll("null,", "");
        keywords = keywords.replaceAll("null", "");

        sufix = sufix.replaceAll("null;", "");
        sufix = sufix.replaceAll("null,", "");
        sufix = sufix.replaceAll("null", "");

        //sufix = sufix.substring( 0, sufix.length() - 1 );

        System.out.println("Keywords: " + keywords);
        if( DMmain.useOscarWEFConnector ){      //false, ignore
            sender.sendMessage(sendRequestOscarWEFConnector + " " + criteria + " " + keywords);
        }else {
            sender.sendMessage(sendMsgReqRecom + " " + recommendationType + " " + sufix);
        }// vrRecommendation request_session_recommendation userId=0&limit=10&topic=deep learning&topic=dialogue system
    }    // Console上输出的结果是Keywords: deep learning;dialogue system




    public void sendMessageToTopLink(RecommendationInterface recommendationData){
        String type = recommendationData instanceof Session ? "session" : "people";
        sender.sendMessage( sendMsgTopLink + " " + type + " " + Utils.toJson( recommendationData) );
    }



    private String createJsonFormat(List<State> intents, String phase, List results){
        DMOutputMessage dmMessage = new DMOutputMessage();
        dmMessage.setPhase( phase );
        dmMessage.setRapport( DMmain.rapportScore );
        for(State st : intents){
            dmMessage.addIntent(st);
        }
        dmMessage.addFields(recommendationType, results);
        String resultsString = Utils.toJson(dmMessage);
        return resultsString;
    }
}
