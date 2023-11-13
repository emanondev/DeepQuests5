package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.RequireType;
import emanondev.deepquests.interfaces.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ErrorRequire<T extends User<T>> extends AQuestComponent<T> implements Require<T> {

    public ErrorRequire(int id, QuestManager<T> manager, YMLSection section) {
        super(id, section, manager);
    }

    @Override
    public String getTypeName() {
        return "error";
    }

    @Override
    public @NotNull Gui getEditorGui(Player target, Gui parent) {
        return new PagedMapGui("&4Corrupted Require", 6, target, parent);
    }

    @Override
    public @NotNull RequireType<T> getType() {
        return null;
    }

    @Override
    public @NotNull List<String> getInfo() {
        return Arrays.asList("&cThis require couldn't be loaded correctly",
                "&cMaybe a plugin wasn't loaded correctly",
                "&cExample: NPCKillTask and Citizen not loading",
                "&cIf error wasn't caused by a plugin you can delete",
                "&cthis task or fix it manually editing the database file");
    }

    @Override
    public boolean isAllowed(T user) {
        return false;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void setHidden(Boolean value) {

    }
}
