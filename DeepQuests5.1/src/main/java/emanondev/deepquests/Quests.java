package emanondev.deepquests;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import emanondev.core.CorePlugin;
import emanondev.core.Hooks;
import emanondev.core.ItemBuilder;
import emanondev.deepquests.command.*;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.GuiHandler;
import emanondev.deepquests.gui.button.GuiElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.nation.NationQuestManager;
import emanondev.deepquests.parties.PartyQuestManager;
import emanondev.deepquests.player.PlayerQuestManager;
import emanondev.deepquests.town.TownQuestManager;
import emanondev.deepquests.utils.BookTextEditor;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author emanon
 *
 *         main class
 */
public class Quests extends CorePlugin {
	private static Quests instance;
	private static final Map<String, QuestManager<?>> managers = new HashMap<>();

	private static PlayerInfoManager playerInfoManager;

	/**
	 * utility: register a command for this plugin
	 * 
	 * @param cmdManager
	 */
	@Deprecated
	private void registerCommand(ACommand cmdManager) {
		log("Registering command " + cmdManager.getName());
		PluginCommand cmd = getCommand(cmdManager.getName());
		cmd.setExecutor(cmdManager);
		cmd.setTabCompleter(cmdManager);
		if (cmdManager.getAliases() != null && !cmdManager.getAliases().isEmpty())
			cmd.setAliases(cmdManager.getAliases());
	}

	/**
	 * 
	 * @return an istance of the plugin
	 */
	public static Quests get() {
		return instance;
	}

	public void reload() {
		Translations.reload();

		GuiConfig.reload();

		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (inv != null && inv.getHolder() != null)
				if (inv.getHolder() instanceof Gui)
					p.closeInventory();
		}
		for (QuestManager<?> qm : managers.values()) {
			logTetraStar(ChatColor.BLUE, "Saving and reloading manager &e" + qm.getName());
			// TODO
			qm.save();
			qm.reload();
			logTetraStar(ChatColor.BLUE, "Manager &e" + qm.getName() + " &fsaved and reloaded");
		}
	}

	public PlayerInfo getPlayerInfo(OfflinePlayer p) {
		return playerInfoManager.getPlayerInfo(p);
	}

	public Gui getEditorGui(Player p, Gui previus) {
		return new Editor(p, previus);
	}

	private class Editor extends PagedMapGui {
		private Editor(Player p, Gui previus) {
			super("&9Editor", 6, p, previus);
			// if (getDefaultQuestManager() != null)
			// this.putButton(0, getDefaultQuestManager().getEditorButton(this));
			this.putButton(2, new QuestManagerSelector());
		}

		private class QuestManagerSelector extends GuiElementSelectorButton<QuestManager<?>> {

			public QuestManagerSelector() {
				super("&9Select a Quest Manager", new ItemBuilder(Material.STRUCTURE_BLOCK).setGuiProperty().build(),
						Editor.this, false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6Click to select a Quest Manager", "", "&9Each quest manager refers quests",
						"&9for a different kind of user or", "&9group of users");
			}

			@Override
			public Collection<QuestManager<?>> getValues() {
				return getManagers();
			}

			@Override
			public void onElementSelectRequest(QuestManager<?> element, Player p) {
				p.openInventory(element.getEditorGui(p, Editor.this).getInventory());
			}
		}
	}

	@Override
	protected boolean registerReloadCommand() {
		return false;
	}

	@Override
	public void disable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (inv != null && inv.getHolder() != null)
				if (inv.getHolder() instanceof Gui)
					p.closeInventory();
		}
		for (QuestManager<?> qm : new HashSet<QuestManager<?>>(managers.values()))
			unregisterQuestManager(qm);
		playerInfoManager.disable();
	}

	@Override
	public void enable() {
		try {

			// consoleLog("enabling Spawn Reason Tracker");
			// registerListener(new SpawnReasonTracker());
			// consoleLog("enabling Gui Handler");
			logTetraStar(ChatColor.BLUE, "Enabling Gui Handler");
			GuiConfig.reload();
			registerListener(new GuiHandler());
			registerListener(new BookTextEditor());

			logTetraStar(ChatColor.BLUE, "Player Preference Tracker");
			playerInfoManager = new PlayerInfoManager();

			// new LoggerManager();

			registerQuestManager(new PlayerQuestManager(PlayerQuestManager.NAME, this));

			if (Hooks.isTownyEnabled()) {
				registerQuestManager(new TownQuestManager(TownQuestManager.NAME, this));
				registerQuestManager(new NationQuestManager(NationQuestManager.NAME, this));
			}
			if (Hooks.isPartiesEnabled())
				registerQuestManager(new PartyQuestManager(PartyQuestManager.NAME, this));
			
			//
			logTetraStar(ChatColor.BLUE, "Registering commands");
			registerCommand(new DeepQuest());
			registerCommand(new DeepQuestText());
			registerCommand(new DeepQuestItem());
			registerCommand(new DeepQuestBack());
			//registerCommand(new DeepQuestImport());
			registerCommand(new CommandQuests());
			registerCommand(new DeepQuestsNewCommand());

			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					for (QuestManager<?> qm : managers.values()) {
						try {
							long now = System.currentTimeMillis();
							logTetraStar(ChatColor.YELLOW, "Loading manager &e" + qm.getName());
							qm.reload();
							logPentaStar(ChatColor.YELLOW,"Loaded manager &e" + qm.getName() + "&f, took &e"
									+ (System.currentTimeMillis() - now) + " &fms");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, 4L);
		} catch (Exception e) {
			e.printStackTrace();
			this.onDisable();
		}
	}

	@Override
	public void load() {
		instance = this;
	}

	public final boolean registerQuestManager(QuestManager<?> questManager) {
		if (questManager == null)
			throw new NullPointerException();
		if (managers.containsValue(questManager)) {
			logIssue(
					"Could not register QuestManager &e" + questManager.getName() + " &fbecause is already registered");
			return false;
		}
		if (managers.containsKey(questManager.getName())) {
			logIssue("Could not register QuestManager &e" + questManager.getName() + " &fbecause name is already used");
			return false;
		}
		logTetraStar(ChatColor.BLUE, "Registering QuestManager &e" + questManager.getName());
		managers.put(questManager.getName(), questManager);
		logDone("Registered QuestManager &e" + questManager.getName());
		return true;
	}

	public final boolean unregisterQuestManager(String name) {
		if (!managers.containsKey(name)) {
			new IllegalArgumentException("Could not find QuestManager " + name).printStackTrace();
			return false;
		}
		logTetraStar(ChatColor.BLUE, "Disabling QuestManager &e" + name);
		QuestManager<?> qm = managers.remove(name);
		qm.save();
		qm.disable();
		logDone("Disabled QuestManager &e" + name);
		return true;
	}

	public final boolean unregisterQuestManager(QuestManager<?> questManager) {
		return unregisterQuestManager(questManager.getName());
	}

	@SuppressWarnings("unchecked")
	public final <T extends User<T>> QuestManager<T> getQuestManager(String name) {
		return (QuestManager<T>) managers.get(name);
	}

	public final Collection<QuestManager<?>> getManagers() {
		return Collections.unmodifiableCollection(managers.values());
	}

	public final Collection<String> getManagersNames() {
		return Collections.unmodifiableCollection(managers.keySet());
	}

}
