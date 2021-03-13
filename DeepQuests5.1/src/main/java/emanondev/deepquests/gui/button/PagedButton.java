package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.PagedGui;

public interface PagedButton extends Button {
	public PagedGui getGui();
	
	/**
	 * @return current page
	 */
	public default int getPage() {
		return getGui().getPage();
	}
}