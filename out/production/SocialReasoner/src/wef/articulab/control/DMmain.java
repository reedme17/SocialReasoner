package wef.articulab.control;

import wef.articulab.control.controllers.*;
import wef.articulab.control.reasoners.*;
import wef.articulab.control.util.Utils;
import wef.articulab.control.vht.VHTConnector;
import wef.articulab.view.emulators.InputController;
import wef.articulab.model.*;
import wef.articulab.model.blackboard.Blackboard;
import wef.articulab.model.history.SocialHistory;
import wef.articulab.view.ui.Visualizer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by oscarr on 4/22/16.
 */
public class DMmain {
    private BehaviorNetworkController bnt1;
    public static TaskReasoner taskReasoner;
    private SocialReasoner socialReasoner;

    private static Visualizer visualizer;
    public static Model model;
    public static boolean noInputFlag = true;
    public static Blackboard blackboard;
    public static SocialHistory socialHistory;
    public static UserModel userModel;
    public static String[] conversationalStrategies = new String[7];
    public static String behavior;
    public static boolean pause;
    public static DMmain DMmain;
    public static InputController inputController;
    public static boolean stop = false;
    public static int syncStep = Constants.SYNC_SEQUENCE_START;
    public static Queue<State> queue;
    public static Properties properties = new Properties();
    public static String log = "";


    //flags
    public static boolean useWoZFlag = false;
    public static boolean flagStart = true;
    public static boolean flagStop = false;
    public static boolean flagReset = false;
    public static boolean flagResetTR = false;
    public static boolean useVHTConnnector;
    public static boolean useFakeNLU;
    public static boolean useTRNotWoZ;
    public static boolean useFSM;
    public static boolean useSRPlot;
    public static boolean useManualMode;
    public static boolean useDummyGoals;
    public static boolean useOscarWEFConnector;
    public static boolean sendMessagesToNLG;

    // preconditions
    public static String rapportLevel; // = Constants.HIGH_RAPPORT;
    public static double rapportScore = 4; // = 6;
    public static String userConvStrategy; // = Constants.VIOLATION_SOCIAL_NORM;

    //delays
    public static long delayMainLoop;
    public static long delayUserIntent;
    private static long delaySystemIntent;

    private Lock lock;
    private Condition modelModifiedByTR;
    private boolean isFirstTime = true;
    private Thread taskReasonerThread;


    public static void main(String args[]){
        if( args.length > 0){
            useVHTConnnector = true;
            VHTConnector.serverIP = args[0];
            if( args.length > 1){
                delayMainLoop = Long.valueOf(args[1]);
            }
        }
        DMmain = new DMmain();
        DMmain.loadProperties();
        if( useVHTConnnector ) {
            VHTConnector.getInstance();
        }
        while( !stop ) {
            DMmain.start();
        }
        System.err.println("Bye bye...");
        System.exit(0);
    }

    public void start(){
        checkStart();
        while ( !flagReset && !stop){
            if( !pause ) {
                socialReasoner.execute();
            }
            Utils.sleep(delayMainLoop);
            checkReset();
        }
    }

    private void createSocialReasoner() {
        socialReasoner = SocialReasoner.getInstance( bnt1, "ConversationalStrategyBN", lock, this, modelModifiedByTR );
        socialReasoner.setModel( model );
    }

    private void checkStart() {
        // waiting for confirmation to start the reasoning process
        boolean insideLoop = false;
        while( !DMmain.flagStart ){
            Utils.sleep( 100 );
            insideLoop = true;
        }
        if( insideLoop || isFirstTime ){
            System.out.println("\nRe-starting...");
            queue = new PriorityQueue<>();
            lock = new ReentrantLock();
            modelModifiedByTR = lock.newCondition();

            //singletons
            blackboard = Blackboard.getInstance();
            socialHistory = SocialHistory.getInstance();
            userModel = UserModel.getInstance();                                        //UserModel
            System.out.println("Creating a user model...... Done!!!!!!!!!!!!!!");
            visualizer = Visualizer.getInstance();
            taskReasoner = TaskReasoner.getInstance(lock, modelModifiedByTR, this);

            bnt1 = new ConversationalStrategyBN();
            blackboard.setModel( bnt1.getStatesList() );
            blackboard.subscribe((ConversationalStrategyBN) bnt1);
            model = taskReasoner.getModel();
            userConvStrategy = model.getInitialUserConvStrat();
            rapportLevel = model.getInitialRapportLevel();
            if( useSRPlot ) {
                visualizer.initializePlot(this, bnt1, null);
                inputController = visualizer.getInputController();
            }else{
                inputController = new InputController(null);
            }
            if( useVHTConnnector ){
                VHTConnector.setInputController(inputController);
                taskReasoner.setVhtConnector();
            }
            createSocialReasoner();
            executeFSM();
            isFirstTime = false;
            flagReset = false;
        }
    }

    private void checkReset() {
        if( flagReset ){
            Blackboard.reset();
            TaskReasoner.reset();
            SocialReasoner.reset();
            SocialHistory.reset();
            noInputFlag = true;
            conversationalStrategies = new String[7];
            pause = false;
            stop = false;
            syncStep = Constants.SYNC_SEQUENCE_START;
            queue.clear();
            queue = null;
            flagStop = false;
            flagStart = false;
            flagResetTR = false;
            System.out.println("Reseting...");
            reset();
            System.gc();
        }
    }

    private void reset() {
        taskReasonerThread.interrupt();
        taskReasonerThread = null;
        modelModifiedByTR = null;
        lock = null;
        bnt1 = null;
        taskReasoner = null;
        socialReasoner = null;
        visualizer = null;
        model = null;
        noInputFlag = true;
        blackboard = null;;
        socialHistory = null;
        conversationalStrategies = new String[7];
        syncStep = Constants.SYNC_SEQUENCE_START;
        queue = null;
        log = "";
    }

    /********************************** TASK REASONER THREAD ********************************/
    private void executeFSM() {
        taskReasonerThread = new Thread(){
            public void run(){
                while( !stop && !flagReset ) {
                    taskReasoner.executeTaskReasoner();
                    Utils.sleep(delaySystemIntent);
                }
                System.out.println("Stopping Task Reasoner");
            }
        };
        taskReasonerThread.start();
    }
    /********************************** END TASK REASONER **********************************/

    public void checkSyncSequence(int nextSyncStep) {
        if( nextSyncStep == syncStep + 1){
            syncStep = nextSyncStep;
        }
    }

    public static String calculateRapScore(String score) {
        try {
            rapportScore = Double.parseDouble(score);
            rapportLevel = (rapportScore == 4? Constants.MEDIUM_RAPPORT : rapportScore > 3 ? Constants.HIGH_RAPPORT
                    : Constants.LOW_RAPPORT);
            return rapportLevel;
        }catch (Exception e){
            return null;
        }
    }

    public void addContinousStates() {
        socialHistory.addStates();
        blackboard.setStatesString( rapportLevel, "DMmain");
        Blackboard.getInstance().setStatesString((userConvStrategy.equals(Constants.SELF_DISCLOSURE) ? Constants.SD_USER + ":" : "") +
                        (userConvStrategy.equals(Constants.SHARED_EXPERIENCES) ? Constants.SE_USER + ":" : "") +
                        (userConvStrategy.equals(Constants.PRAISE) ? Constants.PR_USER + ":" : Constants.NO_PR_USER + ":") +
                        (userConvStrategy.equals(Constants.VIOLATION_SOCIAL_NORM) ? Constants.VSN_USER + ":" : "") +
                        (userConvStrategy.equals(Constants.QUESTION_ELICIT_SD) ? Constants.QESD_USER + ":" : "") +
                        (userConvStrategy.equals(Constants.ADHERE_SOCIAL_NORM) ? Constants.ASN_USER + ":" : "") +
                        (userConvStrategy.equals(Constants.BACK_CHANNEL) ? Constants.BC_USER + ":" : ""),
                "DMmain");
    }

    private void loadProperties(){
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            properties.load(input);

            // get the property value and print it out
            useVHTConnnector = Boolean.valueOf(properties.getProperty("useVHTConnnector"));
            useFakeNLU = Boolean.valueOf(properties.getProperty("useFakeNLU"));
            useTRNotWoZ = Boolean.valueOf(properties.getProperty("useTRNotWoZ"));
            useFSM = Boolean.valueOf(properties.getProperty("useFSM"));
            useSRPlot = Boolean.valueOf(properties.getProperty("useSRPlot"));
            useManualMode = Boolean.valueOf(properties.getProperty("useManualMode"));
            delayMainLoop = Long.valueOf(properties.getProperty("delayMainLoop"));
            delayUserIntent = Long.valueOf(properties.getProperty("delayUserIntent"));
            delaySystemIntent = Long.valueOf(properties.getProperty("delaySystemIntent"));
            useDummyGoals = Boolean.valueOf(properties.getProperty("useDummyGoals"));
            useOscarWEFConnector = Boolean.valueOf(properties.getProperty("useOscarWEFConnector"));
            flagStart = Boolean.valueOf(properties.getProperty("shouldStartAutomatically"));
            sendMessagesToNLG = Boolean.valueOf(properties.getProperty("sendMessagesToNLG"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addToLog(String input) {
        System.out.println(input);
        log += input + "\n";
    }

    public static void storeLog() {
        try (PrintStream out = new PrintStream(new FileOutputStream("" + getDateString() + ".log"))) {
            out.print( log );
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getDateString(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH.mm");
        return format.format( new Date() );
    }
}
