package wef.articulab.control.reasoners;

import wef.articulab.control.DMmain;
import wef.articulab.control.bn.BehaviorNetworkPlus;
import wef.articulab.control.bn.BehaviorPlus;
import wef.articulab.control.controllers.BehaviorNetworkController;
import wef.articulab.control.vht.VHTConnector;
import wef.articulab.model.Constants;
import wef.articulab.model.SocialReasonerOutput;
import wef.articulab.model.blackboard.Blackboard;
import wef.articulab.model.history.SocialHistory;
import wef.articulab.view.ui.Visualizer;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by oscarr on 6/3/16.
 */
public class SocialReasoner {
    private BehaviorNetworkController controller;
    private BehaviorNetworkPlus network;
    private String name;
    private Blackboard blackboard;
    private Lock lock;
    private DMmain DMmain;
    private int cycles = 0;
    private final int maxNumCycles = 4;
    private Model model;
    private State current;
    private VHTConnector vhtConnector;
    private Condition modelModifiedByTR;
    private SocialHistory socialHistory;
    private Visualizer visualizer;
    private TaskReasoner taskReasoner;
    private static SocialReasoner instance;
    private int stepCount;
    private boolean flagSentSROutput = false;

    private SocialReasoner(BehaviorNetworkController bnt, String name, Lock lock, DMmain DMmain, Condition modelModifiedByTR){
        this.controller = bnt;
        this.network = bnt.getNetwork();
        this.name = name;
        this.lock = lock;
        this.modelModifiedByTR = modelModifiedByTR;
        this.DMmain = DMmain;
        this.blackboard = Blackboard.getInstance();
        this.vhtConnector = VHTConnector.getInstance();
        this.socialHistory = SocialHistory.getInstance();
        this.visualizer = Visualizer.getInstance();
        initialize();
        blackboard.setStatesString( bnt.getStates(), bnt.getName() );
    }

    public static SocialReasoner getInstance(BehaviorNetworkController bnt, String name, Lock lock, DMmain DMmain, Condition modelModifiedByTR){
        if( instance == null ){
            instance = new SocialReasoner( bnt, name, lock, DMmain, modelModifiedByTR);
        }
        return instance;
    }

    public static SocialReasoner getInstance(){
        return instance;
    }

    public void execute(){
        lock.lock();
        try {
            network.setState(blackboard.getModel());
            DMmain.checkSyncSequence(Constants.SYNC_AFTER_UPDATE_NETWORK_STATE);
            int idx = network.selectBehavior();
            DMmain.checkSyncSequence(Constants.SYNC_AFTER_SELECT_BEHAVIOR);
            if ( DMmain.syncStep == Constants.SYNC_AFTER_SELECT_BEHAVIOR
                    && ( (idx >= 0 && cycles > 0) || cycles >= maxNumCycles) ){
                if( !DMmain.queue.isEmpty() ){
                    if (idx < 0 && cycles >= maxNumCycles) {
                        network.getHighestActivationUsingNone(); // NONE // previously: network.getHighestActivation();
//                        System.out.println("** Calculating the highest");
                    }
                    String behaviorName = network.getNameBehaviorActivated();
                    socialHistory.add(System.currentTimeMillis(), behaviorName, DMmain.rapportLevel, DMmain.rapportScore);
                    DMmain.conversationalStrategies = network.getModuleNamesByHighestActivation();
                    DMmain.addContinousStates();

                    // send results to NLG and print them out on the screen
                    current = taskReasoner.processSystemIntents();
                    if( vhtConnector != null ) {
                        sendOutput();
                        flagSentSROutput = true;
                    }

                    String output = "System's Intent: " + current.name + "\n" +
                            "System's utterance: " + model.processSystemUtterance() + "\n" +
                            "Triggered by: " + model.triggeredBy + "\n" + "Id: " + current.id + "\n" +
                            "Conversational Strategy: " + DMmain.conversationalStrategies[0] + "\n";
                    if( DMmain.useSRPlot ) {
                        visualizer.printFSMOutput(output);
                        visualizer.printStates(network.getStateString(), current.phase, current.name);
                    }

                    //update state
                    network.execute( cycles );
                    blackboard.removeMessages(controller.getDelList());
                    blackboard.setStatesString(controller.getAddList(), controller.getName());

                    cycles = 0;
                    DMmain.syncStep = Constants.SYNC_SEQUENCE_START;
                    if( DMmain.flagStop && DMmain.queue.isEmpty() ){
                        DMmain.stop = true;
                    }
                }
                modelModifiedByTR.signal();
            } else if( DMmain.syncStep == Constants.SYNC_AFTER_SELECT_BEHAVIOR){
                cycles++;
            }
            if( DMmain.useSRPlot ) {
                visualizer.plot(network.getActivations(), network.getModules().size(), name, network.getTheta(),
                        network.getNameBehaviorActivated());
            }
            if( vhtConnector != null && !flagSentSROutput && stepCount % 20 == 0 ){
                sendOutput();
            }
            stepCount++;
            flagSentSROutput = false;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if( DMmain.flagResetTR || DMmain.flagReset ){
                modelModifiedByTR.signal();
                if( DMmain.flagResetTR ) {
                    DMmain.flagResetTR = false;
                }
            }
            lock.unlock();
        }
    }

    private void sendOutput() {
        SocialReasonerOutput output = new SocialReasonerOutput();
        output.setActivations(network.getOnlyActivations());
        output.setNames(network.getModuleNames());
        output.setThreshold(network.getTheta());
        vhtConnector.sendActivations(output);
    }


    public void initialize() {
        List<BehaviorPlus> modules = network.getModules();
        int size = modules.size();
        String[] names = new String[size + 1];
        for(int i = 0; i < names.length-1; i++) {
            names[i] = modules.get(i).getName();
        }
        names[ size ] = "Activation Threshold";
    }

    public void setModel(Model model) {
        this.model = model;
        current = model.current;
        taskReasoner = TaskReasoner.getInstance();
    }

    public static void reset() {
        instance.network.resetAll();
        instance = null;
    }
}
