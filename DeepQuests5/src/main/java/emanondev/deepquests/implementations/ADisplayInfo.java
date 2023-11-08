package emanondev.deepquests.implementations;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.*;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.MapGui;
import emanondev.deepquests.interfaces.DisplayInfo;
import emanondev.deepquests.interfaces.HasDisplay;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import emanondev.deepquests.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

abstract class ADisplayInfo<T extends User<T>, E extends HasDisplay<T>> implements DisplayInfo<T> {
    private final static String PATH_DESC = "description";
    private final static String PATH_ITEM = "item";
    private final static String PATH_HIDE = "hide";
    private final E parent;
    protected final YMLSection section;

    private final EnumMap<DisplayState, Boolean> isDefHidden = new EnumMap<>(DisplayState.class);
    private final EnumMap<DisplayState, Boolean> isDefItem = new EnumMap<>(DisplayState.class);
    private final EnumMap<DisplayState, Boolean> isDefDesc = new EnumMap<>(DisplayState.class);
    private final EnumMap<DisplayState, Boolean> hidden = new EnumMap<>(DisplayState.class);
    private final EnumMap<DisplayState, ItemStack> item = new EnumMap<>(DisplayState.class);
    private final EnumMap<DisplayState, List<String>> desc = new EnumMap<>(
            DisplayState.class);

    public ADisplayInfo(YMLSection section, E parent) {
        if (parent == null || section == null)
            throw new NullPointerException();
        this.parent = parent;
        this.section = section;
        for (DisplayState state : DisplayState.values()) {
            isDefHidden.put(state, !section.contains(state.name().toLowerCase() + "." + PATH_HIDE));
            isDefItem.put(state, !section.contains(state.name().toLowerCase() + "." + PATH_ITEM));
            isDefDesc.put(state, !section.contains(state.name().toLowerCase() + "." + PATH_DESC));

            if (!isDefHidden.get(state))
                hidden.put(state,
                        section.loadBoolean(state.name().toLowerCase() + "." + PATH_HIDE, getDefaultHidden(state)));
            else
                hidden.put(state, getDefaultHidden(state));

            if (!isDefItem.get(state))
                item.put(state,
                        section.loadItemStack(state.name().toLowerCase() + "." + PATH_ITEM, getDefaultItem(state)));
            else
                item.put(state, getDefaultItem(state));

            if (!isDefDesc.get(state))
                desc.put(state, section.loadStringList(state.name().toLowerCase() + "." + PATH_DESC,
                        getDefaultDescription(state)));
            else
                desc.put(state, getDefaultDescription(state));

            // infos.put(state, new Info(state));
        }
    }

    public YMLSection getConfig() {
        return section;
    }

    public E getParent() {
        return parent;
    }

    private boolean getDefaultHidden(DisplayState state) {
        return parent.getManager().getConfig().loadBoolean(getBasePath() + "." + state.name().toLowerCase() + ".hide",
                false);
    }

    private ItemStack getDefaultItem(DisplayState state) {
        return getParent().getManager().getConfig().loadItemStack(
                getBasePath() + "." + state.name().toLowerCase() + ".item",
                new ItemBuilder(Material.BOOK).setGuiProperty().build());
    }

    private List<String> getDefaultDescription(DisplayState state) {
        return getParent().getManager().getConfig().loadStringList(
                getBasePath() + "." + state.name().toLowerCase() + ".description", new ArrayList<>());
    }

    @Override
    public ItemStack getGuiItem(@NotNull DisplayState state, T user, Player player, boolean forceShow) {
        if (user == null)
            throw new NullPointerException("null user");
        if (!forceShow && hidden.get(state))
            return null;
        ItemStack item = getParent().getDisplayItem(getRawItem(state), getRawDescription(state), user, player);

        if (item == null) {
            new IllegalStateException().printStackTrace();
            item = getDefaultItem(state);
        }
        return item;
    }

    @Override
    public ItemStack getRawItem(@NotNull DisplayState state) {
        return new ItemStack(item.get(state));
    }

    @Override
    public ArrayList<String> getRawDescription(@NotNull DisplayState state) {
        return new ArrayList<>(desc.get(state));
    }

    @Override
    public boolean isHidden(@NotNull DisplayState state) {
        return hidden.get(state);
    }

    /**
     * @param item  - setting as null revert to default
     */
    @Override
    public void setItem(@NotNull DisplayState state, ItemStack item) {
        if (item == null) {
            isDefItem.put(state, true);
            item = getDefaultItem(state);
        } else {
            isDefItem.put(state, false);
        }
        if (item == null)
            throw new NullPointerException("default item is null");
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore())
                meta.setLore(null);
            if (meta.hasDisplayName())
                meta.setDisplayName(null);
            item.setItemMeta(meta);
        }
        if (isDefItem.get(state))
            section.set(state.name().toLowerCase() + "." + PATH_ITEM, null);
        else
            section.set(state.name().toLowerCase() + "." + PATH_ITEM, item);
        this.item.put(state, item);
    }

    /**
     * @param hide  - if null revert to default
     */
    @Override
    public void setHide(@NotNull DisplayState state, Boolean hide) {
        if (hide == null) {
            hide = getDefaultHidden(state);
            isDefHidden.put(state, true);
        } else
            isDefHidden.put(state, false);
        hidden.put(state, hide);

        //section.set(state.name().toLowerCase() + "." + PATH_IS_HIDE_DEFAULT, isDefHidden.get(state));
        if (isDefHidden.get(state))
            section.set(state.name().toLowerCase() + "." + PATH_HIDE, null);
        else
            section.set(state.name().toLowerCase() + "." + PATH_HIDE, hide);
    }

    /**
     * @param desc  - if null revert to default
     */
    @Override
    public void setDescription(@NotNull DisplayState state, @Nullable List<String> desc) {
        if (desc == null) {
            desc = getDefaultDescription(state);
            isDefDesc.put(state, true);
        } else
            isDefDesc.put(state, false);

        this.desc.put(state, desc);

        //section.set(state.name().toLowerCase() + "." + PATH_IS_DESC_DEFAULT, isDefDesc.get(state));
        if (isDefDesc.get(state))
            section.set(state.name().toLowerCase() + "." + PATH_DESC, null);
        else
            section.set(state.name().toLowerCase() + "." + PATH_DESC, desc);
    }

    protected abstract String getBasePath();

    public DisplayEditorButton getDisplayEditorButton(Gui parent) {
        return new DisplayEditorButton(parent);
    }

    private class DisplayEditorButton extends AButton {
        private final ItemStack item = new ItemBuilder(Material.PAPER).setGuiProperty().build();

        public DisplayEditorButton(Gui parent) {
            super(parent);
            Utils.updateDescription(item, Arrays.asList("&6&lFull Display Editor", "", "&7Click to interact"),
                    getTargetPlayer(), true);
        }

        @Override
        public ItemStack getItem() {
            return item;
        }

        @Override
        public boolean update() {
            return false;
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
            clicker.openInventory(new DisplaySelector().getInventory());
        }

        private class DisplaySelector extends MapGui {

            public DisplaySelector() {
                super("&9Status Display Editor", 6, DisplayEditorButton.this.getTargetPlayer(),
                        DisplayEditorButton.this.getGui());
                for (int i = 0; i < DisplayState.values().length; i++) {
                    this.putButton(i, new DisplayButton(DisplayState.values()[i]));
                    this.putButton(i + 9, new StateLoreEditorButton(DisplayState.values()[i]));
                    this.putButton(i + 18, new ItemStackEditorButton(DisplayState.values()[i]));
                    this.putButton(i + 27, new HiddenFlagButton(DisplayState.values()[i]));
                    this.putButton(53, new BackButton(this));
                    updateInventory();
                }
            }

            private class StateLoreEditorButton extends StringListEditorButton {
                private final DisplayState state;

                public StateLoreEditorButton(DisplayState state) {
                    super("&9DisplayTextEditor for " + state.name(),
                            new ItemBuilder(Material.PAPER).setGuiProperty().build(), DisplaySelector.this);
                    this.state = state;
                    update();
                }

                @Override
                public List<String> getButtonDescription() {
                    return null;
                }

                @Override
                public List<String> getCurrentList() {
                    if (state == null)
                        return new ArrayList<>();
                    return ADisplayInfo.this.getRawDescription(state);
                }

                @Override
                public boolean onStringListChange(List<String> list) {
                    ADisplayInfo.this.setDescription(state, list);
                    return true;
                }
                @Override
                public void onClick(Player clicker, ClickType click) {
                    if(click==ClickType.SHIFT_RIGHT) {
                        ADisplayInfo.this.setDescription(state,null);
                        getGui().updateInventory();
                        return;
                    }
                    super.onClick(clicker,click);
                }

            }

            private class DisplayButton extends StaticButton {

                public DisplayButton(DisplayState state) {
                    super(Utils.setDescription(new ItemBuilder(Material.GLASS_PANE).setGuiProperty().build(),
                            Arrays.asList("&6&lStatus: &6" + state.name(), "&6" + state.getDescription(),
                                    "&6Click below buttons to edit:", "&6Display Text, Display Item, Hidden status"),
                            DisplaySelector.this.getTargetPlayer(), true), DisplaySelector.this);
                }

                @Override
                public void onClick(Player clicker, ClickType click) {
                }

            }

            private class HiddenFlagButton extends StaticFlagButton {
                private final DisplayState state;

                public HiddenFlagButton(DisplayState state) {
                    super(Utils.setDescription(new ItemBuilder(Material.LIME_WOOL).setGuiProperty().build(),
                                    List.of("&6This status is &anot hidden"), DisplaySelector.this.getTargetPlayer(),
                                    true),
                            Utils.setDescription(new ItemBuilder(Material.RED_WOOL).setGuiProperty().build(),
                                    List.of("&6This status is &chidden"), DisplaySelector.this.getTargetPlayer(),
                                    true),
                            DisplaySelector.this);
                    this.state = state;
                }

                @Override
                public boolean getCurrentValue() {
                    if (state == null)
                        return false;
                    return ADisplayInfo.this.isHidden(state);
                }

                @Override
                public boolean onValueChangeRequest(boolean value) {
                    ADisplayInfo.this.setHide(state, value);
                    return true;
                }

            }

            private class ItemStackEditorButton extends ItemEditorButton {
                private final DisplayState state;

                public ItemStackEditorButton(DisplayState state) {
                    super(DisplaySelector.this);
                    this.state = state;
                }

                @Override
                public ItemStack getCurrentItem() {
                    if (state == null)
                        return null;
                    return ADisplayInfo.this.getRawItem(state);
                }

                @Override
                public void onReicevedItem(ItemStack item) {
                    ADisplayInfo.this.setItem(state, item);
                    this.getGui().updateInventory();
                }

                @Override
                public List<String> getButtonDescription() {
                    return null;
                }

                @Override
                public boolean update() {
                    ItemStack current = getCurrentItem();
                    if (current == null || current.getType() == Material.AIR)
                        this.item = new ItemBuilder(Material.BARRIER).setGuiProperty().build();
                    else
                        this.item = current;
                    return true;
                }

                @Override
                public void onClick(Player clicker, ClickType click) {
                    this.requestItem(clicker, ChatColor.GOLD + "Click to set the item in your main hand");
                }
            }
        }
    }

}
