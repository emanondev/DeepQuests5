package emanondev.deepquests.interfaces;

import java.util.*;

import emanondev.core.YMLSection;

public interface RequireProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Require<T>, RequireType<T>> {

	public void registerQuestType(RequireType<T> requireType);

	public void registerMissionType(RequireType<T> requireType);

	public void registerTaskType(RequireType<T> requireType);

	public Collection<RequireType<T>> getQuestTypes();

	public Collection<RequireType<T>> getMissionTypes();

	public Collection<RequireType<T>> getTaskTypes();

	public Require<T> getInstance(int id, QuestManager<T> questManager, YMLSection section);

}
