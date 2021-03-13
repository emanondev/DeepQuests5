package emanondev.deepquests.implementations;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import emanondev.core.PermissionBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.RewardType;
import emanondev.deepquests.interfaces.User;

public abstract class ARewardType<T extends User<T>> extends AType<T, Reward<T>> implements RewardType<T> {

	public ARewardType(String id, QuestManager<T> manager) {
		super(id, manager);
		this.editPermission = new PermissionBuilder(
				"deepquests.editor." + this.getManager().getName() + ".rewardtype." + this.getKeyID())
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

	protected abstract boolean getStandardHiddenValue();
}
