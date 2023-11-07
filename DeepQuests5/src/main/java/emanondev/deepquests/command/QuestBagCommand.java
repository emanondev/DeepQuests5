package emanondev.deepquests.command;

import emanondev.core.PermissionBuilder;
import emanondev.core.UtilsMessages;
import emanondev.core.command.CoreCommand;
import emanondev.core.message.DMessage;
import emanondev.deepquests.ItemEditUtils;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuestBagCommand<T extends User<T>> extends CoreCommand {

    private final QuestManager<T> manager;

    private final Permission SEE_OTHERS;

    public QuestBagCommand(QuestManager<T> manager) {
        super(manager.getName() + "questbag", Quests.get(),
                new PermissionBuilder("deepquests.command." + manager.getName() + "questbag")
                        .setDescription("Allows to open and inspect your own questbag for manager " + manager.getName())
                        .buildAndRegister(Quests.get()),
                "allow to use /" + manager.getName() + "questbag command");
        SEE_OTHERS = new PermissionBuilder("deepquests.command." + manager.getName() + "questbag.others")
                .addChild(this.getCommandPermission(), true)
                .setDescription("Allows to open and inspect any questbag for manager " + manager.getName()).buildAndRegister(Quests.get());
        this.manager = manager;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player s)) {
            this.playerOnlyNotify(sender);
            return;
        }
        switch (args.length) {
            case 0 -> {
                T u = manager.getUserManager().getUser(s);
                if (u == null) {
                    // no user
                    new DMessage(this.getPlugin(),sender).appendLang(
                            "command." + this.getID() + ".no-user").send();
                    return;
                }
                if (u.getQuestBag() == null) {
                    // nobag
                    new DMessage(this.getPlugin(),sender).appendLang(
                            "command." + this.getID() + ".no-bag").send();
                    return;
                }
                Map<String, Integer> map = u.getQuestBag().getQuestItems();
                if (map.isEmpty()) {
                    // no items
                    new DMessage(this.getPlugin(),sender).appendLang(
                            "command." + this.getID() + ".no-items").send();
                    return;
                }
                Gui gui = ItemEditUtils.craftGui(u, map, s, this.getPlugin().getLanguageConfig(s)
                        .loadMessage("command." + this.getID() + ".gui-title", "&9Quest Bag"));
                s.openInventory(gui.getInventory());
            }
            case 1 -> {
                if (!s.hasPermission(SEE_OTHERS)) {
                    this.permissionLackNotify(s, SEE_OTHERS);
                    return;
                }
                Player t = this.readPlayer(sender, args[0]);
                if (t == null) {
                    // unexisting player
                    UtilsMessages.sendMessage(sender,
                            this.getPlugin().getLanguageConfig(sender).loadMessage(
                                    "command." + this.getID() + ".wrong-player-name",
                                    "&cNo user with name &6%name% &cwas found", "%name%", args[0]));
                    return;
                }
                T u = manager.getUserManager().getUser(t);
                if (u == null) {
                    // no user
                    new DMessage(this.getPlugin(),sender).appendLang(
                            "command." + this.getID() + ".no-user").send();
                    return;
                }
                if (u.getQuestBag() == null) {
                    // nobag
                    new DMessage(this.getPlugin(),sender).appendLang(
                            "command." + this.getID() + ".no-bag").send();
                    return;
                }
                Map<String, Integer> map = u.getQuestBag().getQuestItems();
                if (map.isEmpty()) {
                    // noitems
                    new DMessage(this.getPlugin(),sender).appendLang(
                            "command." + this.getID() + ".no-items").send();
                    return;
                }
                Gui gui = ItemEditUtils.craftGui(u, map, s, this.getPlugin().getLanguageConfig(s)
                        .loadMessage("command." + this.getID() + ".gui-title", "&9Quest Bag"));
                s.openInventory(gui.getInventory());
            }
            default -> new DMessage(this.getPlugin(),sender).appendLang(
                    "command." + this.getID() + ".help", "%alias%", alias).send();
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args, Location loc) {
        if (args.length != 1 || !sender.hasPermission(SEE_OTHERS))
            return Collections.emptyList();
        return this.completePlayerNames(sender, args[0]);
    }

}
