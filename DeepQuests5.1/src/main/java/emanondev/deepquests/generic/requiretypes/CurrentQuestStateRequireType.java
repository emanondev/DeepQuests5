package emanondev.deepquests.generic.requiretypes;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.core.YMLSection;
import emanondev.deepquests.data.DisplayStateData;
import emanondev.deepquests.data.TargetQuestData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;

public class CurrentQuestStateRequireType<T extends User<T>> extends ARequireType<T> {
	public static final String ID = "current_quest_state";

	public CurrentQuestStateRequireType(QuestManager<T> manager) {
		super(ID, manager);
	}

	@Override
	public Material getGuiMaterial() {
		return Material.BLUE_BANNER;
	}

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.CURLY_BORDER).setGuiProperty()
				.build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("Require selected mission to have one of selected status");
	}

	@Override
	public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new CurrentQuestStateRequire(id, manager, section);
	}

	public class CurrentQuestStateRequire extends ARequire<T> {
		private TargetQuestData<T, CurrentQuestStateRequire> targetQuest = null;
		private DisplayStateData<T, CurrentQuestStateRequire> stateData = null;

		public CurrentQuestStateRequire(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, CurrentQuestStateRequireType.this, section);
			targetQuest = new TargetQuestData<T, CurrentQuestStateRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_TARGETQUEST));
			stateData = new DisplayStateData<T, CurrentQuestStateRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_DISPLAYSTATE));
		}

		@Override
		public boolean isAllowed(T user) {
			Quest<T> quest = targetQuest.getQuest();
			if (quest == null)
				return false;
			return stateData.isValidState(user.getDisplayState(quest));
		}

		public TargetQuestData<T, CurrentQuestStateRequire> getTargetQuestData() {
			return targetQuest;
		}

		public DisplayStateData<T, CurrentQuestStateRequire> getDisplayStateData() {
			return stateData;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(targetQuest.getInfo());
			info.addAll(stateData.getInfo());
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARequireGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, targetQuest.getQuestSelectorButton(this));
				this.putButton(28, stateData.getDisplaySelectorButton(this));
			}
		}
	}
}
