package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

public interface RequireType<T extends User<T>> extends QuestComponentType<T, Require<T>> {

    @NotNull Require<T> getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section);

    default @NotNull RequireProvider<T> getProvider() {
        return getManager().getRequireProvider();
    }
}
