package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

public interface Navigable {

    @Deprecated
    default YMLSection getNavigator() {
        return getConfig();
    }

    YMLSection getConfig();

}
