package emanondev.deepquests.interfaces;

import java.util.*;

public interface QuestComponent<T extends User<T>> extends Navigable,Comparable<QuestComponent<T>>,GuiElement {
	
	/**
	 * @return recap of info abouth this
	 */
	public List<String> getInfo();
	
	/**
	 * 
	 * @return the QuestManager of this
	 */
	public QuestManager<T> getManager();
	
	/**
	 * 
	 * @return the unique id
	 */
	public int getID();

	@Override
	public default int compareTo(QuestComponent<T> qc) {
		if (qc == null)
			return getPriority();
		if ((getPriority()-qc.getPriority())!=0)
			return getPriority()-qc.getPriority();
		return qc.getID()-getID();
	}
	
	/**
	 * Allowed values [Integer.MIN_VALUE;Integer.MAX_VALUE]
	 * 
	 * @param priority the value
	 * @return true if succesfully updated
	 */
	public void setPriority(int priority);
	
	/**
	 * return the displayName of this
	 */
	public String getDisplayName();
	
	/**
	 * 
	 * @param name
	 */
	public void setDisplayName(String name);

}
