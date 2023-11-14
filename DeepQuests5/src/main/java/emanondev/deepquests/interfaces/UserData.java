package emanondev.deepquests.interfaces;

import org.jetbrains.annotations.NotNull;

public interface UserData<T extends User<T>> extends Navigable {

    @NotNull T getUser();

    void reset();

    void erase();
}
