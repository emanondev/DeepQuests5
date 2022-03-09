package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;

public abstract class AButton implements Button {
    private final Gui parent;

    public AButton(Gui parent) {
        this.parent = parent;
    }

    public Gui getGui() {
        return parent;
    }
}