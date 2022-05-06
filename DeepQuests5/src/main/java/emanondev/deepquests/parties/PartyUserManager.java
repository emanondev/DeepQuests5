package emanondev.deepquests.parties;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPostCreateEvent;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPreDeleteEvent;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPreRenameEvent;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import emanondev.deepquests.Quests;
import emanondev.deepquests.implementations.AUserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PartyUserManager extends AUserManager<QuestParty> implements Listener {

    private final Map<Party, QuestParty> users = new HashMap<>();

    public PartyUserManager(PartyQuestManager questManager) {
        super(questManager);
        Bukkit.getPluginManager().registerEvents(this, Quests.get());
    }

    @Override
    public PartyQuestManager getManager() {
        return (PartyQuestManager) super.getManager();
    }

    @Override
    public Collection<QuestParty> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public QuestParty getUser(String uuid) {
        Party party = getParty(uuid);
        return party == null ? null : users.get(party);
    }

    @Override
    public QuestParty getUser(Player p) {
        try {
            Party party = getParty(p);
            return party == null ? null : users.get(party);
        } catch (Exception e) {
            return null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(BukkitPartiesPartyPostCreateEvent event) {
        QuestParty questUser = new QuestParty(this, event.getParty());
        users.put(event.getParty(), questUser);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(BukkitPartiesPartyPreDeleteEvent event) {
        QuestParty user = users.remove(event.getParty());
        if (user != null)
            user.saveOnDisk();
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(BukkitPartiesPartyPreRenameEvent event) {
        event.setCancelled(true);
        Bukkit.getPlayer(event.getPartyPlayer().getPlayerUUID())
                .sendMessage("Evento incompatibile con le quest");
        //TODO
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            Party party = getParty(event.getPlayer());
            if (party == null || users.containsKey(party))
                return;
            users.put(party, new QuestParty(this, party));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Party party = getParty(event.getPlayer());
        if (party == null || !users.containsKey(party))
            return;
        if (Parties.getApi().getOnlinePlayers(party).isEmpty()) {
            QuestParty user = users.remove(party);
            if (user != null) {
                user.saveOnDisk();
            }
        } else {
            QuestParty user = users.get(party);
            if (user != null) {
                user.saveOnDisk();
            }
        }

    }

    @Override
    public void reload() {
        saveAll();
        users.clear();
        for (Party party : Parties.getApi().getOnlineParties()) {
            if (!users.containsKey(party)) {
                QuestParty questUser = new QuestParty(this, party);
                users.put(party, questUser);
            }
        }

    }

    private Party getParty(Player p) {
        PartyPlayer pPlayer = Parties.getApi().getPartyPlayer(p.getUniqueId());
        if (pPlayer == null)
            return null;
        return getParty(pPlayer.getPartyName());
    }

    private Party getParty(String partyName) {
        if (partyName == null)
            return null;
        return Parties.getApi().getParty(partyName);
    }

    public QuestParty getUser(Party p) {
        return users.get(p);
    }

}