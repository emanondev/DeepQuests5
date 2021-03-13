package emanondev.deepquests.player.requiretypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.core.YMLSection;
import emanondev.deepquests.data.PermissionData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.player.QuestPlayer;

public class PermissionRequireType extends ARequireType<QuestPlayer> {

	public PermissionRequireType(QuestManager<QuestPlayer> manager) {
		super(ID, manager);
	}

	private static final String ID = "permission";

	@Override
	public Material getGuiMaterial() {
		return Material.TRIPWIRE_HOOK;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require a certain permission");
	}

	@Override
	public Require<QuestPlayer> getInstance(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
		return new PermissionRequire(id, manager, section);
	}

	public class PermissionRequire extends ARequire<QuestPlayer> {

		private PermissionData<QuestPlayer, PermissionRequire> permissionData = null;

		public PermissionRequire(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
			super(id, manager, PermissionRequireType.this, section);
			permissionData = new PermissionData<QuestPlayer, PermissionRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_PERMISSIONDATA));
		}

		public PermissionData<QuestPlayer, PermissionRequire> getPermissionData() {
			return permissionData;
		}

		public boolean isAllowed(QuestPlayer p) {
			return permissionData.hasPermission(p.getPlayer());
		}

		@Override
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(permissionData.getInfo());
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARequireGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(28, permissionData.getPermissionButtonEditor(this));
			}
		}
	}

}