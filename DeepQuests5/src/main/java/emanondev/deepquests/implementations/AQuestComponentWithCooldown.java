package emanondev.deepquests.implementations;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AmountSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.HasCooldown;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.StringUtils;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class AQuestComponentWithCooldown<T extends User<T>> extends AQuestComponentWithWorlds<T>
        implements HasCooldown<T> {

    public AQuestComponentWithCooldown(int id, YMLSection section, QuestManager<T> manager) {
        super(id, section, manager);
        cooldownMinutes = Math.max(0L, getConfig().loadLong(Paths.COOLDOWN_MINUTES, 1440L));
        repeatable = getConfig().loadBoolean(Paths.REPEATABLE, false);
    }
	
	/*
	public Navigator getNavigator() {
		super.getNavigator();
		nav.setLong(Paths.COOLDOWN_MINUTES,cooldownMinutes);
		nav.setBoolean(Paths.REPEATABLE,repeatable);
		return nav;
	}*/

    private long cooldownMinutes;
    private boolean repeatable;

    @Override
    public long getCooldownMinutes() {
        return cooldownMinutes;
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public void setCooldownMinutes(long cooldown) {
        if (cooldownMinutes == cooldown)
            return;
        cooldownMinutes = Math.max(0L, cooldown);
        getConfig().set(Paths.COOLDOWN_MINUTES, cooldownMinutes);
        getConfig().saveAsync();
    }

    @Override
    public void setRepeatable(boolean value) {
        if (repeatable == value)
            return;
        repeatable = value;
        getConfig().set(Paths.REPEATABLE, repeatable);
        getConfig().saveAsync();
    }

    protected class AAAGuiEditor extends AAGuiEditor {

        public AAAGuiEditor(String title, Player player, Gui previousHolder) {
            super(title, player, previousHolder);
            this.putButton(9, new CooldownEditorButton());
        }

        private class CooldownEditorButton extends AmountSelectorButton {

            public CooldownEditorButton() {
                super("Cooldown Editor", new ItemBuilder(Material.CLOCK).setGuiProperty().build(),
                        AAAGuiEditor.this,
                        1L, 10L, 60L, 360L, 1440L, 7 * 1440L, 30 * 1440L);
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6Cooldown Editor Button");

                if (isRepeatable()) {
                    desc.add("&9Current Status: &aEnabled");
                    desc.add("&9Minutes: &e" + getCooldownMinutes());
                    desc.add("&9Time: &e" + StringUtils.getStringCooldown(getCooldownTime()));
                } else {
                    desc.add("&9Current Status: &cDisabled");
                    desc.add("&9&mMinutes: &e&m" + getCooldownMinutes());
                    desc.add("&9&mTime: &e&m" + StringUtils.getStringCooldown(getCooldownTime()));
                }

                desc.add("");
                if (isRepeatable())
                    desc.add("&7Right Click to disable");
                else
                    desc.add("&7Right Click to enable");
                desc.add("&7Left Click to edit");
                return desc;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                switch (click) {
                    case RIGHT, SHIFT_RIGHT -> {
                        setRepeatable(!isRepeatable());
                        updateInventory();
                        return;
                    }
                    default -> {
                    }
                }
                super.onClick(clicker, click);
            }

            @Override
            public long getCurrentAmount() {
                return getCooldownMinutes();
            }

            @Override
            public boolean onAmountChangeRequest(long value) {
                setCooldownMinutes(value);
                return true;
            }

            @Override
            protected ItemStack craftEditorAmountButtonItem(long amount) {
                ItemStack item;
                if (amount > 0) {
                    item = new ItemBuilder(Material.LIME_WOOL).build();
                    Utils.updateDescription(item, Arrays.asList("&aAdd " + StringUtils.getStringCooldown(amount * 60 * 1000), "&7" + amount + " minutes"), null, true);
                } else {
                    item = new ItemBuilder(Material.RED_WOOL).build();
                    Utils.updateDescription(item, Arrays.asList("&cRemove " + StringUtils.getStringCooldown(-amount * 60 * 1000), "&7" + (-amount) + " minutes"), null, true);
                }
                return item;
            }
        }

    }

}
