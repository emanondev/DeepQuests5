package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import org.jetbrains.annotations.NotNull;

public abstract class AButton implements Button {
    private final Gui parent;

    public AButton(@NotNull Gui parent) {
        this.parent = parent;
    }

    public @NotNull Gui getGui() {
        return parent;
    }
}