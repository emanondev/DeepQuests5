package emanondev.deepquests.gui.inventory;

import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.button.EditMissionButton;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class EditMissionsMenu<T extends User<T>> extends ListGui<EditMissionButton<T>> {
    public EditMissionsMenu(Player player, Gui previusHolder, Quest<T> quest, T user) {
        super(GuiConfig.Generic.getMissionsMenuTitle(player), 6, player, previusHolder, 1);
        if (user == null || quest == null)
            throw new NullPointerException();
        for (Mission<T> mission : quest.getMissions())
            rawMissionButtons.add(new EditMissionButton<T>(this, mission, user));
        Collections.sort(rawMissionButtons);
        updateInventory();
    }

    private ArrayList<EditMissionButton<T>> rawMissionButtons = new ArrayList<>();

    public boolean updateInventory() {
        this.clearButtons();
        for (EditMissionButton<T> button : rawMissionButtons)
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