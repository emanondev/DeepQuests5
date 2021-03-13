package emanondev.deepquests.implementations;

import java.util.*;

import org.bukkit.entity.Player;

import emanondev.core.YMLSection;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.RequireType;
import emanondev.deepquests.interfaces.User;


public abstract class ARequire<T extends User<T>> extends AQuestComponent<T> implements Require<T> {

	private boolean isHidden = false;
	
	public boolean isHidden() {
		return isHidden;
	}
	public void setHidden(Boolean value) {
		if (value!=null && isHidden==value)
			return;
		if (value == null)
			isHidden = getType().getDefaultIsHidden();
		else
			isHidden = value;
		getConfig().set(Paths.IS_HIDDEN, value==null?null:isHidden);
	}
	
	private final RequireType<T> type;

	public ARequire(int id,QuestManager<T> manager,RequireType<T> type,YMLSection section) {
		super(id,section,manager);
		this.type = type;
		getConfig().set(Paths.TYPE_NAME, type.getKeyID());
		isHidden = getConfig().getBoolean(Paths.IS_HIDDEN, getType().getDefaultIsHidden());
	}
	
	public List<String> getInfo(){
		List<String> info = new ArrayList<>();
		info.add("&9&lRequire: &6"+ this.getDisplayName());
		info.add("&8Type: &7"+getType()!=null ? getType().getKeyID() : "&cError");

		info.add("&8KEY: "+ this.getID());
		info.add("");
		info.add("&9Priority: &e"+getPriority());
		
		return info;
	}
	
	/*
	@Override
	public Navigator getNavigator() {
		super.getNavigator();
		nav.setString(Paths.TYPE_NAME, type.getKeyID());
		nav.setBoolean(Paths.IS_HIDDEN, isHidden);
		return nav;
	}*/
	
	@Override
	public final RequireType<T> getType() {
		return type;
	}
	
	protected class ARequireGuiEditor extends AGuiEditor {

		public ARequireGuiEditor(Player player, Gui previusHolder) {
			super("&9Require: &r"+getDisplayName()
				+" &9ID: &e"+getID()
				+" &9Type: &e"+getType().getKeyID(), player, previusHolder);
		}
	}
	
}
