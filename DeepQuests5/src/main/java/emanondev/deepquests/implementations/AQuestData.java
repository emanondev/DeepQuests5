package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestData;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class AQuestData<T extends User<T>> extends AUserComplexData<T> implements QuestData<T> {
    private final Quest<T> quest;
    private int missionPoints;

    public AQuestData(@NotNull T user, @NotNull Quest<T> quest, @NotNull YMLSection section) {
        super(user, section);
        this.quest = quest;
        missionPoints = getConfig().getInteger(Paths.USERDATA_POINTS, 0);
    }

    public @NotNull Quest<T> getQuest() {
        return quest;
    }

    public long getCooldownTimeLeft() {
        long val = getSelfCooldown();
        if (getUser().getDisplayState(quest) != DisplayState.COOLDOWN)
            return val;
        long val2 = getMissionsCooldown();
        return Math.max(val, val2);

    }

    public boolean isOnSelfCooldown() {
        return quest.isRepeatable() && getSelfCooldown() > 0;
    }

    public long getSelfCooldown() {
        return Math.max(0,
                Math.max(getLastCompleted(), getLastFailed()) + quest.getCooldownTime() - new Date().getTime());
    }

    @Override
    public boolean isOnMissionCooldown() {
        return getMissionsCooldown() > 0;
    }

    @Override
    public long getMissionsCooldown() {
        boolean found = false;
        long val = Long.MAX_VALUE;
        for (Mission<T> m : quest.getMissions()) {
            long tmp = getUser().getMissionData(m).getCooldownTimeLeft();
            if (!found && tmp > 0)
                found = true;
            if (tmp > 0)
                val = Math.min(val, tmp);
        }
        if (!found)
            return 0L;
        return val;
    }

    public boolean isOnCooldown() {
        return quest.isRepeatable() && getCooldownTimeLeft() > 0;
    }

    @Override
    public void reset() {
        super.reset();
        for (Mission<T> mission : getQuest().getMissions())
            getUser().getMissionData(mission).reset();
    }

    @Override
    public void erase() {
        for (Mission<T> mission : getQuest().getMissions())
            getUser().getMissionData(mission).erase();
        getUser().eraseQuestData(getQuest());
    }

    @Override
    public int getPoints() {
        return missionPoints;
    }

    @Override
    public void setPoints(int amount) {
        missionPoints = amount;
        if (missionPoints == 0)
            getConfig().set(Paths.USERDATA_POINTS, null);
        else
            getConfig().set(Paths.USERDATA_POINTS, missionPoints);
    }

}
