package emanondev.deepquests.town;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import emanondev.core.CorePlugin;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AQuestManager;
import emanondev.deepquests.town.rewardtypes.ConsoleCommandRewardType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TownQuestManager extends AQuestManager<QuestTown> {

    public final static String NAME = "towns";
    private final TownUserManager userManager;

    public TownQuestManager(@NotNull String name, @NotNull CorePlugin plugin) {
        super(name, plugin);
        userManager = new TownUserManager(this);

        // TODO types
        this.getRewardProvider().registerType(new ConsoleCommandRewardType(this));
    }

    @Override
    public @NotNull TownUserManager getUserManager() {
        return userManager;
    }

    @Override
    public @NotNull SortableButton getEditorButton(@NotNull Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    @Override
    public @NotNull Gui getEditorGui(Player target, Gui parent) {
        return new EditorGui(target, parent);
    }

    @Override
    public List<String> getInfo() {
        return Arrays.asList("&6&lTowns", "&6Quests related to towns");
    }

    @Override
    public @NotNull Material getGuiMaterial() {
        return Material.BRICK;
    }

    @Override
    public QuestTown getArgomentUser(String argument) {
        try {
            return getUserManager().getUser(TownyUniverse.getInstance().getTown(argument));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @NotNull Collection<String> getUsersArguments() {
        HashSet<String> names = new HashSet<>();
        for (Town town : TownyUniverse.getInstance().getTowns())
            names.add(town.getName());
        return names;
    }

    protected class EditorGui extends AQuestManager<QuestTown>.EditorGui {

        public EditorGui(Player player, Gui previousHolder) {
            super(player, previousHolder);
        }

    }

}
