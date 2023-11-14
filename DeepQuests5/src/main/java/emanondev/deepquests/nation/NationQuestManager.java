package emanondev.deepquests.nation;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import emanondev.core.CorePlugin;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AQuestManager;
import emanondev.deepquests.nation.rewardtypes.ConsoleCommandRewardType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class NationQuestManager extends AQuestManager<QuestNation> {

    public final static String NAME = "nations";
    private final NationUserManager userManager;

    public NationQuestManager(@NotNull String name, @NotNull CorePlugin plugin) {
        super(name, plugin);
        userManager = new NationUserManager(this);

        // TODO types
        this.getRewardProvider().registerType(new ConsoleCommandRewardType(this));
    }

    @Override
    public @NotNull NationUserManager getUserManager() {
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
        return Arrays.asList("&6&lNations", "&6Quests related to nations");
    }

    @Override
    public @NotNull Material getGuiMaterial() {
        return Material.BRICKS;
    }

    @Override
    public QuestNation getArgomentUser(String argument) {
        try {
            return getUserManager().getUser(TownyUniverse.getInstance().getNation(argument));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @NotNull Collection<String> getUsersArguments() {
        HashSet<String> names = new HashSet<>();
        for (Nation nation : TownyUniverse.getInstance().getNations())
            names.add(nation.getName());
        return names;
    }

    protected class EditorGui extends AQuestManager<QuestNation>.EditorGui {

        public EditorGui(Player player, Gui previousHolder) {
            super(player, previousHolder);
        }

    }

}
