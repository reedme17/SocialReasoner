package wef.articulab.view.ui;

import wef.articulab.control.DMmain;
import wef.articulab.control.controllers.BehaviorNetworkController;
import wef.articulab.control.vht.VHTConnector;
import wef.articulab.view.emulators.InputController;

import java.util.ArrayList;

/**
 * Created by oscarr on 6/3/16.
 */
public class Visualizer {
    private CombinedBNXYPlot plot;
    private static Visualizer instance;
    private VHTConnector vhtConnector;
    private ArrayList<Double>[] behaviorValues;

    public static Visualizer getInstance(){
        if( instance == null ){
            instance = new Visualizer();
        }
        return instance;
    }

    public void initializePlot( DMmain DMmain, BehaviorNetworkController bnt1, BehaviorNetworkController bnt2 ){
        plot = CombinedBNXYPlot.startPlot( bnt1.getName(), DMmain.useFSM? null : bnt2.getName(), bnt1.getTitle(),
                DMmain.useFSM? null : bnt2.getTitle(), bnt1.getSeries(), DMmain.useFSM? null : bnt2.getSeries(), DMmain.useSRPlot );
        if(DMmain.useVHTConnnector && vhtConnector == null ) {
            vhtConnector = VHTConnector.getInstance();
            vhtConnector.setInputController(plot.getInputController());
            vhtConnector.setDMmain(DMmain);
        }
        behaviorValues = new ArrayList[bnt1.getSize()];
        for( int i = 0; i < behaviorValues.length; i++ ){
            behaviorValues[i] = new ArrayList<>();
        }
    }

    public void plot(double[] values, int size, String name, double theta, String behActivated){
        for(int i = 0; i < size; i++){
            behaviorValues[i].add(values[i]);
        }
        plot.setDataset(name, behaviorValues, theta, behActivated, values[size] );
    }

    public void printFSMOutput(String output) {
        plot.getOutputPanel().printFSMOutput( output);
    }

    public void printStates(String stateString, String phase, String intent) {
        plot.getOutputPanel().printStates( stateString, phase, intent);
    }

    public InputController getInputController() {
        return plot.getInputController();
    }
}
