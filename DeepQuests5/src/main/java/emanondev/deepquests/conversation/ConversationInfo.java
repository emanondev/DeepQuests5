package emanondev.deepquests.conversation;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;

public class ConversationInfo<U extends User<U>> {

    private final U user;
    private final int conversationID;
    private final int nodeId;
    private final YMLSection section;

    public ConversationInfo(U user, int id, int conversationID, YMLSection section) {
        if (user == null || section == null)
            throw new NullPointerException();
        this.user = user;
        this.conversationID = conversationID;
        this.section = section;
        this.nodeId = id;
    }

    @Override
    public int hashCode() {
        final int prime = 997;
        int result = getManager().hashCode();
        result = prime * result + conversationID;
        result = prime * result + nodeId;
        return result;
    }

    public final U getUser() {
        return user;
    }

    public final int getConversationId() {
        return this.conversationID;
    }

    public final int getId() {
        return this.nodeId;
    }

    public final QuestManager<U> getManager() {
        return this.user.getManager();
    }

    public Integer getLastSeenNode() {
        return section.getInteger("last_node_id", null);
    }

    public void setLastSeenNode(Integer value) {
        section.set("last_node_id", value);
    }

    public void resetLastSeenNode() {
        section.set("last_node_id", null);
    }

}
