package emanondev.deepquests.gui.inventory;

import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.button.EditQuestButton;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EditQuestsMenu extends ListGui<EditQuestButton> {
    private ArrayList<EditQuestButton> rawQuestButtons = new ArrayList<>();

    public EditQuestsMenu(Player player, Gui previusHolder, Collection<QuestManager<?>> managers) {
        super(GuiConfig.Generic.getQuestsMenuTitle(player), 6, player, previusHolder, 1);
        if (managers == null)
            managers = Quests.get().getManagers();
        for (QuestManager<?> manager : managers) {
            if (manager == null)
                continue;
            User user = manager.getUserManager().getUser(getTargetPlayer());
            if (user == null)
                continue;
            for (Quest quest : manager.getQuests())
                rawQuestButtons.add(new EditQuestButton(this, quest, user));
        }
        Collections.sort(rawQuestButtons);
        updateInventory();
    }

    public boolean updateInventory() {
        this.clearButtons();
        for (EditQuestButton button : rawQuestButtons)
            this.addButton(button);
        return super.updateInventory();
    }

    protected int loadPreviusPageButtonPosition() {
        return 6;
    }

    protected int loadNextPageButtonPosition() {
        return 7;
    }

}