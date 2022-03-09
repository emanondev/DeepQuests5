package emanondev.deepquests.conversation;

import emanondev.deepquests.interfaces.User;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationManager {

    private Map<Integer, List<Conversation<? extends User<?>>>> map = new HashMap<>();

    public void registerConversation(Conversation<? extends User<?>> conv) {

    }

    public void unregisterConversation(Conversation<? extends User<?>> conv) {

    }

    public void updateConversation(Conversation<? extends User<?>> conv) {

    }

    @EventHandler
    private void event(NPCRightClickEvent event) {
        if (map.containsKey(event.getNPC().getId())) {

        }
    }

}
