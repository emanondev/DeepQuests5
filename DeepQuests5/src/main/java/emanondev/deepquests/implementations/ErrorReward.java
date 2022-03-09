package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.RewardType;
import emanondev.deepquests.interfaces.User;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ErrorReward<T extends User<T>> extends AQuestComponent<T> implements Reward<T> {

    public ErrorReward(int id, QuestManager<T> manager, YMLSection section) {
        super(id, section, manager);
    }

    @Override
    public String getTypeName() {
        return "error";
    }

    @Override
    public Gui getEditorGui(Player target, Gui parent) {
        return new PagedMapGui("&4Corrupted Reward", 6, target, parent);
    }

    @Override
    public RewardType<T> getType() {
        return null;
    }

    @Override
    public void apply(T user, int amount) {

    }

    @Override
    public List<String> getInfo() {
        return Arrays.asList("&cThis reward couldn't be loaded correctly", "&cMaybe a plugin wasn't loaded correctly",
                "&cExample: NPCKillTask and Citizen not loading", "&cIf error wasn't caused by a plugin you can delete",
                "&cthis task or fix it manually editing the database file");
    }

    @Override
    public void feedback(T user, int amount) {
    }

    @Override
    public String getRawFeedback() {
        return "";
    }

    @Override
    public String getFeedback(T user, int amount) {
        return "";
    }

    @Override
    public void setFeedback(String feedback) {
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(Boolean value) {
    }

}
