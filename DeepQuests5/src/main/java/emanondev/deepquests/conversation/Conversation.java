package emanondev.deepquests.conversation;

import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.GuiElement;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class Conversation<U extends User<U>> implements GuiElement {

    private final QuestManager<U> manager;
    private final int id;
    private final HashMap<Integer, ConversationNode<U>> nodes = new HashMap<>();
    private final YMLSection section;

    @Override
    public int hashCode() {
        final int prime = 997;
        int result = manager.hashCode();
        result = prime * result + id;
        return result;
    }

    public Conversation(int id, QuestManager<U> manager, YMLSection section) {
        if (manager == null || section == null)
            throw new NullPointerException();
        this.manager = manager;
        this.id = id;
        this.section = section;
        //TODO loadNodes
    }

    public int getId() {
        return id;
    }

    public QuestManager<U> getManager() {
        return manager;
    }

    @Override
    public Material getGuiMaterial() {
        return Material.BOOKSHELF;
    }

    @Override
    public Gui getEditorGui(Player target, Gui parent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPriority() {
        return section.getInteger("priority", 0);
    }

    public void setPriority(int value) {
        if (value == 0)
            section.set("priority", null);
        else
            section.set("priority", value);
    }

    @Override
    public List<String> getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortableButton getEditorButton(Gui parent) {
        // TODO Auto-generated method stub
        return null;
    }


}
