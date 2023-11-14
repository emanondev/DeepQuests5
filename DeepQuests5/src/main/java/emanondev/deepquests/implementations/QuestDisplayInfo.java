package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;
import org.jetbrains.annotations.NotNull;

public class QuestDisplayInfo<T extends User<T>> extends ADisplayInfo<T, Quest<T>> {
    public QuestDisplayInfo(@NotNull YMLSection section, @NotNull Quest<T> quest) {
        super(section, quest);
    }

    @Override
    protected String getBasePath() {
        return "quest-display-defaults";
    }
}
