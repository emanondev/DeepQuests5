package emanondev.deepquests.player.requiretypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.McMMOSkillTypeData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.player.QuestPlayer;
import emanondev.core.ItemBuilder;

public class McmmoLevelRequireType extends ARequireType<QuestPlayer> {

	public McmmoLevelRequireType(QuestManager<QuestPlayer> manager) {
		super(ID, manager);
	}

	private static final String ID = "mcmmo_level";

	@Override
	public Material getGuiMaterial() {
		return Material.EXPERIENCE_BOTTLE;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require a certain lv on a mcmmo skill");
	}

	@Override
	public Require<QuestPlayer> getInstance(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
		return new McmmoLevelRequire(id, manager, section);
	}

	public class McmmoLevelRequire extends ARequire<QuestPlayer> {

		private McMMOSkillTypeData<QuestPlayer, McmmoLevelRequire> skillData = null;
		private AmountData<QuestPlayer, McmmoLevelRequire> amountData = null;

		public McmmoLevelRequire(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
			super(id, manager, McmmoLevelRequireType.this, section);
			skillData = new McMMOSkillTypeData<QuestPlayer, McmmoLevelRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_MCMMODATA));
			amountData = new AmountData<QuestPlayer, McmmoLevelRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
		}

		public McMMOSkillTypeData<QuestPlayer, McmmoLevelRequire> getMcMMOSkillTypeData() {
			return skillData;
		}

		public AmountData<QuestPlayer, McmmoLevelRequire> getAmountData() {
			return amountData;
		}

		public boolean isAllowed(QuestPlayer p) {
			if (skillData.getSkillType() == null)
				return false;
			try {
				McMMOPlayer mcmmoPlayer = UserManager.getPlayer(p.getPlayer());
				if (mcmmoPlayer.getSkillLevel(skillData.getSkillType()) >= amountData.getAmount())
					return true;
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(skillData.getInfo());
			info.add("&9Required Level: &e" + amountData.getAmount());
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARequireGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, skillData.getSkillTypeSelector(this));
				this.putButton(28,
						amountData.getAmountEditorButton("&9Select required Level",
								Arrays.asList("&6Required Level Selector", "&9Level: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}
}
