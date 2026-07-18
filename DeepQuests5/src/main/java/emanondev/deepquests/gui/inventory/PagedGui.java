package emanondev.deepquests.gui.inventory;

public interface PagedGui extends Gui {

    int getPage();

    boolean setPage(int i);

    default boolean incPage() {
        return setPage(getPage() + 1);
    }

    default boolean decPage() {
        return setPage(getPage() - 1);
    }

    /**
     * returns the biggest slot used <br>
     * (used to calculate max page)
     *
     * @return
     */
    int getMaxSlot();

    default int getMaxPage() {
        return getMaxSlot() / getInventorySize() + 1;
    }

}