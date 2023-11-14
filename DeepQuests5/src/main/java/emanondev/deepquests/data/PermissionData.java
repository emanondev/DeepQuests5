package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private String permission;

    public PermissionData(@NotNull E parent, @NotNull YMLSection section) {
        super(parent, section);
        permission = getConfig().getString(Paths.DATA_PERMISSION, null);
        if (permission != null && (permission.contains(" ") || permission.isEmpty()))
            permission = null;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(@Nullable String value) {
        if (Objects.equals(this.permission, value))
            return;
        if (value != null && (value.contains(" ") || value.isEmpty()))
            return;
        this.permission = value;
        getConfig().set(Paths.DATA_PERMISSION, permission);
    }

    public boolean hasPermission(Player player) {
        if (permission == null || player == null)
            return false;
        return player.hasPermission(permission);
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        if (getPermission() == null)
            info.add("&9Permission &cnot setted");
        else
            info.add("&9Permission &e" + getPermission());
        return info;
    }

    public @NotNull Button getPermissionButtonEditor(@NotNull Gui gui) {
        return new PermissionEditor(gui);
    }

    private class PermissionEditor extends TextEditorButton {

        public PermissionEditor(Gui parent) {
            super(new ItemBuilder(Material.TRIPWIRE_HOOK).setGuiProperty().build(), parent);
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
            this.requestText(clicker, permission, "&6Click and write the permission");
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6Permission Editor Button");
            list.addAll(getInfo());
            return list;
        }

        @Override
        public void onReicevedText(String text) {
            setPermission(text);
        }
    }
}