package emanondev.deepquests.gui.button;

import org.jetbrains.annotations.NotNull;

public interface SortableButton extends Button, Comparable<SortableButton> {

    int getPriority();

    default int compareTo(@NotNull SortableButton button) {
        return button.getPriority() - getPriority();
    }

}
