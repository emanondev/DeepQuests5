package emanondev.deepquests.command;

import emanondev.deepquests.Perms;
import emanondev.deepquests.Quests;
import emanondev.deepquests.Translations;
import emanondev.deepquests.generic.requiretypes.CurrentMissionStateRequireType.CurrentMissionStateRequire;
import emanondev.deepquests.generic.requiretypes.CurrentQuestStateRequireType.CurrentQuestStateRequire;
import emanondev.deepquests.gui.inventory.EditQuestsMenu;
import emanondev.deepquests.gui.inventory.QuestsMenu;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DeepQuest extends ACommand {

    /**
     *
     */
    public DeepQuest() {
        super("deepquests", Arrays.asList("qa", "questadmin", "deepquest", "dq"), null, new SubEditor(),
                new SubOpengui(), new SubReload(), new SubManipulator(), new SubCheckCicles());
        this.setShowLockedSuggestions(false);
    }
}

class SubCheckCicles extends ASubCommand {
    SubCheckCicles() {
        super("checkcicles", Perms.ADMIN_RELOAD);
        this.setDescription(ChatColor.GOLD + "Checking Cicles");
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        for (QuestManager<?> man : Quests.get().getManagers()) {

            for (Quest quest : man.getQuests()) {
                try {
                    unloop(quest, new ArrayList<>());
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(e.getMessage());
                }
            }
            for (Mission mission : man.getMissions()) {
                try {
                    unloop(mission, new ArrayList<>());
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(e.getMessage());
                }
            }
        }
        sender.sendMessage("check finished");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void unloop(Quest quest, ArrayList<QuestComponent> now) throws IllegalStateException {
        if (quest == null)
            return;
        if (now.contains(quest)) {
            String text = ChatColor.DARK_RED + "<>";
            for (QuestComponent comp : now)
                text = text + ChatColor.GREEN + (comp instanceof Quest ? (" quest ID ") : (" mission ID "))
                        + ChatColor.YELLOW + comp.getID() + ChatColor.GREEN + " name (" + ChatColor.RESET
                        + comp.getDisplayName() + ChatColor.GREEN + ") -> ";
            throw new IllegalStateException(text.substring(0, text.length() - 3));
        }
        now.add(quest);
        for (Require req : ((Collection<Require>) quest.getRequires())) {
            if (req instanceof CurrentMissionStateRequire) {
                unloop(((CurrentMissionStateRequire) req).getTargetMissionData().getMission(),
                        new ArrayList<>(now));
            } else if (req instanceof CurrentQuestStateRequire) {
                unloop(((CurrentQuestStateRequire) req).getTargetQuestData().getQuest(),
                        new ArrayList<>(now));
            }
        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void unloop(Mission mission, ArrayList<QuestComponent> now) throws IllegalStateException {
        if (mission == null)
            return;
        if (now.contains(mission)) {
            String text = ChatColor.DARK_RED + "<>";
            for (QuestComponent comp : now)
                text = text + ChatColor.GREEN + (comp instanceof Quest ? (" quest ID ") : (" mission ID "))
                        + ChatColor.YELLOW + comp.getID() + ChatColor.GREEN + " name (" + ChatColor.RESET
                        + comp.getDisplayName() + ChatColor.GREEN + ") -> ";
            throw new IllegalStateException(text.substring(0, text.length() - 3));
        }
        now.add(mission);
        for (Require req : ((Collection<Require>) mission.getRequires())) {
            if (req instanceof CurrentMissionStateRequire) {
                unloop(((CurrentMissionStateRequire) req).getTargetMissionData().getMission(),
                        new ArrayList<>(now));
            } else if (req instanceof CurrentQuestStateRequire) {
                unloop(((CurrentQuestStateRequire) req).getTargetQuestData().getQuest(),
                        new ArrayList<>(now));
            }
        }
    }
}

/*
 * qa reload editor player <player> opengui
 *
 */

class SubReload extends ASubCommand {
    SubReload() {
        super("reload", Perms.ADMIN_RELOAD);
        this.setDescription(ChatColor.GOLD + "Reload the plugin configuration");
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        Quests.get().reload();
        sender.sendMessage(Translations.Command.RELOAD);
    }
}

class SubManipulator extends ASubCommand {
    SubManipulator() {
        super("manipulator", Perms.ADMIN_MANIPULATOR);
        this.setParams("<player>");
        this.setDescription(ChatColor.GOLD + "open the menu to manipulate player data");
        this.setPlayersOnly(true);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.size() != 1) {
            onHelp(params, sender, label, args);
            return;
        }
        Player target = Bukkit.getPlayer(params.get(0));
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + params.get(0) + " not found");
            return;
        }
        Player p = (Player) sender;
        p.openInventory(new EditQuestsMenu(target, null, null).getInventory());
    }

    public ArrayList<String> onTab(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (params.size() != 1)
            return list;
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getName().toLowerCase().startsWith(params.get(0)))
                list.add(p.getName());
        return list;
    }
}

class SubOpengui extends ASubCommand {
    SubOpengui() {
        super("opengui", Perms.ADMIN_OPENGUI);
        this.setParams("<player>");
        this.setDescription(ChatColor.GOLD + "open the menu as if you were player");
        this.setPlayersOnly(true);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.size() != 1) {
            onHelp(params, sender, label, args);
            return;
        }
        Player target = Bukkit.getPlayer(params.get(0));
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + params.get(0) + " not found");
            return;
        }
        Player p = (Player) sender;
        p.openInventory(new QuestsMenu(target, null, null).getInventory());
    }

    public ArrayList<String> onTab(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (params.size() != 1)
            return list;
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getName().toLowerCase().startsWith(params.get(0)))
                list.add(p.getName());
        return list;
    }
}

@SuppressWarnings("rawtypes")
class SubEditor extends ASubCommand {
    SubEditor() {
        super("editor", Perms.ADMIN_EDITOR, new SubEditorUser());
        this.setDescription(ChatColor.GOLD + "Open Gui Editor for Quests\n" + ChatColor.GOLD
                + "Select a manager to skip manager selection");
        this.setParams("[manager]");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty() && sender instanceof Player) {
            ((Player) sender).openInventory(Quests.get().getEditorGui((Player) sender, null).getInventory());
            return;
        }
        if (params.isEmpty() || Quests.get().getQuestManager(params.get(0).toLowerCase()) == null) {
            boolean color = true;
            ComponentBuilder comp = new ComponentBuilder(ChatColor.BLUE + "Choose a Quest Manager\n");
            for (QuestManager man : Quests.get().getManagers()) {
                comp.append((color ? ChatColor.YELLOW : ChatColor.GOLD) + man.getName());
                List<String> desc = Utils.fixList(man.getInfo(), null, true);
                StringBuilder txt = new StringBuilder("");
                for (String line : desc) {
                    txt.append(line).append("\n");
                }

                comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new Text(txt.substring(0, txt.length() - 2))))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                "/" + label + " " + args[0] + " " + man.getName()));
                color = !color;
            }
            sender.spigot().sendMessage(comp.create());
            return;
        }
        if (params.size() == 1 && sender instanceof Player
                && Quests.get().getQuestManager(params.get(0).toLowerCase()) != null) {
            ((Player) sender).openInventory(Quests.get().getQuestManager(params.get(0).toLowerCase())
                    .getEditorGui((Player) sender, Quests.get().getEditorGui((Player) sender, null)).getInventory());
            return;
        }
        params.remove(0);
        super.onCmd(params, sender, label, args);
    }

    public ArrayList<String> onTab(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.size() == 1) {
            ArrayList<String> list = new ArrayList<>();
            for (QuestManager man : Quests.get().getManagers())
                if (man.getName().startsWith(params.get(0).toLowerCase()))
                    list.add(man.getName());
            return list;
        }
        params.remove(0);
        return super.onTab(params, sender, label, args);
    }
}

// qa editor <name> user <user>
@SuppressWarnings("rawtypes")
class SubEditorUser extends ASubCommand {
    SubEditorUser() {
        super("user", Perms.ADMIN_EDITOR, new SubEditorUserResetquest(), new SubEditorUserCompletequest(),
                new SubEditorUserFailquest(), new SubEditorUserErasequest(), new SubEditorUserResetmission(),
                new SubEditorUserStartmission(), new SubEditorUserCompletemission(), new SubEditorUserFailmission(),
                new SubEditorUserErasemission(), new SubEditorUserResettask(), new SubEditorUserProgresstask(),
                new SubEditorUserErasetask());
        this.setDescription(ChatColor.GOLD + "Edit user\n" + ChatColor.GOLD + "Select a user to maipolate it");
        this.setPlayersOnly(false);
        this.setParams("<user>");
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        User user = manager.getArgomentUser(params.get(0));
        if (user == null) {
            sender.sendMessage(ChatColor.RED + "Can't find user with nick " + ChatColor.GOLD + params.get(0)
                    + ChatColor.RED + " on manager " + ChatColor.GOLD + args[1].toLowerCase());
            return;
        }
        params.remove(0);
        if (params.isEmpty() && sender instanceof Player) {
            sender.sendMessage("gui need to be implemented");
            // selected user open gui
            return;
        }
        super.onCmd(params, sender, label, args);
    }

    public ArrayList<String> onTab(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.size() == 1)
            return new ArrayList<>();
        params.remove(0);
        return super.onTab(params, sender, label, args);

    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserFailquest extends ASubCommand {
    SubEditorUserFailquest() {
        super("failquest", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Fail selected quest");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Quest quest = manager.getQuest(id);
        if (quest == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any quest with ID " + ChatColor.GOLD + args[5]);
            super.onHelp(params, sender, label, args);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.failQuest(quest);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Quest " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " failed for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserResetquest extends ASubCommand {
    SubEditorUserResetquest() {
        super("resetquest", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Reset selected quest");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Quest quest = manager.getQuest(id);
        if (quest == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any quest with ID " + ChatColor.GOLD + args[5]);
            super.onHelp(params, sender, label, args);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.resetQuest(quest);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Quest " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " resetted for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserErasequest extends ASubCommand {
    SubEditorUserErasequest() {
        super("erasequest", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Erase selected quest");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Quest quest = manager.getQuest(id);
        if (quest == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any quest with ID " + ChatColor.GOLD + args[5]);
            super.onHelp(params, sender, label, args);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.eraseQuestData(quest);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Quest " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " erased for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserCompletequest extends ASubCommand {
    SubEditorUserCompletequest() {
        super("completequest", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Complete selected quest");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Quest quest = manager.getQuest(id);
        if (quest == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any quest with ID " + ChatColor.GOLD + args[5]);
            super.onHelp(params, sender, label, args);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.completeQuest(quest);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Quest " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " completed for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserResetmission extends ASubCommand {
    SubEditorUserResetmission() {
        super("resetmission", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Reset selected mission");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Mission mission = manager.getMission(id);
        if (mission == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any mission with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.resetMission(mission);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Mission " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " resetted for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserCompletemission extends ASubCommand {
    SubEditorUserCompletemission() {
        super("completemission", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Complete selected mission");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Mission mission = manager.getMission(id);
        if (mission == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any mission with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.completeMission(mission);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Mission " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " completed for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserFailmission extends ASubCommand {
    SubEditorUserFailmission() {
        super("failmission", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Fail selected mission");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Mission mission = manager.getMission(id);
        if (mission == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any mission with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.failMission(mission);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Mission " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " failed for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserStartmission extends ASubCommand {
    SubEditorUserStartmission() {
        super("startmission", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Start selected mission");
        this.setParams("<id> [forced=true]");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Mission mission = manager.getMission(id);
        if (mission == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any mission with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        boolean forced = true;
        try {
            forced = Boolean.parseBoolean(args[6]);
        } catch (Exception ignored) {

        }
        boolean result = user.startMission(mission, null, forced);
        user.saveOnDisk();
        if (result) {
            String msg = ChatColor.GREEN + "Mission " + ChatColor.YELLOW + id + ChatColor.GREEN + " of "
                    + ChatColor.YELLOW + manager.getName() + ChatColor.GREEN + " started for user " + ChatColor.YELLOW
                    + user.getUID();
            Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
            if (!sender.equals(Bukkit.getConsoleSender()))
                sender.sendMessage(msg);
        }
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserErasemission extends ASubCommand {
    SubEditorUserErasemission() {
        super("erasemission", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Erase selected mission");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Mission mission = manager.getMission(id);
        if (mission == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any mission with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.eraseMissionData(mission);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Mission " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " erased for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserResettask extends ASubCommand {
    SubEditorUserResettask() {
        super("resettask", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Reset selected task");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Task task = manager.getTask(id);
        if (task == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any task with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.resetTask(task);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Task " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " resetted for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserProgresstask extends ASubCommand {
    SubEditorUserProgresstask() {
        super("progresstask", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Progress selected task");
        this.setParams("<id> [amount]");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Task task = manager.getTask(id);
        if (task == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any task with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        int amount;
        if (args.length >= 7)
            try {
                amount = Integer.parseInt(args[6]);
                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Amount (" + ChatColor.GOLD + amount + ChatColor.RED
                            + ") isn't a valid number, must be at least 1");
                    return;
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[6] + ChatColor.RED
                        + " as a valid number");
                return;
            }
        User user = manager.getArgomentUser(args[3]);
        user.progressTask(task, 1, null, true);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Task " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " progressed for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class SubEditorUserErasetask extends ASubCommand {
    SubEditorUserErasetask() {
        super("erasetask", Perms.ADMIN_EDITOR);
        this.setDescription(ChatColor.GOLD + "Erase selected task");
        this.setParams("<id>");
        this.setPlayersOnly(false);
    }

    @Override
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params.isEmpty()) {
            super.onHelp(params, sender, label, args);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED + "Cound't read " + ChatColor.GOLD + args[5] + ChatColor.RED + " as a valid number");
            return;
        }
        QuestManager manager = Quests.get().getQuestManager(args[1].toLowerCase());
        Task task = manager.getTask(id);
        if (task == null) {
            sender.sendMessage(ChatColor.RED + "Cound't find any task with ID " + ChatColor.GOLD + args[5]);
            return;
        }
        User user = manager.getArgomentUser(args[3]);
        user.eraseTaskData(task);
        user.saveOnDisk();
        String msg = ChatColor.GREEN + "Task " + ChatColor.YELLOW + id + ChatColor.GREEN + " of " + ChatColor.YELLOW
                + manager.getName() + ChatColor.GREEN + " erased for user " + ChatColor.YELLOW + user.getUID();
        Bukkit.getConsoleSender().sendMessage(Utils.clearColors(msg));
        if (!sender.equals(Bukkit.getConsoleSender()))
            sender.sendMessage(msg);
    }
}