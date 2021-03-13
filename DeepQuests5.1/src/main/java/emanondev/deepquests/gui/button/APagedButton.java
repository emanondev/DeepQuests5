package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.PagedGui;

public abstract class APagedButton extends AButton implements PagedButton {

	public APagedButton(PagedGui parent) {
		super(parent);
	}

	@Override
	public PagedGui getGui() {
		return (PagedGui) super.getGui();
	}

}