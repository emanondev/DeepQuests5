package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

public interface Navigable {

    @Deprecated
    default YMLSection getNavigator() {
        return getConfig();
    }

    @NotNull YMLSection getConfig();

}
