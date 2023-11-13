package emanondev.deepquests.implementations;

import emanondev.core.PermissionBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.TaskType;
import emanondev.deepquests.interfaces.User;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public abstract class ATaskType<T extends User<T>> extends AType<T, Task<T>> implements TaskType<T> {

    private final Permission editPermission;

    public ATaskType(@NotNull String id, @NotNull QuestManager<T> manager) {
        super(id, manager);
        this.editPermission = new PermissionBuilder(
                "deepquests.editor." + this.getManager().getName() + ".tasktype." + this.getKeyID())
                .setDescription("Allows to edit tasks with type " + getKeyID() + " for manager " + this.getManager().getName())
                .setAccess(PermissionDefault.FALSE).buildAndRegister(getManager().getPlugin(), true);
    }

    public final @NotNull Permission getEditorPermission() {
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
