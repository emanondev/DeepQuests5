package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.PagedGui;
import org.jetbrains.annotations.NotNull;

public abstract class APagedButton extends AButton implements PagedButton {

    public APagedButton(PagedGui parent) {
        super(parent);
    }

    @Override
    public @NotNull PagedGui getGui() {
        return (PagedGui) super.getGui();
    }

}