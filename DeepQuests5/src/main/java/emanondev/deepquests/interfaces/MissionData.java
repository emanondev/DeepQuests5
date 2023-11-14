package emanondev.deepquests.interfaces;

import org.jetbrains.annotations.NotNull;

public interface MissionData<T extends User<T>> extends ComplexData<T> {

    @NotNull Mission<T> getMission();

}
