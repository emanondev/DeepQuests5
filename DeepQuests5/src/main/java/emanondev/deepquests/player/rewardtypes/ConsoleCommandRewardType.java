package emanondev.deepquests.player.rewardtypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.CommandData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.player.QuestPlayer;
import emanondev.deepquests.utils.DataUtils;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ConsoleCommandRewardType extends ARewardType<QuestPlayer> {
    private final static String ID = "console_command";

    public ConsoleCommandRewardType(QuestManager<QuestPlayer> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.COMMAND_BLOCK;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("&7Perform a console command,", "&7%player% is replaced with player name",
                "&7Command must &cNOT &7contain the starting '/'");
    }

    @Override
    public @NotNull ConsoleCommandReward getInstance(int id, @NotNull QuestManager<QuestPlayer> manager, @NotNull YMLSection section) {
        return new ConsoleCommandReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<QuestPlayer> reward) {
        if (!(reward instanceof ConsoleCommandRewardType.ConsoleCommandReward))
            return null;
        ConsoleCommandReward r = (ConsoleCommandReward) reward;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:console_execute} &e{command}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{command}", DataUtils.getCommandHolder(r.getCommandData()));
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return true;
    }

    public class ConsoleCommandReward extends AReward<QuestPlayer> {
        private final CommandData<QuestPlayer, ConsoleCommandReward> cmdData;

        public ConsoleCommandReward(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, ConsoleCommandRewardType.this, section);
            cmdData = new CommandData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_COMMAND));
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(cmdData.getInfo());
            return info;
        }

        public CommandData<QuestPlayer, ConsoleCommandReward> getCommandData() {
            return cmdData;
        }

        @Override
        public void apply(@NotNull QuestPlayer qPlayer, int amount) {
            if (cmdData.getCommand() == null)
                return;
            String command = Utils
                    .fixString(cmdData.getCommand().replace("%player%", qPlayer.getOfflinePlayer().getName())
                            .replace("%player_name%", qPlayer.getOfflinePlayer().getName()), qPlayer.getPlayer(), false);
            for (int i = 0; i < amount; i++)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, cmdData.getCommandEditorButton(this));
            }
        }
    }
}