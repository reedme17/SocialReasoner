package wef.articulab.model.recommendation.participant;

/**
 * Created by oscarr on 4/20/16.
 */
public class ForumNetwork {
    private String forumCommunity;
    private String network;

    public String getForumCommunity() {
        return forumCommunity;
    }

    public void setForumCommunity(Object forumCommunity) {
        this.forumCommunity = (String)forumCommunity;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(Object network) {
        this.network = (String)network;
    }
}
