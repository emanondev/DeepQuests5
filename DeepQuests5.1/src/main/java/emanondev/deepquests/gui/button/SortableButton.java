package emanondev.deepquests.gui.button;

public interface SortableButton extends Button,Comparable<SortableButton>{
	
	public int getPriority();
	
	public default int compareTo(SortableButton button) {
		if (button==null)
			return -getPriority();
		return button.getPriority()-getPriority();
	}

}
