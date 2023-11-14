package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.User;
import org.jetbrains.annotations.NotNull;

public class MissionDisplayInfo<T extends User<T>> extends ADisplayInfo<T, Mission<T>> {

    public MissionDisplayInfo(@NotNull YMLSection section, @NotNull Mission<T> mission) {
        super(section, mission);
    }

    @Override
    protected String getBasePath() {
        return "mission-display-defaults";
    }
}
