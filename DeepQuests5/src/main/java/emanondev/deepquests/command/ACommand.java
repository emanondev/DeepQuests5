package emanondev.deepquests.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ACommand extends ASubCommand implements TabExecutor {

    /**
     * @param commandName - nome del comando (ex: /heal <- nome = "heal")
     * @param aliases     - lista degli alias del comando (facoltativa può essere null)
     * @param permission  - se il comando richiede un permesso specificarlo qui altrimenti null
     * @param subs        - lista dei sottocomandi (ASubCommand)
     */
    public ACommand(String commandName, List<String> aliases, String permission, ASubCommand... subs) {
        super(aliases, permission, subs);
        if (commandName == null || commandName.isEmpty())
            throw new NullPointerException();
        if (commandName.contains(" "))
            throw new IllegalArgumentException("Command Name '" + commandName + "' contains spaces");
        this.name = commandName.toLowerCase();
    }

    private final String name;

    /**
     * @return restituisce il nome del comando
     */
    public String getName() {
        return name;
    }

    /**
     * già implementato per compilare la lista dei sottocomandi,
     * se si intende modificarlo sovrascrivere #onTab
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> params = new ArrayList<>();
        if (!hasPermission(sender))
            return params;
        if (playersOnly() && !(sender instanceof Player))
            return params;
        Collections.addAll(params, args);

        return onTab(params, sender, label, args);
    }

    /**
     * già implementato per compilare la lista dei sottocomandi,
     * se si intende modificarlo sovrascrivere #onCmd
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!hasPermission(sender)) {
            CmdUtils.lackPermission(sender, getPermission());
            return true;
        }
        if (playersOnly() && !(sender instanceof Player)) {
            CmdUtils.playersOnly(sender);
            return true;
        }
        ArrayList<String> params = new ArrayList<>();
        Collections.addAll(params, args);

        onCmd(params, sender, label, args);
        return true;
    }


}
