package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.GuiElement;
import org.jetbrains.annotations.NotNull;

public abstract class AGuiElementButton<T extends GuiElement> extends AButton implements SortableButton {

    private final T element;

    public AGuiElementButton(@NotNull Gui parent, @NotNull T element) {
        super(parent);
        this.element = element;
    }

    public @NotNull T getElement() {
        return element;
    }

    @Override
    public int getPriority() {
        return element.getPriority();
    }

}
