package emanondev.deepquests.implementations;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import emanondev.core.PermissionBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.RequireType;
import emanondev.deepquests.interfaces.User;

public abstract class ARequireType<T extends User<T>> extends AType<T, Require<T>> implements RequireType<T> {

	public ARequireType(String id, QuestManager<T> manager) {
		super(id, manager);
		this.editPermission = new PermissionBuilder(
				"deepquests.editor." + this.getManager().getName() + ".requiretype." + this.getKeyID())
				.setDescription("Allows to edit rewards with type " + getKeyID()+" for manager "+this.getManager().getName())
				.setAccess(PermissionDefault.FALSE).buildAndRegister(getManager().getPlugin(),true);
	}

	private final Permission editPermission;

	public final Permission getEditorPermission() {
		return editPermission;
	}

	public boolean getDefaultIsHidden() {
		YMLSection config = getProvider().getTypeConfig(this);
		Boolean result = config.loadBoolean(Paths.IS_HIDDEN, null);
		if (result == null) {
			result = getStandardHiddenValue();
			config.set(Paths.IS_HIDDEN, result);
		}
		return result;
	}

	protected boolean getStandardHiddenValue() {
		return false;
	}
}
