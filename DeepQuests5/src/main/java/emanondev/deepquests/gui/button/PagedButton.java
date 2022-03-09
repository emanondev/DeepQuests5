package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.PagedGui;

public interface PagedButton extends Button {
    PagedGui getGui();

    /**
     * @return current page
     */
    default int getPage() {
        return getGui().getPage();
    }
}