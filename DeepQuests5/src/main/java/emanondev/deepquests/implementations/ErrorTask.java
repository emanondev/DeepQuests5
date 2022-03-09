package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.interfaces.*;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ErrorTask<T extends User<T>> extends AQuestComponent<T> implements Task<T> {

    private final Mission<T> mission;

    public ErrorTask(int id, Mission<T> mission, YMLSection section) {
        super(id, section, mission.getManager());
        this.mission = mission;
    }

    @Override
    public List<String> getInfo() {
        return Arrays.asList("&cThis task couldn't be loaded correctly",
                "&cMaybe a plugin wasn't loaded correctly",
                "&cExample: NPCKillTask and Citizen not loading",
                "&cIf error wasn't caused by a plugin you can delete",
                "&cthis task or fix it manually editing the database file");
    }

    @Override
    public Mission<T> getMission() {
        return mission;
    }


    @Override
    public double getProgressChance() {
        return 0;
    }

    @Override
    public void setProgressChance(double progressChance) {
    }

    @Override
    public int getMaxProgress() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setMaxProgress(int maxProgress) {
    }

    @Override
    public TaskType<T> getType() {
        return null;
    }

    @Override
    public BarStyle getBossBarStyle() {
        return BarStyle.SOLID;
    }

    @Override
    public BarColor getBossBarColor() {
        return BarColor.BLUE;
    }

    @Override
    public void setBossBarStyle(BarStyle barStyle) {
    }

    @Override
    public void setBossBarColor(BarColor barColor) {
    }

    @Override
    public boolean showBossBar() {
        return false;
    }

    @Override
    public void setShowBossBar(Boolean value) {
    }

    @Override
    public String getTypeName() {
        return "error";
    }

    @Override
    public Gui getEditorGui(Player target, Gui parent) {
        return new PagedMapGui("&4Corrupted Task", 6, target, parent);
    }

    @Override
    public Collection<Reward<T>> getCompleteRewards() {
        return null;
    }

    @Override
    public Reward<T> getCompleteReward(int id) {
        return null;
    }

    @Override
    public boolean addCompleteReward(Reward<T> reward) {
        return false;
    }

    @Override
    public boolean removeCompleteReward(Reward<T> reward) {
        return false;
    }

    @Override
    public Collection<Reward<T>> getProgressRewards() {
        return null;
    }

    @Override
    public Reward<T> getProgressReward(int id) {
        return null;
    }

    @Override
    public boolean addProgressReward(Reward<T> reward) {
        return false;
    }

    @Override
    public boolean removeProgressReward(Reward<T> reward) {
        return false;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void setHidden(Boolean value) {
    }

    @Override
    public boolean isWorldAllowed(World world) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void toggleWorld(String world) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isWorldListWhitelist() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWorldWhitelist(boolean value) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getRawPhaseDescription(Phase phase) {
        return null;
    }

    @Override
    public void setPhaseDescription(String value, Phase phase) {
    }

}
