package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RequireProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Require<T>, RequireType<T>> {

    void registerQuestType(@NotNull RequireType<T> requireType);

    void registerMissionType(@NotNull RequireType<T> requireType);

    void registerTaskType(@NotNull RequireType<T> requireType);

    @NotNull Collection<RequireType<T>> getQuestTypes();

    @NotNull Collection<RequireType<T>> getMissionTypes();

    @NotNull Collection<RequireType<T>> getTaskTypes();

    @NotNull Require<T> getInstance(int id, @NotNull YMLSection section);

}
