package emanondev.deepquests.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface QuestComponent<T extends User<T>> extends Navigable, Comparable<QuestComponent<T>>, GuiElement {

    /**
     * @return recap of info about this
     */
    List<String> getInfo();

    /**
     * @return the QuestManager of this
     */
    QuestManager<T> getManager();

    /**
     * @return the unique id
     */
    int getID();

    @Override
    default int compareTo(@NotNull QuestComponent<T> qc) {
        if ((getPriority() - qc.getPriority()) != 0)
            return getPriority() - qc.getPriority();
        return qc.getID() - getID();
    }

    /**
     * Allowed values [Integer.MIN_VALUE;Integer.MAX_VALUE]
     *
     * @param priority the value
     */
    void setPriority(int priority);

    /**
     * return the displayName of this
     */
    String getDisplayName();

    /**
     * @param name
     */
    void setDisplayName(String name);

}
