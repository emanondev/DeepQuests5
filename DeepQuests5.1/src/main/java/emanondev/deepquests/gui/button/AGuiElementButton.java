package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.GuiElement;

public abstract class AGuiElementButton<T extends GuiElement> extends AButton implements SortableButton {

	private T element;
	
	public AGuiElementButton(Gui parent,T element) {
		super(parent);
		this.element = element;
	}
	
	public T getElement() {
		return element;
	}

	@Override
	public int getPriority() {
		return element.getPriority();
	}
	
}
