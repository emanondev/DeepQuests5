package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.DisplayStateData;
import emanondev.deepquests.data.TargetQuestData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.User;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurrentQuestStateRequireType<T extends User<T>> extends ARequireType<T> {
    public static final String ID = "current_quest_state";

    public CurrentQuestStateRequireType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.BLUE_BANNER;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.CURLY_BORDER).setGuiProperty()
                .build();
    }

    @Override
    public List<String> getDescription() {
        return List.of("Require selected mission to have one of selected status");
    }

    @Override
    public @NotNull Require<T> getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new CurrentQuestStateRequire(id, manager, section);
    }

    public class CurrentQuestStateRequire extends ARequire<T> {
        private final TargetQuestData<T, CurrentQuestStateRequire> targetQuest;
        private final DisplayStateData<T, CurrentQuestStateRequire> stateData;

        public CurrentQuestStateRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, CurrentQuestStateRequireType.this, section);
            targetQuest = new TargetQuestData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_TARGETQUEST));
            stateData = new DisplayStateData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_DISPLAYSTATE));
        }

        @Override
        public boolean isAllowed(@NotNull T user) {
            Quest<T> quest = targetQuest.getQuest();
            if (quest == null)
                return false;
            return stateData.isValidState(user.getDisplayState(quest));
        }

        public TargetQuestData<T, CurrentQuestStateRequire> getTargetQuestData() {
            return targetQuest;
        }

        public DisplayStateData<T, CurrentQuestStateRequire> getDisplayStateData() {
            return stateData;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(targetQuest.getInfo());
            info.addAll(stateData.getInfo());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, getTargetQuestData().getQuestSelectorButton(this));
                this.putButton(28, getDisplayStateData().getDisplaySelectorButton(this));
            }
        }
    }
}
