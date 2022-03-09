package emanondev.deepquests.player.requiretypes;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayTime.PlayTimeManager.PlaytimeRange;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.SingleEnumData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.player.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CMIPlayTimeRequireType extends ARequireType<QuestPlayer> {
    private static final String ID = "day_of_week";

    public CMIPlayTimeRequireType(QuestManager<QuestPlayer> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.CLOCK;
    }

    @Override
    public List<String> getDescription() {
        return List.of("Require selected mission to have one of selected status");
    }

    @Override
    public Require<QuestPlayer> getInstance(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
        return new CMIPlayTimeRequire(id, manager, section);
    }

    public class CMIPlayTimeRequire extends ARequire<QuestPlayer> {

        private final AmountData<QuestPlayer, CMIPlayTimeRequire> amountData;
        private final SingleEnumData<QuestPlayer, CMIPlayTimeRequire, PlaytimeRange> playtimeRangeData;

        public CMIPlayTimeRequire(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, CMIPlayTimeRequireType.this, section);
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
            playtimeRangeData = new SingleEnumData<>(this,
                    getConfig().loadSection(Paths.DATA_PLAYTIMERANGE), PlaytimeRange.class);
        }

        @Override
        public boolean isAllowed(QuestPlayer user) {
            CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(user.getOfflinePlayer());
            return cmiUser.getCMIPlayTime().getPlayTime(
                    playtimeRangeData.getType() == null ? PlaytimeRange.total : playtimeRangeData.getType()) / 1000
                    / 60 > amountData.getAmount();
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add("&9Required Time (minutes):");
            info.add(" &9- &e" + amountData.getAmount());
            info.add("&9Time counted: &e" + (playtimeRangeData.getType() == null ? PlaytimeRange.total : playtimeRangeData.getType()).name());
            return info;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, playtimeRangeData.getEditorButton(this));
                this.putButton(28,
                        amountData.getAmountEditorButton("&9Select required Playtime minutes",
                                Arrays.asList("&6Required PlayTime Selector", "&9Minutes: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}