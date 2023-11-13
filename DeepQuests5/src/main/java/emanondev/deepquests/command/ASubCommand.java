package emanondev.deepquests.command;

import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;


public abstract class ASubCommand {
    protected final HashMap<String, ASubCommand> subsByAlias = new HashMap<>();
    protected final LinkedHashSet<ASubCommand> subs = new LinkedHashSet<>();
    private final String permission;
    private final List<String> aliases = new ArrayList<>();
    private boolean playerOnly = false;
    private String params = null;
    private String description;
    private boolean showLockedSuggestions = true;

    /**
     * @param name       - nome di questo sottocomando (ex: /gemme bal - "bal" è il nome del sottocomando
     * @param permission - permesso (facoltativo) impedisce l'uso di questo sottocomando a chi non ha il permesso
     * @param subs       - lista eventuale di sottocomandi
     */
    public ASubCommand(String name, String permission, ASubCommand... subs) {
        this(List.of(name), permission, subs);
    }

    /**
     * @param aliases    - lista alias di questo sottocomando (ex: /f help, /f ?, /f aiuto : "help,?,aiuto" sono alias
     * @param permission - permesso (facoltativo) impedisce l'uso di questo sottocomando a chi non ha il permesso
     * @param subs       - lista eventuale di sottocomandi
     */
    public ASubCommand(List<String> aliases, String permission, ASubCommand... subs) {
        if (aliases != null)
            for (String alias : aliases) {
                if (alias == null || alias.isEmpty())
                    try {
                        throw new NullPointerException("attemping to register null or empty alias");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else if (alias.contains(" "))
                    try {
                        throw new NullPointerException("attemping to register alias with a space");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else if (!this.aliases.contains(alias.toLowerCase()))
                    this.aliases.add(alias.toLowerCase());
                else
                    try {
                        throw new IllegalArgumentException("attemping to register alias " + alias.toLowerCase() + " twice");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        this.permission = permission;
        if (subs != null && subs.length != 0) {
            for (ASubCommand sub : subs) {
                if (sub != null) {
                    this.subs.add(sub);
                    for (String alias : sub.getAliases())
                        if (!this.subsByAlias.containsKey(alias))
                            this.subsByAlias.put(alias, sub);
                        else
                            try {
                                throw new IllegalArgumentException("attemping to register subcommand alias " + alias.toLowerCase() + " twice");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                }
            }
        }
    }

    /**
     * da implementare per sottocomandi "foglia"
     *
     * @param params - lista attuale dei parametri (ex: /bal pay catullo 45, se siamo al sottocomando "pay" params è "catullo","45")
     * @param sender - esecutore del comando
     * @param label  - alias del comando usato, solitamente superfluo
     * @param args   - lista grezza completa dei parametri (ex: /bal pay catullo 45 , "pay","catullo",45")
     */
    public void onCmd(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (subs.isEmpty()) {
            CmdUtils.notImplemented(sender);
            return;
        }
        if (params == null || params.isEmpty() || !subsByAlias.containsKey(params.get(0).toLowerCase())) {
            onHelp(params, sender, label, args);
            return;
        }
        ASubCommand sub = subsByAlias.get(params.get(0).toLowerCase());

        if (sub.playersOnly() && !(sender instanceof Player)) {
            CmdUtils.playersOnly(sender);
            return;
        }
        if (sub.hasPermission(sender)) {
            params.remove(0);
            sub.onCmd(params, sender, label, args);
            return;
        }
        CmdUtils.lackPermission(sender, sub.getPermission());
    }

    /**
     * da implementare per sottocomandi "foglia" o che richiedono tab particolari
     *
     * @param params - lista attuale dei parametri (ex: /itemedit lore a, se siamo al sottocomando "lore" params è "a")
     * @param sender - esecutore del comando
     * @param label  - alias del comando usato, solitamente superfluo
     * @param args   - lista grezza completa dei parametri (ex: /itemedit lore a , "lore","a")
     *               <p>
     *               autocompleta con i sottocomandi <br>
     *               (ex: per itemedit i sottocomandi sono "set","remove","add" quindi<br>
     *               completerà con i sottocomandi che continuano "a", in questo caso "add") <br><br>
     * @return completitions
     */
    public ArrayList<String> onTab(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        if (params == null || params.isEmpty() || subs.isEmpty())
            return new ArrayList<>();
        if (params.size() == 1) {
            ArrayList<String> list = new ArrayList<>();
            for (ASubCommand sub : subs) {
                if (sub.playersOnly() && !(sender instanceof Player))
                    continue;
                if (sub.hasPermission(sender) && sub.getFirstAlias().startsWith(params.get(0).toLowerCase()))
                    list.add(sub.getFirstAlias());
            }
            return list;
        }

        if (!subsByAlias.containsKey(params.get(0).toLowerCase()))
            return new ArrayList<>();

        ASubCommand sub = subsByAlias.get(params.get(0).toLowerCase());
        if (!sub.hasPermission(sender))
            return new ArrayList<>();
        params.remove(0);
        return sub.onTab(params, sender, label, args);
    }

    /**
     * metodo da usare nel costruttore per impedire l'uso ai non player,
     * se settato true quando un non giocatore usa il comando stamperà che il comando è solo per player
     *
     * @param value
     * @return the object for chaining
     */
    public ASubCommand setPlayersOnly(boolean value) {
        playerOnly = value;
        return this;
    }

    public boolean playersOnly() {
        return playerOnly;
    }

    /**
     * Utility
     *
     * @param target
     * @return true if target has permission for this or if permission is null
     */
    public boolean hasPermission(CommandSender target) {
        if (permission == null || target.hasPermission(permission)) {
            return true;
        }
        return false;
    }

    /*
    public void onHelp(CommandSender s,String label,String[] args) {
        CmdUtils.fail(s);
    }*/
    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    public String getFirstAlias() {
        return aliases.get(0);
    }

    public String getPermission() {
        return permission;
    }

    public String getParams() {
        return params;
    }

    /**
     * for help utility
     *
     * @param params - (ex: /itemedit lore , params = "<add,remove,set> [...]"; for /itemedit lore add , params = "[text to add]")
     * @return this
     */
    protected ASubCommand setParams(String params) {
        this.params = params;
        return this;
    }

    public String getDescription() {
        return description;
    }

    /**
     * for help utility
     *
     * @param list - (ex: for /itemedit lore add , list = Arrays.asList("&6Aggiunge il testo nell'ultima linea","&cNota:se il testo non è presente aggiunge una linea vuota"))
     */
    protected void setDescription(List<String> list) {
        if (list == null || list.isEmpty()) {
            setDescription((String) null);
            return;
        }
        StringBuilder text = new StringBuilder();
        for (String line : list)
            text.append("\n").append(line);
        setDescription(text.toString().replaceFirst("\n", ""));
    }

    protected void setDescription(String s) {
        this.description = s;
    }

    private boolean showLockedSuggestions() {
        return showLockedSuggestions;
    }

    /**
     * mostrare i sottocomandi a cui non si ha accesso? <br>
     * ex: facendo /itemedit lore, mostrerà i 3 sottocomandi add,remove,set<br>
     * nel caso sia settato a true e sender non abbia il permesso itemedit.lore.remove<br>
     * mostrerà a sender solo add e set<br>
     * nel caso sia settato a false e sender non abbia il permesso itemedit.lore.remove<br>
     * mostrerà a sender add,set e remove ma marcherà remove in rosso <br>
     * per mostrare l'assenza del permesso per eseguirlo
     *
     * @param value
     */
    protected void setShowLockedSuggestions(boolean value) {
        showLockedSuggestions = value;
    }

    /**
     * schermata di aiuto, si consiglia di non reimplementare
     */
    public void onHelp(ArrayList<String> params, CommandSender sender, String label, String[] args) {
        String previusArgs = getPreviusParams(params, label, args);
        ComponentBuilder comp = new ComponentBuilder(
                "" + ChatColor.BLUE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-----"
                        + ChatColor.GRAY + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "[--"
                        + ChatColor.BLUE + "   Help   "
                        + ChatColor.GRAY + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--]"
                        + ChatColor.BLUE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-----");
        if (!subs.isEmpty()) {
            comp.append("\n" + ChatColor.BLUE + " - /" + previusArgs + " [...]");
            if (getDescription() != null)
                comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text(Utils.fixString(getDescription(), null, true))
                ));
            comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + previusArgs));

            boolean deepPermission = false;
            for (ASubCommand sub : subs) {
                if (sub.hasPermission(sender)) {
                    deepPermission = true;
                    if (sub.getParams() == null)
                        comp.append("\n" + ChatColor.DARK_AQUA + sub.getFirstAlias());
                    else
                        comp.append("\n" + ChatColor.DARK_AQUA + sub.getFirstAlias() + " " + ChatColor.AQUA + sub.getParams());
                    if (sub.getDescription() != null)
                        comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new Text(Utils.fixString(sub.getDescription(), null, true))
                        ));
                    comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + previusArgs + " " + sub.getFirstAlias()));
                } else if (showLockedSuggestions()) {
                    if (sub.getParams() == null)
                        comp.append("\n" + ChatColor.RED + sub.getFirstAlias());
                    else
                        comp.append("\n" + ChatColor.RED + sub.getFirstAlias() + " " + ChatColor.GOLD + sub.getParams());
                    if (getDescription() != null)
                        comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new Text(Utils.fixString(getDescription(), null, true))
                        ));
                }
            }
            if (!deepPermission && !showLockedSuggestions()) {
                for (ASubCommand sub : subs)
                    CmdUtils.lackPermission(sender, sub.getPermission());
                return;
            }
        } else {
            if (getParams() != null)
                comp.append("\n" + ChatColor.RED + " - /" + previusArgs + " " + ChatColor.GOLD + getParams());
            else
                comp.append("\n" + ChatColor.RED + " - /" + previusArgs);
            if (getDescription() != null)
                comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text(Utils.fixString(getDescription(), null, true))
                ));
            comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + previusArgs));

        }
        comp.append("\n").append("" + ChatColor.BLUE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-----"
                + ChatColor.GRAY + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "[--"
                + ChatColor.BLUE + "   Help   "
                + ChatColor.GRAY + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--]"
                + ChatColor.BLUE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-----");

        sender.spigot().sendMessage(comp.create());
    }

    private String getPreviusParams(ArrayList<String> params, String label, String[] args) {
        int max = args.length - params.size();
        StringBuilder text = new StringBuilder(label);
        for (int i = 0; i < max; i++) {
            text.append(" ").append(args[i]);
        }
        return text.toString();
    }
}
