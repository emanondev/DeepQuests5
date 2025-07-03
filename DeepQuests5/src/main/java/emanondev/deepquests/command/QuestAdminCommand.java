package emanondev.deepquests.command;

import emanondev.core.command.CoreCommand;
import emanondev.core.message.DMessage;
import emanondev.deepquests.ItemEditUtils;
import emanondev.deepquests.Quests;
import emanondev.deepquests.Translations;
import emanondev.deepquests.generic.requiretypes.CurrentMissionStateRequireType;
import emanondev.deepquests.generic.requiretypes.CurrentQuestStateRequireType;
import emanondev.deepquests.gui.inventory.EditQuestsMenu;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.MissionsMenu;
import emanondev.deepquests.gui.inventory.QuestsMenu;
import emanondev.deepquests.interfaces.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class QuestAdminCommand extends CoreCommand {
    public QuestAdminCommand() {
        super("deepquests", Quests.get(), P.COMMAND_DEEPQUESTS, "basic command for admins", List.of("dq", "deepquest", "questadmin", "qa"));
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            help(sender, label);
            return;
        }
        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "editor" -> editor(sender, label, args);
            case "openquestsgui" -> openquestsgui(sender, label, args);
            case "openmissionsgui" -> openmissionsgui(sender, label, args);
            case "reload" -> reloadC(sender, label, args);
            case "manipulator" -> manipulator(sender, label, args);
            case "checkcycles" -> checkcycles(sender, label, args);
            default -> help(sender, label);
        }
    }

    private void craftHelp(CommandSender sender, Permission perm, boolean playerOnly, DMessage msg, String preCmd, List<String> hover, String arg) {
        craftHelp(sender, perm, playerOnly, msg, preCmd, hover, arg, null);
    }

    private void craftHelp(CommandSender sender, @Nullable Permission perm, boolean playerOnly, DMessage msg, String preCmd, List<String> hover, String arg, @Nullable String params) {
        if (perm != null && !sender.hasPermission(perm))
            msg.appendHover(hover, new DMessage(getPlugin(), sender).appendSuggest("/" + preCmd + " " + arg + (params != null ? " " + params : ""),
                    (perm != null && !sender.hasPermission(perm) && (!playerOnly || sender instanceof Player)) ?
                            ("<dark_aqua>" + arg + "</dark_aqua>" + (params != null ? " <aqua>" + params + "</aqua>" : "")) :
                            ("<red>" + arg + "</red>" + (params != null ? " <gold>" + params + "</gold>" : ""))
            )).newLine();
    }

    private void help(CommandSender sender, String label) {
        DMessage msg = new DMessage(getPlugin(), sender);
        msg.append("<blue><b><st>-----<gray>[--</gray></b></st>   Help   <b><st><gray>--]</gray>-----</b></st>").newLine()
                .append(" - /" + label + " [...]");
        craftHelp(sender, P.COMMAND_DEEPQUESTS_EDITOR, false, msg, label, List.of("<gold>Open Gui Editor for Quests",
                "Select a manager to skip manager selection</gold>"), "editor", "[manager]");
        craftHelp(sender, P.COMMAND_DEEPQUESTS_OPENGUI, true, msg, label, List.of("<gold>Open the menu as if you were player</gold>"),
                "openquestsgui", "<player>");
        craftHelp(sender, P.COMMAND_DEEPQUESTS_OPENGUI, true, msg, label, List.of("<gold>Open the menu as if you were player</gold>"),
                "openmissionsgui", "<player>");
        craftHelp(sender, P.COMMAND_DEEPQUESTS_RELOAD, false, msg, label, List.of("<gold>Reload the plugin configuration</gold>"),
                "reload");
        craftHelp(sender, P.COMMAND_DEEPQUESTS_MANIPULATOR, true, msg, label, List.of("<gold>Open the menu to manipulate player data</gold>"),
                "manipulator", "<player>");
        craftHelp(sender, P.COMMAND_DEEPQUESTS_CHECKCYCLES, false, msg, label, List.of("<gold>Checking Cicles</gold>"),
                "checkcycles");
        msg.newLine().append("<b><st>-----<gray>[--</gray></b></st>   Help   <b><st><gray>--]</gray>-----</b></st></blue>").send();
    }

    //qa editor [manager]
    //qa editor <manager> user <user> [...]
    private void editor(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_EDITOR)) {
            this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_EDITOR);
            return;
        }
        /*if (args.length != 2) {
            editorHelp(sender, label);
            return;
        }*/
        switch (args.length) {
            case 1 -> {
                if (!(sender instanceof Player player)) {
                    this.playerOnlyNotify(sender);
                    return;
                }
                player.openInventory(Quests.get().getEditorGui((Player) sender, null).getInventory());
            }
            case 2 -> {
                if (!(sender instanceof Player player)) {
                    this.playerOnlyNotify(sender);
                    return;
                }
                QuestManager<?> qm = Quests.get().getQuestManager(args[1]);
                if (qm == null) {
                    new DMessage(getPlugin(), player).append("<red>QuestManager <gold>" + args[1] + "</gold> not found").send();
                    return;
                }
                player.openInventory(qm.getEditorGui(player, Quests.get().getEditorGui(player, null)).getInventory());
            }
            default -> {
                QuestManager<?> qm = Quests.get().getQuestManager(args[1]);
                if (qm == null) {
                    new DMessage(getPlugin(), sender).append("<red>QuestManager <gold>" + args[1] + "</gold> not found").send();
                    return;
                }
                editorUser(sender, label, qm, args);
            }
        }
    }

    //qa editor <manager> user <user> Resetquest <id>
    //qa editor <manager> user <user> Completequest <id>
    //qa editor <manager> user <user> Failquest <id>
    //qa editor <manager> user <user> Erasequest <id>
    //qa editor <manager> user <user> Resetmission <id>
    //qa editor <manager> user <user> Startmission <id>
    //qa editor <manager> user <user> Completemission <id>
    //qa editor <manager> user <user> Failmission <id>
    //qa editor <manager> user <user> Erasemission <id>
    //qa editor <manager> user <user> Resettask <id>
    //qa editor <manager> user <user> Progresstask <id>
    //qa editor <manager> user <user> Erasetask <id>
    //qa editor <manager> user <user> TestQuest <id>
    //qa editor <manager> user <user> TestMission <id>
    private <T extends User<T>> void editorUser(CommandSender sender, String label, QuestManager<T> qm, String[] args) {
        if (args.length == 4 || args.length == 5) {
            helpEditorUser(sender, label, qm, args);
            return;
        }
        User<T> user = qm.getArgomentUser(args[3]);
        if (user == null) {
            new DMessage(Quests.get(), sender).append("<red>Can't find user with nick <gold>" + args[3]
                    + "</gold> on manager <gold>" + qm.getName()).send();
            return;
        }
        Integer id = readInt(args[5]);
        if (id == null) {
            new DMessage(Quests.get(), sender).append("<red>Cound't read <gold>" + args[5]
                    + "</gold> as a valid number").send();
            return;
        }

        switch (args[4].toLowerCase(Locale.ENGLISH)) {
            case "resetquest" -> {
                Quest<T> quest = qm.getQuest(id);
                if (quest == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any quest with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.resetQuest(quest);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Quest <yellow>" + id
                            + "</yellow> resetted for user <yellow>" + user.getUID()).send();
            }
            case "completequest" -> {
                Quest<T> quest = qm.getQuest(id);
                if (quest == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any quest with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.completeQuest(quest);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Quest <yellow>" + id
                            + "</yellow> completed for user <yellow>" + user.getUID()).send();
            }
            case "failquest" -> {
                Quest<T> quest = qm.getQuest(id);
                if (quest == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any quest with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Quest <yellow>" + id
                            + "</yellow> failed for user <yellow>" + user.getUID()).send();
            }
            case "erasequest" -> {
                Quest<T> quest = qm.getQuest(id);
                if (quest == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any quest with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.eraseQuestData(quest);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Quest <yellow>" + id
                            + "</yellow> erased for user <yellow>" + user.getUID()).send();
            }
            case "resetmission" -> {
                Mission<T> mission = qm.getMission(id);
                if (mission == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any mission with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.resetMission(mission);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Mission <yellow>" + id
                            + "</yellow> resetted for user <yellow>" + user.getUID()).send();
            }
            case "startmission" -> {
                Mission<T> mission = qm.getMission(id);
                if (mission == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any mission with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                Boolean forced = args.length>=7?readBoolean(args[6]):Boolean.TRUE;
                if (forced==null)
                    new DMessage(Quests.get(), sender).append("<red>Cound't read boolean value <gold>" + args[6]
                            + "</gold> on manager <gold>" + qm.getName()).send();
                if (user.startMission(mission,null,true)) {
                    user.saveOnDisk();
                    if (!(sender instanceof Player))
                        new DMessage(Quests.get(), sender).append("<green>Mission <yellow>" + id
                                + "</yellow> started for user <yellow>" + user.getUID()).send();
                }
                else
                    new DMessage(Quests.get(), sender).append("<green>Mission <yellow>" + id
                            + "</yellow> NOT started for user <yellow>" + user.getUID()).send();
            }
            case "completemission" -> {
                Mission<T> mission = qm.getMission(id);
                if (mission == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any mission with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.completeMission(mission);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Mission <yellow>" + id
                            + "</yellow> completed for user <yellow>" + user.getUID()).send();
            }
            case "failmission" -> {
                Mission<T> mission = qm.getMission(id);
                if (mission == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any mission with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.failMission(mission);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Mission <yellow>" + id
                            + "</yellow> failed for user <yellow>" + user.getUID()).send();
            }
            case "erasemission" -> {
                Mission<T> mission = qm.getMission(id);
                if (mission == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any mission with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.eraseMissionData(mission);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Mission <yellow>" + id
                            + "</yellow> erased for user <yellow>" + user.getUID()).send();
            }
            case "resettask" -> {
                Task<T> task = qm.getTask(id);
                if (task == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any task with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.resetTask(task);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Task <yellow>" + id
                            + "</yellow> resetted for user <yellow>" + user.getUID()).send();

            }
            case "progresstask" -> {
                Task<T> task = qm.getTask(id);
                if (task == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any task with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                Integer progress = args.length>=7?readInt(args[7]):Integer.valueOf(1);
                if (progress==null){
                    new DMessage(Quests.get(), sender).append("<red>Cound't read progress value <gold>" + progress).send();
                    return;
                }
                if (progress<=0){
                    new DMessage(Quests.get(), sender).append("<red>Invalid progress amount must be at least 1 <gold>" + progress).send();
                    return;
                }
                int progressed = user.progressTask(task, progress, null, true);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Task <yellow>" + id
                            + "</yellow> progressed by <yellow>"+progressed+"</yellow> for user <yellow>" + user.getUID()).send();
            }
            case "erasetask" -> {
                Task<T> task = qm.getTask(id);
                if (task == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any task with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                user.eraseTaskData(task);
                user.saveOnDisk();
                if (!(sender instanceof Player))
                    new DMessage(Quests.get(), sender).append("<green>Task <yellow>" + id
                            + "</yellow> erased for user <yellow>" + user.getUID()).send();
            }
            case "testquest" -> {
                Quest<T> quest = qm.getQuest(id);
                if (quest == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any quest with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                new DMessage(Quests.get(), sender).append("<green>Quest <yellow>" + id
                        + "</yellow> is <yellow>"+user.getDisplayState(quest).name()+"</yellow> for user <yellow>" + user.getUID()).send();
            }
            case "testmission" -> {
                Mission<T> mission = qm.getMission(id);
                if (mission == null) {
                    new DMessage(Quests.get(), sender).append("<red>Cound't find any mission with ID <gold>" + id
                            + "</gold> on manager <gold>" + qm.getName()).send();
                    return;
                }
                new DMessage(Quests.get(), sender).append("<green>Mission <yellow>" + id
                            + "</yellow> is <yellow>"+user.getDisplayState(mission).name()+"</yellow> for user <yellow>" + user.getUID()).send();
            }
            default -> helpEditorUser(sender, label, qm, args);
        }
    }

    // /qa editor <qm> user <user>
    private <T extends User<T>> void helpEditorUser(CommandSender sender, String label, QuestManager<T> qm, String[] args) {
        DMessage msg = new DMessage(getPlugin(), sender);
        String preCmd = label + " editor "+args[1]+" user "+(args.length>=4?args[3]:"<user>");
        msg.append("<blue><b><st>-----<gray>[--</gray></b></st>   Help   <b><st><gray>--]</gray>-----</b></st>").newLine()
                .append(" - /" + preCmd+" [...]");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Complete selected quest</gold>"),
                "completequest","<quest id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Fail selected quest</gold>"),
                "failquest","<quest id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Reset selected quest</gold>"),
                "resetquest","<quest id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Erase selected quest</gold>"),
                "erasequest","<quest id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Complete selected mission</gold>"),
                "completemission","<mission id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Fail selected mission</gold>"),
                "failmission","<mission id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Reset selected mission</gold>"),
                "resetmission","<mission id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Erase selected mission</gold>"),
                "erasemission","<mission id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Start selected mission</gold>"),
                "startmission","<mission id> [forced=true]");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Reset selected task</gold>"),
                "resettask","<task id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Erase selected task</gold>"),
                "erasetask","<task id>");
        craftHelp(sender, null, false, msg, preCmd, List.of("<gold>Progress selected task</gold>"),
                "progresstask","<task id> [progress=1]");
        msg.newLine().append("<b><st>-----<gray>[--</gray></b></st>   Help   <b><st><gray>--]</gray>-----</b></st></blue>").send();
    }

    private void checkcycles(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_CHECKCYCLES)) {
            this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_CHECKCYCLES);
            return;
        }
        for (QuestManager<?> man : Quests.get().getManagers()) {
            for (Quest<?> quest : man.getQuests()) {
                try {
                    unloop(quest, new ArrayList<>());
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(e.getMessage());
                }
            }
            for (Mission<?> mission : man.getMissions()) {
                try {
                    unloop(mission, new ArrayList<>());
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(e.getMessage());
                }
            }
        }
        new DMessage(getPlugin(), sender).append("<green>Check finished").send();
    }


    //legacyCode
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void unloop(Quest quest, ArrayList<QuestComponent> now) throws IllegalStateException {
        if (quest == null)
            return;
        if (now.contains(quest)) {
            StringBuilder text = new StringBuilder(ChatColor.DARK_RED + "<>");
            for (QuestComponent comp : now)
                text.append(ChatColor.GREEN).append(comp instanceof Quest ? (" quest ID ") : (" mission ID "))
                        .append(ChatColor.YELLOW).append(comp.getID()).append(ChatColor.GREEN).append(" name (")
                        .append(ChatColor.RESET).append(comp.getDisplayName()).append(ChatColor.GREEN).append(") -> ");
            throw new IllegalStateException(text.substring(0, text.length() - 3));
        }
        now.add(quest);
        for (Require req : ((Collection<Require>) quest.getRequires())) {
            if (req instanceof CurrentMissionStateRequireType.CurrentMissionStateRequire) {
                unloop(((CurrentMissionStateRequireType.CurrentMissionStateRequire) req).getTargetMissionData().getMission(),
                        new ArrayList<>(now));
            } else if (req instanceof CurrentQuestStateRequireType.CurrentQuestStateRequire) {
                unloop(((CurrentQuestStateRequireType.CurrentQuestStateRequire) req).getTargetQuestData().getQuest(),
                        new ArrayList<>(now));
            }
        }

    }

    //legacyCode
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void unloop(Mission mission, ArrayList<QuestComponent> now) throws IllegalStateException {
        if (mission == null)
            return;
        if (now.contains(mission)) {
            StringBuilder text = new StringBuilder(ChatColor.DARK_RED + "<>");
            for (QuestComponent comp : now)
                text.append(ChatColor.GREEN).append(comp instanceof Quest ? (" quest ID ") : (" mission ID "))
                        .append(ChatColor.YELLOW).append(comp.getID()).append(ChatColor.GREEN).append(" name (")
                        .append(ChatColor.RESET).append(comp.getDisplayName()).append(ChatColor.GREEN).append(") -> ");
            throw new IllegalStateException(text.substring(0, text.length() - 3));
        }
        now.add(mission);
        for (Require req : ((Collection<Require>) mission.getRequires())) {
            if (req instanceof CurrentMissionStateRequireType.CurrentMissionStateRequire) {
                unloop(((CurrentMissionStateRequireType.CurrentMissionStateRequire) req).getTargetMissionData().getMission(),
                        new ArrayList<>(now));
            } else if (req instanceof CurrentQuestStateRequireType.CurrentQuestStateRequire) {
                unloop(((CurrentQuestStateRequireType.CurrentQuestStateRequire) req).getTargetQuestData().getQuest(),
                        new ArrayList<>(now));
            }
        }
    }


    //qa manipulator <player>
    private void manipulator(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_MANIPULATOR)) {
            this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_MANIPULATOR);
            return;
        }
        if (!(sender instanceof Player player)) {
            this.playerOnlyNotify(sender);
            return;
        }
        if (args.length != 2) {
            monoLineHelp(sender, label, "manipulator <aqua><player></aqua>");
            return;
        }
        Player target = readPlayer(player, args[1]);
        if (target == null) {
            new DMessage(getPlugin(), player).append("<red>Player <gold>" + args[1] + "</gold> not found").send();
            return;
        }
        player.openInventory(new EditQuestsMenu(target, null, null).getInventory());
    }

    private void monoLineHelp(CommandSender sender, String label, String line) {
        new DMessage(getPlugin(), sender).append("<blue><b><st>-----<gray>[--</gray></b></st>   Help   <b><st><gray>--]</gray>-----</b></st>")
                .newLine().append("<dark_aqua>/" + label + " " + line + "</dark_aqua>")
                .newLine().append("<b><st>-----<gray>[--</gray></b></st>   Help   <b><st><gray>--]</gray>-----</b></st></blue>").send();
    }

    private void reloadC(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_RELOAD)) {
            this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_RELOAD);
            return;
        }
        Quests.get().reload();
        sender.sendMessage(Translations.Command.RELOAD);//TODO
    }

    private void openquestsgui(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_OPENGUI)) {
            this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_OPENGUI);
            return;
        }
        if (!(sender instanceof Player player)) {
            this.playerOnlyNotify(sender);
            return;
        }
        if (args.length != 2) {
            monoLineHelp(sender, label, "openquestsgui <aqua><player></aqua>");
            return;
        }
        Player target = readPlayer(player, args[1]);
        if (target == null) {
            new DMessage(getPlugin(), player).append("<red>Player <gold>" + args[1] + "</gold> not found").send();
            return;
        }
        player.openInventory(new QuestsMenu(target, null, null).getInventory());
    }

    private void openmissionsgui(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_OPENGUI)) {
            this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_OPENGUI);
            return;
        }
        if (!(sender instanceof Player player)) {
            this.playerOnlyNotify(sender);
            return;
        }
        if (args.length != 2) {
            monoLineHelp(sender, label, "openmissionsgui <aqua><player></aqua>");
            return;
        }
        Player target = readPlayer(player, args[1]);
        if (target == null) {
            new DMessage(getPlugin(), player).append("<red>Player <gold>" + args[1] + "</gold> not found").send();
            return;
        }
        player.openInventory(new MissionsMenu(target, null).getInventory());
    }

    // qa questbag %manager% %user% add/remove %id%
    @SuppressWarnings("rawtypes")
    private void questbag(CommandSender sender, String alias, String[] args) {
        if (args.length != 5 && args.length != 6) {
           new DMessage(this.getPlugin(), sender).appendLang("command.deepquests.questbag.wrong_params",
                    "&4[&c✘&4] &c/%alias% questbag <manager> <user> <add/remove> <id> [amount]", "%alias%",
                    alias).send();
            return;
        }
        QuestManager qm = Quests.get().getQuestManager(args[1]);
        if (qm == null) {
           new DMessage(this.getPlugin(), sender).appendLang(
                    "command.deepquests.questbag.invalid_manager", "&4[&c✘&4] &cInvalid manager name",
                    "%alias%", alias).send();
            return;
        }
        User u = qm.getArgomentUser(args[2]);
        if (u == null) {

           new DMessage(this.getPlugin(), sender).appendLang(
                    "command.deepquests.questbag.invalid_user", "&4[&c✘&4] &cInvalid user name", "%alias%", alias).send();
            return;
        }
        boolean add;
        switch (args[3].toLowerCase()) {
            case "add" -> add = true;
            case "remove" -> add = false;
            default -> {
               new DMessage(this.getPlugin(), sender).appendLang(
                        "command.deepquests.questbag.invalid_operation",
                        "&4[&c✘&4] &cInvalid operation name, user &eadd &cor &eremove", "%alias%", alias).send();
                return;
            }
        }
        int amount;
        try {
            amount = args.length >= 6 ? Integer.parseInt(args[5]) : 1;
            if (amount <= 0)
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {

            return;

        }
        String id = args[4].toLowerCase();
        if (add) {
            u.getQuestBag().addQuestItem(id, amount);
           new DMessage(this.getPlugin(), sender).appendLang("command.deepquests.questbag.success.add",
                    "&2[&a✔&2] &aAdded &e%amount% &aof &e%id% &ato &e%user% &aquestbag", "%amount%",
                    String.valueOf(amount), "%id%", id, "%user%", args[2]).send();
            return;
        }

        if (u.getQuestBag().getQuestItemAmount(id) == 0) {
           new DMessage(this.getPlugin(), sender).appendLang("command.deepquests.questbag.has_no_items",
                    "&2[&a✔&2] &e%user% &ahas no &e%id% &aon his questbag", "%id%", id, "%user%", args[2]).send();
            return;
        }
        int removed = u.getQuestBag().removeQuestItem(id, amount);
       new DMessage(this.getPlugin(), sender).appendLang("command.deepquests.questbag.success.remove",
                "&2[&a✔&2] &aRemoved &e%amount% &aof &e%id% &ato &e%user% &aquestbag", "%amount%",
                String.valueOf(removed), "%id%", id, "%user%", args[2]).send();
    }


    @SuppressWarnings("rawtypes")
    private void seequestbag(CommandSender sender, String alias, String[] args) {
        if (args.length != 3) {
            new DMessage(this.getPlugin(), sender).appendLang("command.deepquests.seequestbag.wrong_params",
                    "&4[&c✘&4] &c/%alias% seequestbag <manager> <user>", "%alias%",
                    alias).send();
            return;
        }
        QuestManager qm = Quests.get().getQuestManager(args[1]);
        if (qm == null) {
           new DMessage(this.getPlugin(), sender).appendLang(
                    "command.deepquests.seequestbag.invalid_manager", "&4[&c✘&4] &cInvalid manager name",
                    "%alias%", alias).send();
            return;
        }
        User u = qm.getArgomentUser(args[2]);
        if (u == null) {

           new DMessage(this.getPlugin(), sender).appendLang(
                    "command.deepquests.seequestbag.invalid_user", "&4[&c✘&4] &cInvalid user name", "%alias%", alias).send();
            return;
        }

        Gui gui = ItemEditUtils.craftGui(u, u.getQuestBag().getQuestItems(), (Player) sender,new DMessage(this.getPlugin(), sender).appendLang(
                "command.deepquests.seequestbag.gui-title", "&4QuestBag of %user% for %manager%",
                "%alias%", alias, "%user%", u.getUID(), "%manager%", qm.getName()).toString());
        //TODO open?

    }

    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @Nullable Location location) {
        if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_HELP))
            return Collections.emptyList();
        //TODO add permission checks
        return switch (args.length) {
            case 1 -> this.complete(args[0], List.of("editor", "openquestsgui","openmissionsgui", "reload", "manipulator", "checkcycles","questbag","seequestbag"));
            case 2 -> switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "editor","questbag","seequestbag" -> complete(args[1], Quests.get().getManagers(), QuestManager::getName);
                case "manipulator", "openmissionsgui", "openquestsgui" -> completePlayerNames(sender, args[1]);
                default -> Collections.emptyList();
            };
            case 3 -> switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "editor","questbag","seequestbag" -> {
                    QuestManager<?> qm = Quests.get().getQuestManager(args[1]);
                    if (qm == null)
                        yield Collections.emptyList();
                    yield this.complete(args[2], qm.getUsersArguments());
                }
                default -> Collections.emptyList();
            };
            case 4 -> switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "editor" -> {
                    QuestManager<?> qm = Quests.get().getQuestManager(args[1]);
                    if (qm == null)
                        yield Collections.emptyList();
                    yield this.complete(args[3], qm.getUsersArguments());
                }
                default -> Collections.emptyList();
            };
            case 5 -> switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "editor" -> this.complete(args[4], List.of("resetquest", "completequest", "failquest", "erasequest",
                        "resetmission", "startmission", "completemission", "failmission", "erasemission",
                        "resettask", "progresstask", "erasetask", "testquest", "testmission"));
                default -> Collections.emptyList();
            };
            case 6 -> switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "editor" -> switch (args[4].toLowerCase(Locale.ENGLISH)) {
                    case "resetquest", "completequest", "failquest", "erasequest", "testquest" -> {
                        QuestManager<?> qm = Quests.get().getQuestManager(args[1]);
                        if (qm == null)
                            yield Collections.emptyList();
                        yield this.complete(args[5], qm.getQuests(), t -> String.valueOf(t.getID()));
                    }
                    case "resetmission", "startmission", "completemission", "failmission", "erasemission", "testmission" -> {
                        QuestManager<?> qm = Quests.get().getQuestManager(args[1]);
                        if (qm == null)
                            yield Collections.emptyList();
                        yield this.complete(args[5], qm.getMissions(), t -> String.valueOf(t.getID()));
                    }
                    case "resettask", "progresstask", "erasetask" -> {
                        QuestManager<?> qm = Quests.get().getQuestManager(args[1]);
                        if (qm == null)
                            yield Collections.emptyList();
                        yield this.complete(args[5], qm.getTasks(), t -> String.valueOf(t.getID()));
                    }
                    default -> Collections.emptyList();
                };
                default -> Collections.emptyList();
            };
            case 7 -> switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "editor" -> switch (args[4].toLowerCase(Locale.ENGLISH)) {
                    case "startmission" -> this.complete(args[6], List.of("true", "false"));
                    case "progresstask" -> this.complete(args[6], List.of(1,2,3,4,5,6,7,8,9), Object::toString);
                    default -> Collections.emptyList();
                };
                default -> Collections.emptyList();

            };
            default -> Collections.emptyList();
        };
    }
}
