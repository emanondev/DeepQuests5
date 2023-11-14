package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.RequireType;
import emanondev.deepquests.interfaces.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public abstract class ARequire<T extends User<T>> extends AQuestComponent<T> implements Require<T> {

    private final RequireType<T> type;
    private boolean isHidden;

    public ARequire(int id, @NotNull QuestManager<T> manager, @NotNull RequireType<T> type, @NotNull YMLSection section) {
        super(id, section, manager);
        this.type = type;
        getConfig().set(Paths.TYPE_NAME, type.getKeyID());
        isHidden = getConfig().getBoolean(Paths.IS_HIDDEN, getType().getDefaultIsHidden());
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(Boolean value) {
        if (value != null && isHidden == value)
            return;
        isHidden = Objects.requireNonNullElse(value, getType().getDefaultIsHidden());
        getConfig().set(Paths.IS_HIDDEN, value == null ? null : isHidden);
    }

    public @NotNull List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add("&9&lRequire: &6" + this.getDisplayName());
        info.add("&8Type: &7" + getTypeName());

        info.add("&8KEY: " + this.getID());
        info.add("");
        info.add("&9Priority: &e" + getPriority());

        return info;
    }

    @Override
    public final @NotNull RequireType<T> getType() {
        return type;
    }

    protected class ARequireGuiEditor extends AGuiEditor {

        public ARequireGuiEditor(Player player, Gui previousHolder) {
            super("&9Require: &r" + ARequire.this.getDisplayName()
                    + " &9ID: &e" + ARequire.this.getID()
                    + " &9Type: &e" + ARequire.this.getTypeName(), player, previousHolder);
        }
    }

}
