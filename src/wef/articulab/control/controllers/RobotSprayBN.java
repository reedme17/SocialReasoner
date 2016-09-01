package wef.articulab.control.controllers;

import wef.articulab.control.controllers.BehaviorNetworkController;
import wef.articulab.control.bn.BehaviorNetwork;

/**
 * Created by oscarr on 4/28/16.
 */
public class RobotSprayBN extends BehaviorNetworkController {
    private BehaviorNetwork network;
//    @Override
//    public BehaviorNetworkPlus createBN() {
//        network = new BehaviorNetwork();
//
//        Behavior beh1 = new Behavior( "PLACE-BOARD-IN-VISE", new String[]{"board-in-hand"}, new String[]{"hand-is-empty", "board-in-vise"}, new String[]{"board-in-hand"});
//        Behavior beh2 = new Behavior( "SPRAY-PAINT-SELF", new String[]{"operational", "sprayer-in-hand"}, new String[]{"self-painted"}, new String[]{"operational"});
//        Behavior beh3 = new Behavior( "SAND-BOARD-IN-HAND", new String[]{"operational", "board-in-hand", "sander-in-hand"}, new String[]{"board-sanded"}, null);
//        Behavior beh4 = new Behavior( "SAND-BOARD-IN-VISE", new String[]{"operational", "board-in-vise", "sander-in-hand"}, new String[]{"board-sanded"}, null);
//        Behavior beh5 = new Behavior( "PICK-UP-SANDER", new String[]{"sander-somewhere", "hand-is-empty"}, new String[]{"sander-in-hand"}, new String[]{"sander-somewhere", "hand-is-empty"});
//        Behavior beh6 = new Behavior( "PICK-UP-SPRAYER", new String[]{"sprayer-somewhere", "hand-is-empty"}, new String[]{"sprayer-in-hand"}, new String[]{"sprayer-somewhere", "hand-is-empty"});
//        Behavior beh7 = new Behavior( "PICK-UP-BOARD", new String[]{"board-somewhere", "hand-is-empty"}, new String[]{"board-in-hand"}, new String[]{"board-somewhere", "hand-is-empty"});
//        Behavior beh8 = new Behavior( "PUT-DOWN-SPRAYER", new String[]{"sprayer-in-hand"}, new String[]{"sprayer-somewhere", "hand-is-empty"}, new String[]{"sprayer-in-hand"});
//        Behavior beh9 = new Behavior( "PUT-DOWN-SANDER", new String[]{"sander-in-hand"}, new String[]{"sander-somewhere", "hand-is-empty"}, new String[]{"sander-in-hand"});
//        Behavior beh10 = new Behavior( "PUT-DOWN-BOARD", new String[]{"board-in-hand"}, new String[]{"board-somewhere", "hand-is-empty"}, new String[]{"board-in-hand"});
//
//        modules.add(beh1);
//        modules.add(beh2);
//        modules.add(beh3);
//        modules.add(beh4);
//        modules.add(beh5);
//        modules.add(beh6);
//        modules.add(beh7);
//        modules.add(beh8);
//        modules.add(beh9);
//        modules.add(beh10);
//
//        NUM_BEHAVIORS = modules.size();
//        NUM_VARIABLES = NUM_BEHAVIORS + 2; //+2 includes the threshold line and behavior activated
//
//        states.add("hand-is-empty");
//        states.add("hand-is-empty");
//        states.add("sander-somewhere");
//        states.add("sprayer-somewhere");
//        states.add("board-somewhere");
//        states.add("operational");
//
//        goals.add("board-sanded");
//        goals.add("self-painted");
//
//        network.setGoals(goals);
//        network.setPi(20);
//        network.setTheta(45);
//        network.setInitialTheta(45);
//        network.setPhi(20);
//        network.setGamma(70);
//        network.setDelta(50);
//
//        network.setModules(modules, NUM_VARIABLES);
//        network.setState(states);
//        network.setGoalsR(new Vector<>());
//        return network;
//    }

    @Override
    public String extractState(String state) {
        return null;
    }

    public String removeState( String state ){
        return null;
    }
}
