package wef.articulab.model.history;


import wef.articulab.model.Constants;
import wef.articulab.model.blackboard.Blackboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 4/29/16.
 */
public class SocialHistory {
    private List<CSItem> historyList;
    private Blackboard blackboard;
    private static SocialHistory history;
    private final String THIS = "SocialHistory";

    private SocialHistory(){
        historyList = new ArrayList<>();
        blackboard = Blackboard.getInstance();
    }

    public static SocialHistory getInstance(){
        if( history == null ){
            history = new SocialHistory();
        }
        return history;
    }

    public void add(long timestamp, String cs, String rapporLevel, double rapportScore){
        historyList.add(new CSItem(timestamp, getCS(cs), rapporLevel, rapportScore));
    }

    public void addStates(){
        boolean isBC = false;
        if( !historyList.isEmpty() ){
            blackboard.removeMessagesContain("_HISTORY");
            String previous = historyList.get( historyList.size() - 1).getCs();
            if( previous.equals(Constants.SD_SYSTEM) ){
                blackboard.setStatesString( Constants.SD_HISTORY, THIS );
            }else if( previous.equals( Constants.QESD_SYSTEM)) {
                blackboard.setStatesString( Constants.QESD_HISTORY, THIS );
            }else if( previous.equals( Constants.BC_SYSTEM)) {
                blackboard.setStatesString( Constants.BC_HISTORY, THIS );
                isBC = true;
            }else if( previous.equals(Constants.RSE_SYSTEM)) {
                blackboard.setStatesString( Constants.RSE_HISTORY, THIS );
            }else if( previous.equals(Constants.ASN_SYSTEM)) {
                blackboard.setStatesString( Constants.ASN_HISTORY, THIS );
            }else if( previous.equals(Constants.VSN_SYSTEM)) {
                blackboard.setStatesString( Constants.VSN_HISTORY, THIS );
                if( historyList.size() >= 2
                        && historyList.get( historyList.size() - 1).getCs().equals( Constants.VSN_HISTORY) ){
                    blackboard.setStatesString( Constants.SEVERAL_ASN_HISTORY, THIS );
                }
            }else if( previous.equals(Constants.PR_SYSTEM)) {
                blackboard.setStatesString( Constants.PR_HISTORY, THIS );
            }
            if( !isBC ){
                blackboard.setStatesString( Constants.NOT_BC_HISTORY, THIS );
            }
        }
    }

    private String getCS(String cs){
        if( "ASN".equals(cs) ){
            return Constants.ASN_SYSTEM;
        }
        if( "VSN".equals(cs) ){
            return Constants.VSN_SYSTEM;
        }
        if( "SD".equals(cs) ){
            return Constants.SD_SYSTEM;
        }
        if( "QESD".equals(cs) ){
            return Constants.QESD_SYSTEM;
        }
        if( "PR".equals(cs) ){
            return Constants.PR_SYSTEM;
        }
        if( "RSE".equals(cs) ){
            return Constants.RSE_SYSTEM;
        }
        if( "BC".equals(cs) ){
            return Constants.BC_SYSTEM;
        }
        return cs;
    }

    public static void reset() {
        history.historyList.clear();
        history = null;
    }
}

// Conversational Strategy
class CSItem {
    private long timestamp;
    private String cs;
    private String rapporLevel;
    private double rapportScore;

    public CSItem(long timestamp, String cs, String rapporLevel, double rapportScore) {
        this.timestamp = timestamp;
        this.cs = cs;
        this.rapporLevel = rapporLevel;
        this.rapportScore = rapportScore;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public String getRapporLevel() {
        return rapporLevel;
    }

    public void setRapporLevel(String rapporLevel) {
        this.rapporLevel = rapporLevel;
    }

    public double getRapportScore() {
        return rapportScore;
    }

    public void setRapportScore(int rapportScore) {
        this.rapportScore = rapportScore;
    }

    @Override
    public String toString(){
        return cs + " : " + rapportScore;
    }
}