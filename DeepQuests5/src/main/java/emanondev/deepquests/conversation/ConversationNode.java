package emanondev.deepquests.conversation;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.User;

import java.util.ArrayList;
import java.util.List;

public class ConversationNode<U extends User<U>> {

    public ConversationNode(int id, YMLSection section, Conversation parent) {
        this.id = id;
        this.section = section;
        this.parent = parent;
    }

    private final int id;
    private final Conversation parent;
    private final YMLSection section;
    private final List<Require<U>> requires = new ArrayList<>();
    private final List<Reward<U>> rewards = new ArrayList<>();
    private boolean isEndNode;

}
