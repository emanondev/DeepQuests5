package emanondev.deepquests.data;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;

import emanondev.deepquests.gui.button.AButton;
import emanondev.deepquests.gui.button.AmountSelectorButton;
import emanondev.deepquests.gui.button.FlagButton;
import emanondev.deepquests.gui.button.ItemEditorButton;
import emanondev.deepquests.gui.button.StaticFlagButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.Utils;

public class ToolData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

	public ToolData(E parent, YMLSection section) { super(parent,section);
		this.enabled = getConfig().getBoolean("is-enabled", this.enabled);
		this.item = getConfig().getItemStack("item", this.item);
		this.doCheckAmount = getConfig().getBoolean("check-amount",this.doCheckAmount);
		this.amountValue = getConfig().getInteger("amount-value", this.amountValue);
		this.doCheckDamage = getConfig().getBoolean("check-damage", this.doCheckDamage);
		this.damageValue = getConfig().getInteger("damage-value", this.damageValue);
		this.easyCheck = getConfig().getBoolean("easy-check", this.easyCheck);
		this.doCheckUnbreakable = getConfig().getBoolean("check-unbreakable", this.doCheckUnbreakable);
		this.unbreakableValue = getConfig().getBoolean("unbreakable-value", this.unbreakableValue);
		this.doCheckDisplayName = getConfig().getBoolean("check-display-name", this.doCheckDisplayName);
		this.displayNameValue = getConfig().getString("display-name-value", this.displayNameValue);
		this.doCheckLore = getConfig().getBoolean("check-lore", this.doCheckLore);
		this.loreValue = getConfig().getStringList("lore-value", this.loreValue);
		this.doCheckFlags = getConfig().getBoolean("check-flags", this.doCheckFlags);
		this.flagValues.addAll(getConfig().loadEnumSet("flags-value", EnumSet.noneOf(ItemFlag.class),ItemFlag.class));
		this.doCheckEnchants = getConfig().getBoolean("check-enchants", this.doCheckEnchants);
		this.doCheckAttributes = getConfig().getBoolean("check-attributes", this.doCheckAttributes);
		this.usePlaceHolder = getConfig().getBoolean("use-placeholderapi", this.usePlaceHolder);
		//TODO ench list
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	/*
	@Override
	public Navigator getNavigator() {
		getConfig().setBoolean("is-enabled", this.enabled);
		getConfig().setBoolean("check-amount",this.doCheckAmount);
		getConfig().setInteger("amount-value", this.amountValue);
		getConfig().setBoolean("check-damage", this.doCheckDamage);
		getConfig().setInteger("damage-value", this.damageValue);
		getConfig().setBoolean("easy-check", this.easyCheck);
		getConfig().setBoolean("check-unbreakable", this.doCheckUnbreakable);
		getConfig().setBoolean("unbreakable-value", this.unbreakableValue);
		getConfig().setBoolean("check-display-name", this.doCheckDisplayName);
		getConfig().setString("display-name-value", this.displayNameValue);
		getConfig().setBoolean("check-lore", this.doCheckLore);
		getConfig().setStringList("lore-value", this.loreValue);
		getConfig().setBoolean("check-flags", this.doCheckFlags);
		getConfig().setEnumCollection("flags-value", flagValues);
		getConfig().setBoolean("check-enchants", this.doCheckEnchants);
		getConfig().setBoolean("check-attributes", this.doCheckAttributes);
		getConfig().setBoolean("use-placeholderapi", this.usePlaceHolder);
		//TODO ench list
		return nav;
	}*/

	private boolean enabled = false;
	private ItemStack item = null;
	
	public ItemStack getItem() {
		return item;
	}

	private boolean doCheckAmount = false;
	private int amountValue = 1;

	private boolean doCheckDamage = false;
	private int damageValue = 0;

	private boolean easyCheck = true;

	private boolean doCheckUnbreakable = true;
	private boolean unbreakableValue = false;

	private boolean doCheckDisplayName = true;
	private String displayNameValue = null;

	private boolean doCheckLore = true;
	private List<String> loreValue = null;

	private boolean doCheckFlags = true;
	private final EnumSet<ItemFlag> flagValues = EnumSet.noneOf(ItemFlag.class);

	private boolean doCheckEnchants = true;
	private final HashMap<Enchantment, EnchantCheck> enchMap = new HashMap<>();

	private boolean doCheckAttributes = true;
	//private HashMap<Enchantment,EnchantCheckType> enchMap = new HashMap<>();

	private boolean usePlaceHolder = true;
	
	public void setItem(ItemStack item) {
		this.item = item;
		getConfig().set("item", this.item);
		if (item==null)
			return;
		ItemMeta meta = item.getItemMeta();
		amountValue = item.getAmount();
		damageValue = meta instanceof Damageable? ((Damageable) meta).getDamage():0;
		unbreakableValue = meta.isUnbreakable();
		displayNameValue = meta.getDisplayName();
		loreValue = meta.getLore();
		flagValues.clear();
		flagValues.addAll(meta.getItemFlags());
		enchMap.clear();
		meta.getEnchants().forEach((key,value)->enchMap.put(key, new EnchantCheck(EnchantCheckType.EQUALS,value)));

		getConfig().set("amount-value", this.amountValue);
		getConfig().set("damage-value", this.damageValue);
		getConfig().set("unbreakable-value", this.unbreakableValue);
		getConfig().set("display-name-value", this.displayNameValue);
		getConfig().set("lore-value", this.loreValue);
		getConfig().setEnumsAsStringList("flags-value", flagValues);
	}

	public boolean isValidTool(ItemStack tool, Player p) {
		if (!isEnabled())
			return true;
		if (this.item == null && tool == null)
			return true;
		if (this.item == null || tool == null)
			return false;
		if (this.item.getType() != tool.getType())
			return false;
		if (this.doCheckAmount && tool.getAmount() != this.amountValue)
			return false;

		tool = new ItemStack(tool);
		ItemStack item = new ItemStack(this.item);
		ItemMeta itemMeta = item.getItemMeta();
		ItemMeta toolMeta = tool.getItemMeta();
		if (this.doCheckDamage && toolMeta instanceof Damageable
				&& ((Damageable) toolMeta).getDamage() != this.damageValue)
			return false;
		if (toolMeta instanceof Damageable)
			((Damageable) itemMeta).setDamage(((Damageable) toolMeta).getDamage());
		if (this.usePlaceHolder && this.displayNameValue != null)
			itemMeta.setDisplayName(Utils.fixString(this.displayNameValue, p, true));
		if (this.usePlaceHolder && this.loreValue != null)
			itemMeta.setLore(Utils.fixList(this.loreValue, p, true));
		item.setItemMeta(itemMeta);

		if (this.easyCheck) {
			if (tool.isSimilar(item))
				return true;
			else
				return false;
		}
		if (toolMeta.equals(itemMeta))
			return true;
		if (!this.doCheckDisplayName)
			itemMeta.setDisplayName(toolMeta.getDisplayName());
		if (!this.doCheckLore)
			itemMeta.setLore(toolMeta.getLore());
		if (!this.doCheckAttributes)
			itemMeta.setAttributeModifiers(toolMeta.getAttributeModifiers());
		if (!this.doCheckEnchants || areValidEnchants(toolMeta)) {
			for (Enchantment ench : itemMeta.getEnchants().keySet())
				itemMeta.removeEnchant(ench);
			for (Enchantment ench : toolMeta.getEnchants().keySet())
				itemMeta.addEnchant(ench, toolMeta.getEnchantLevel(ench), true);
		}
		if (!this.doCheckAttributes)
			itemMeta.setAttributeModifiers(toolMeta.getAttributeModifiers());
		if (!this.doCheckUnbreakable)
			itemMeta.setUnbreakable(toolMeta.isUnbreakable());
		if (!this.doCheckFlags) {
			itemMeta.removeItemFlags(ItemFlag.values());
			itemMeta.addItemFlags(toolMeta.getItemFlags().toArray(new ItemFlag[0]));
		}

		if (toolMeta.equals(itemMeta))
			return true;
		return false;
	}

	private boolean areValidEnchants(ItemMeta meta) {
		for (Enchantment ench : enchMap.keySet())
			if (!enchMap.get(ench).check(meta.getEnchantLevel(ench)))
				return false;
		return true;
	}

	private class EnchantCheck {
		private EnchantCheckType type;
		private int level;

		private EnchantCheck(EnchantCheckType type, int level) {
			if (type == null)
				throw new NullPointerException();
			this.type = type;
			this.level = level;
		}

		private void setType(EnchantCheckType type) {
			if (type == null)
				throw new NullPointerException();
			this.type = type;
		}

		private void setLevel(int level) {
			this.level = level;
		}

		private boolean check(int level) {
			switch (type) {
			case DIFFERENT:
				return this.level != level;
			case EQUALS:
				return this.level == level;
			case EQUALS_OR_HIGHTER:
				return this.level <= level;
			case EQUALS_OR_LOWER:
				return this.level >= level;
			case NONE:
				return true;
			default:
				throw new IllegalStateException();
			}
		}
	}

	private enum EnchantCheckType {
		/**
		 * no check
		 */
		NONE,

		/**
		 * must be equals
		 */
		EQUALS,

		/**
		 * all enchants must be lower or equals
		 */
		EQUALS_OR_LOWER,

		/**
		 * tool might have highter or equals enchant levels only on
		 */
		EQUALS_OR_HIGHTER,

		/**
		 * tool must not have enchant or have different enchants
		 */
		DIFFERENT,
	}

	/**
	 * 
	 * @param title name of Button like Weapon Check Button
	 * @param gui parent gui
	 * @param startingSlot any 9 multiple number, all slots from that value to value+17 are
	 *            reserved
	 */
	public void setupButtons(String title, PagedMapGui gui, int startingSlot) {
		gui.putButton(startingSlot, new EnableToolCheckFlag(title, gui));
		gui.putButton(startingSlot+9, new ItemEditor(gui));
		gui.putButton(startingSlot+10, new ItemDisplayButton(gui));
		gui.putButton(startingSlot + 2, new AmountEditor(gui));
		gui.putButton(startingSlot + 11, new DamageEditor(gui));
		gui.putButton(startingSlot + 3, new SimpleCheckFlag(gui));
		// TODO complete
	}
	
	public void setAmount(int amount) {
		amount = (int) Math.max(1,Math.min(127,amount));
		this.amountValue = amount;
		if (this.item!=null)
			this.item.setAmount(amount);
		getConfig().set("amount-value", this.amountValue);
	}

	public void setDamage(int damage) {
		damage = (int) Math.max(0,Math.min(damage,1562));
		this.damageValue = damage;
		if (this.item!=null) {
			ItemMeta meta = item.getItemMeta();
			if (meta instanceof Damageable) {
				((Damageable) meta).setDamage(damage);
				item.setItemMeta(meta);
			}
		}
		getConfig().set("amount-value", this.amountValue);
	}

	private class SimpleCheckFlag extends StaticFlagButton {

		public SimpleCheckFlag(Gui parent) {
			super(Utils.setDescription(new ItemBuilder(Material.ORANGE_BANNER).setGuiProperty().build(),
					Arrays.asList("&6Simple check", "&9Status: &cDisabled", "", "&7Click to toggle"), null, true),
					Utils.setDescription(new ItemBuilder(Material.LIME_BANNER).setGuiProperty().build(),
							Arrays.asList("&6Simple check", "&9Status: &aEnabled", "", "&7Click to toggle"), null,
							true),
					parent);
		}

		@Override
		public boolean getCurrentValue() {
			return easyCheck;
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			easyCheck = !easyCheck;
			return true;
		}
		
		public void onClick(Player clicker, ClickType click) {
			if (!enabled || ToolData.this.getItem()==null)
				return;
			super.onClick(clicker, click);
		}
		
		public ItemStack getItem() {
			if (!enabled|| ToolData.this.getItem()==null)
				return null;
			return super.getItem();
		}
	}

	
	private class EnableToolCheckFlag extends FlagButton {
		private String title;

		public EnableToolCheckFlag(String title, Gui parent) {
			super(new ItemBuilder(Material.RED_BANNER).setGuiProperty().build(),
					new ItemBuilder(Material.GREEN_BANNER).setGuiProperty().build(), parent);
			this.title = title;
		}

		@Override
		public boolean getCurrentValue() {
			return enabled;
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			enabled = !enabled;
			return true;
		}

		@Override
		public List<String> getButtonDescription() {
			return getInfo(title);
		}
	}
	private class AmountEditor extends AmountSelectorButton {

		public AmountEditor(Gui parent) {
			super("&9Amount Editor", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent, 1L,4L,8L,16L,32L,64L,128L);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> list = new ArrayList<>();
			list.add("&6Amount");
			if (!doCheckAmount) {
				list.add("&9Amount is &enot checked");
				list.add("&9Any amount is fine");
				list.add("");
				list.add("&7Right click to enable");
				return list;
			}
			list.add("&9Amount is &echecked");
			list.add("&9Value: &a"+amountValue);
			list.add("");
			list.add("&7Right click to disable");
			list.add("&7Left click to edit amount");
			return list;
		}

		@Override
		public long getCurrentAmount() {
			return amountValue;
		}

		@Override
		public boolean onAmountChangeRequest(long value) {
			ToolData.this.setAmount((int) value);
			return true;
		}

		public void onClick(Player clicker, ClickType click) {
			if (!enabled || ToolData.this.getItem()==null)
				return;
			if (click==ClickType.RIGHT) {
				ToolData.this.doCheckAmount = !ToolData.this.doCheckAmount;
				this.getGui().updateInventory();
				return;
			}
			super.onClick(clicker, click);
		}
		
		public ItemStack getItem() {
			if (!enabled || ToolData.this.getItem()==null)
				return null;
			return super.getItem();
		}

	}
	private class DamageEditor extends AmountSelectorButton {

		public DamageEditor(Gui parent) {
			super("&9Damage Editor", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent, 1, 5,10,50,100,500,1000);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> list = new ArrayList<>();
			list.add("&6Damage");
			if (!doCheckDamage) {
				list.add("&9Damage is &enot checked");
				list.add("&9Any amount is fine");
				list.add("");
				list.add("&7Right click to enable");
				return list;
			}
			list.add("&9Damage is &echecked");
			list.add("&9Value: &a"+damageValue);
			list.add("");
			list.add("&7Right click to disable");
			list.add("&7Left click to edit amount");
			return list;
		}

		@Override
		public long getCurrentAmount() {
			return damageValue;
		}

		@Override
		public boolean onAmountChangeRequest(long value) {
			ToolData.this.setDamage((int) value);
			return true;
		}

		public void onClick(Player clicker, ClickType click) {
			if (!enabled|| ToolData.this.getItem()==null || !(ToolData.this.getItem().getItemMeta() instanceof Damageable))
				return;
			if (click==ClickType.RIGHT) {
				ToolData.this.doCheckDamage = !ToolData.this.doCheckDamage;
				this.getGui().updateInventory();
				return;
			}
			super.onClick(clicker, click);
		}
		
		public ItemStack getItem() {
			if (!enabled|| ToolData.this.getItem()==null || !(ToolData.this.getItem().getItemMeta() instanceof Damageable))
				return null;
			return super.getItem();
		}

	}
	public List<String> getInfo() {
		ArrayList<String> list = new ArrayList<>();
		if (!enabled) {
			list.add("  &9Check &cDisabled");
			return list;
		}
		if (item == null) {
			list.add("  &cNo tool should be used");
			list.add("  &9(&eEmpty Hand&9)");
			return list;
		}
		list.add("  &9Material: &e" + item.getType());
		if (doCheckAmount)
			list.add("  &9Amount: &e" + amountValue);
		else
			list.add("  &9Amount: &7Not Checked");
		if ((item.getItemMeta()) instanceof Damageable) {
			if (doCheckDamage)
				list.add("  &9Damage: &e" + ((Damageable) item.getItemMeta()).getDamage());
			else
				list.add("  &9Damage: &7Not Checked");
		}
		if (easyCheck)
			return list;
		if (doCheckDisplayName)
			list.add("  &9DisplayName: &e" + displayNameValue == null ? "&cNONE" : displayNameValue);
		else
			list.add("  &9DisplayName: &7Not Checked");

		if (doCheckLore)
			if (loreValue == null)
				list.add("  &9Lore: &cNONE");
			else {
				list.add("  &9Lore:");
				for (String lore : loreValue)
					list.add("  &9- '&r" + lore + "&9'");
			}
		else
			list.add("  &9Lore: &7Not Checked");

		if (doCheckUnbreakable)
			list.add("  &9Unbreakable: &7Not Checked");
		else
			list.add("  &9Unbreakable: &e" + unbreakableValue);

		if (doCheckLore)
			if (loreValue == null)
				list.add("  &9ItemFlags: &cNONE");
			else {
				list.add("  &9ItemFlags:");
				for (ItemFlag flag : item.getItemMeta().getItemFlags())
					list.add("  &9- '&r" + flag + "&9'");
			}
		else
			list.add("  &9ItemFlags: &7Not Checked");

		if (doCheckLore)
			if (loreValue == null)
				list.add("  &9ItemFlags: &cNONE");
			else {
				list.add("  &9ItemFlags:");
				for (ItemFlag flag : item.getItemMeta().getItemFlags())
					list.add("  &9- '&r" + flag + "&9'");
			}
		else
			list.add("  &9ItemFlags: &7Not Checked");

		list.add("  &cInfo Not Completed");
		return list;

	}

	private List<String> getInfo(String title) {
		if (!enabled)
			return Arrays.asList(title, "&cDisabled", "", "&6Click to Toggle");
		ArrayList<String> list = new ArrayList<>();
		list.add(title);
		list.addAll(getInfo());
		list.add("");
		list.add("&6Click to Toggle");
		return list;
	}

	private class ItemDisplayButton extends AButton {

		public ItemDisplayButton(Gui parent) {
			super(parent);
		}

		@Override
		public ItemStack getItem() {
			if (!enabled)
				return null;
			return ToolData.this.getItem();
		}

		@Override
		public boolean update() {
			return true;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
		}
		
	}
	
	
	private class ItemEditor extends ItemEditorButton {

		public ItemEditor(Gui parent) {
			super(parent);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (!enabled)
				return;
			this.requestItem(clicker, "&6Select the item");
		}

		@Override
		public ItemStack getCurrentItem() {
			return ToolData.this.getItem();
		}

		@Override
		public void onReicevedItem(ItemStack item) {
			setItem(item);
			getGui().updateInventory();
		}

		@Override
		public List<String> getButtonDescription() {
			if (ToolData.this.getItem()==null) {
				return Arrays.asList("&6ItemEditor Button","&cNo item Setted","&7Click to set");
			}
			return Arrays.asList("&6ItemEditor Button","&9Item as on right slot","&7Click to set");
		}
		
		public ItemStack getItem() {
			if (!enabled)
				return null;
			if (ToolData.this.getItem()==null)
				return Utils.setDescription(
						new ItemBuilder(Material.BARRIER).setGuiProperty().build(),
						getButtonDescription(),null,true);
			return Utils.setDescription(
					new ItemBuilder(ToolData.this.getItem()).setGuiProperty().build(),
					getButtonDescription(),null,true);
		}
		
	}

}