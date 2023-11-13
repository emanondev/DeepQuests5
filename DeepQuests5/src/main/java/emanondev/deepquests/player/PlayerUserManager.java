package emanondev.deepquests.player;

import emanondev.core.UtilsMessages;
import emanondev.deepquests.Quests;
import emanondev.deepquests.implementations.AUserManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class PlayerUserManager extends AUserManager<QuestPlayer> implements Listener {

    private static final long AUTOSAVE_TIMER = 20 * 3600;
    private final Map<UUID, QuestPlayer> users = Collections.synchronizedMap(new HashMap<>());
    private BukkitTask autosave;
    private BukkitTask saving;
    private boolean enabled = false;

    public PlayerUserManager(PlayerQuestManager questManager) {
        super(questManager);
        setAutosave();
    }

    @Override
    public PlayerQuestManager getManager() {
        return (PlayerQuestManager) super.getManager();
    }

    @Override
    public Collection<QuestPlayer> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public QuestPlayer getUser(String uuid) {
        return users.get(UUID.fromString(uuid));
    }

    @Override
    public QuestPlayer getUser(Player p) {
        return users.get(p.getUniqueId());
    }

    @Override
    public void reload() {
        if (autosave != null)
            autosave.cancel();
        if (saving != null)
            saving.cancel();
        saveAll();
        users.clear();
        if (!enabled) {
            enabled = true;
            Bukkit.getPluginManager().registerEvents(this, Quests.get());
        }
        loadPlayersAsync(Bukkit.getOnlinePlayers());
        setAutosave();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
            return;
        loadPlayerAsync(event.getPlayer());
    }

    public QuestPlayer loadPlayer(OfflinePlayer p) {
        if (users.containsKey(p.getUniqueId()))
            return users.get(p.getUniqueId());
        long time = System.currentTimeMillis();
        QuestPlayer questUser = new QuestPlayer(this, p);
        users.put(p.getUniqueId(), questUser);
        Quests.get().logDone(ChatColor.BLUE,
                "Loaded player &e" + p.getName() + "&f took &c" + (System.currentTimeMillis() - time) + "&f ms");
        return questUser;

    }

    private void unloadPlayer(OfflinePlayer player) {
        users.remove(player.getUniqueId());
        Quests.get().logDone("Unloaded player &e" + player.getName());
    }

    private void savePlayer(QuestPlayer u) {
        Quests.get().logTetraStar(ChatColor.BLUE, "Saving player &e" + u.getOfflinePlayer().getName());
        long time = System.currentTimeMillis();
        u.saveOnDisk();
        Quests.get().logDone("Saved player &e" + u.getOfflinePlayer().getName() + "&f took &e"
                + (System.currentTimeMillis() - time) + "&f ms");
    }

    private void loadPlayersAsync(Collection<? extends Player> players) {
        if (players == null || players.isEmpty())
            return;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : players)
                    loadPlayer(player);
            }
        }.runTaskAsynchronously(getManager().getPlugin());
    }

    private void loadPlayerAsync(Player player) {
        if (player == null || users.containsKey(player.getUniqueId()))
            return;
        new BukkitRunnable() {
            @Override
            public void run() {
                loadPlayer(player);
            }
        }.runTaskAsynchronously(getManager().getPlugin());
    }

    private void setAutosave() {
        autosave = Bukkit.getScheduler().runTaskTimerAsynchronously(Quests.get(), () -> {
            final Map<UUID, QuestPlayer> clone = new HashMap<>(users);
            saving = Bukkit.getScheduler().runTaskTimerAsynchronously(Quests.get(), () -> {
                UUID uuid = null;
                if (clone.size() == 0) {
                    saving.cancel();
                    saving = null;
                    return;
                }
                for (UUID uid : clone.keySet()) {
                    uuid = uid;
                    break;
                }
                if (uuid != null) {
                    // String name = Bukkit.getOfflinePlayer(uuid).getName();
                    QuestPlayer user = clone.get(uuid);
                    savePlayer(user);
                    clone.remove(uuid);
                    if (Bukkit.getPlayer(uuid) == null)
                        unloadPlayer(user.getOfflinePlayer());
                }
            }, 40L, 40L);

        }, AUTOSAVE_TIMER, AUTOSAVE_TIMER);

    }

    public void swapPlayers(OfflinePlayer p1, OfflinePlayer p2, CommandSender sender) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    QuestPlayer u1 = users.get(p1.getUniqueId());
                    QuestPlayer u2 = users.get(p2.getUniqueId());
                    if (u1 != null) {
                        savePlayer(u1);
                        unloadPlayer(p1);
                    }

                    if (u2 != null) {
                        savePlayer(u2);
                        unloadPlayer(p2);
                    }

                    File file1 = getUserFile(p1.getUniqueId().toString());
                    File file2 = getUserFile(p2.getUniqueId().toString());

                    String path1 = file1.getAbsolutePath();
                    String path2 = file2.getAbsolutePath();

                    File tmp = getUserFile("temp_swap_" + ((int) (Math.random() * 10000)));

                    if (!file1.exists() || !file2.exists())
                        throw new IllegalStateException();
                    if (!file1.renameTo(tmp))
                        throw new IllegalStateException();
                    if (!file2.renameTo(new File(path1)))
                        throw new IllegalStateException();
                    if (!tmp.renameTo(new File(path2)))
                        throw new IllegalStateException();

                    if (p1.isOnline())
                        loadPlayer(p1);
                    if (p2.isOnline())
                        loadPlayer(p2);
                    Quests.get().logDone("Swapped quest players &e" + p1.getName() + "&f (&e" + p1.getUniqueId() + "&f) and &e" + p2.getName() + "&f (&e" + p2.getUniqueId() + "&f)");
                    if (sender != null)
                        UtilsMessages.sendMessage(sender,
                                getManager().getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.database.swapplayers.success",
                                        "&b[&a✓&b] &aSwapped players &e%player1% &aand &e%player2%", "%player1%", p1.getName(), "%player2%", p2.getName()));

                } catch (Exception e) {
                    e.printStackTrace();
                    Quests.get().logIssue("Unable to swap quest players &e" + p1.getName() + "&f (&e" + p1.getUniqueId() + "&f) and &e" + p2.getName() + "&f (&e" + p2.getUniqueId() + "&f)");
                    UtilsMessages.sendMessage(sender,
                            getManager().getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.database.swapplayers.failed",
                                    "&4[&c✗&4] &cUnable to swap players &6%player1% &cand &6%player2%", "%player1%", p1.getName(), "%player2%", p2.getName()));
                }
            }
        }.runTaskAsynchronously(getManager().getPlugin());

    }

}
