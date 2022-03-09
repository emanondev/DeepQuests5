package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

import java.util.Collection;

public interface RequireProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Require<T>, RequireType<T>> {

    void registerQuestType(RequireType<T> requireType);

    void registerMissionType(RequireType<T> requireType);

    void registerTaskType(RequireType<T> requireType);

    Collection<RequireType<T>> getQuestTypes();

    Collection<RequireType<T>> getMissionTypes();

    Collection<RequireType<T>> getTaskTypes();

    Require<T> getInstance(int id, QuestManager<T> questManager, YMLSection section);

}
