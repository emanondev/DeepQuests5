package emanondev.deepquests.gui;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLConfig;
import emanondev.deepquests.Quests;
import emanondev.deepquests.utils.DisplayState;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

public class GuiConfig {
    @Deprecated
    public static final String PAGE_HOLDER = "%page%";
    @Deprecated
    public static final String TARGET_PAGE_HOLDER = "%target_page%";
    @Deprecated
    public static final String AMOUNT_HOLDER = "%amount%";

    private static final YMLConfig guiConfig = Quests.get().getConfig("guiconfig.yml");
    private static final ItemStack defaultItem = new ItemBuilder(Material.BARRIER).setGuiProperty().build();

    public static void reload() {

        guiConfig.reload();
        Generic.reload();
        PlayerQuests.reload();
    }

    private static @Nullable List<String> getStringList(@NotNull String path) {
        return guiConfig.getStringList(path, null, false);
        /*
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;*/
    }

    private static String getString(@NotNull String path) {
        return Utils.fixString(guiConfig.getString(path, null),null,true);
         // guiConfig.getNavigator().getString(path, null, true);
    }

    private static ItemStack getItem(String path) {
        Object value = guiConfig.get(path, new ItemStack(defaultItem));
        if (value instanceof ItemStack)
            return (ItemStack) value;
        if (value instanceof String) {
            String[] infos = ((String) value).split(":");
            try {
                ItemStack item = new ItemStack(Material.valueOf(infos[0].toUpperCase()));
                if (infos.length == 1)
                    return item;
                ItemMeta meta = item.getItemMeta();
                for (int i = 1; i < infos.length; i++) {
                    if (infos[i].startsWith("amount-"))
                        item.setAmount(Integer.parseInt(infos[i].replace("amount-", "")));
                    else if (infos[i].startsWith("damage-"))
                        ((Damageable) meta).setDamage(Integer.parseInt(infos[i].replace("damage-", "")));
                    else if (infos[i].equals("unbreakable"))
                        meta.setUnbreakable(true);
                    else if (infos[i].equals("hideall"))
                        meta.addItemFlags(ItemFlag.values());
                }
                item.setItemMeta(meta);
                return item;
            } catch (Exception e) {
                new IllegalArgumentException("Attemping to read ItemStack failed: unable to convert value for '" + value + "' returning default").printStackTrace();
                return new ItemStack(defaultItem);
            }

        }
        new IllegalArgumentException("Attemping to read ItemStack failed: unable to convert value for '" + value + "' returning default").printStackTrace();
        return new ItemStack(defaultItem);
        //return guiConfig.getNavigator().getItemStack(path, new ItemStack(defaultItem));
    }

    public static class Generic {
        private static final String BASE_PATH = "generic.";
        public static List<String> AMOUNT_SELECTOR_ADD;
        public static List<String> AMOUNT_SELECTOR_REMOVE;
        public static List<String> AMOUNT_SELECTOR_SHOW;
        public static List<String> NEXT_PAGE;
        public static ItemStack NEXT_PAGE_ITEM;
        public static List<String> PREVIUS_PAGE;
        public static ItemStack PREVIUS_PAGE_ITEM;
        public static List<String> BACK_INVENTORY;
        public static ItemStack BACK_INVENTORY_ITEM;
        public static List<String> CLOSE_INVENTORY;
        public static ItemStack CLOSE_INVENTORY_ITEM;
        public static ItemStack EMPTY_BUTTON_ITEM;
        public static String COMMAND_FOR_PLAYERS_ONLY;
        public static String LACK_OF_PERMISSION;
        public static List<String> WHITELIST_DESCRIPTION;
        public static List<String> BLACKLIST_DESCRIPTION;
        public static List<String> NULL_ELEMENT;
        public static String NO_VALUE_SET;
        public static String INVALID_NUMBER;
        public static String INVALID_INPUT;
        public static String NOT_A_NUMBER;
        public static String NO_TEXT_CHANGES;
        public static String CONFIRM_CLICK_GUI_TITLE;
        public static List<String> CONFIRM_BUTTON_DESCRIPTION;
        public static List<String> UNCONFIRM_BUTTON_DESCRIPTION;
        public static ItemStack CONFIRM_BUTTON_ITEM;
        public static ItemStack UNCONFIRM_BUTTON_ITEM;

        private static List<String> getStringList(String path) {
            return GuiConfig.getStringList(BASE_PATH + path);
        }

        private static String getString(String path) {
            return GuiConfig.getString(BASE_PATH + path);
        }

        private static ItemStack getItem(String path) {
            return GuiConfig.getItem(BASE_PATH + path);
        }

        private static void reload() {

            NEXT_PAGE = getStringList("next_page");
            NEXT_PAGE_ITEM = getItem("next_page_item");

            PREVIUS_PAGE = getStringList("previus_page");
            PREVIUS_PAGE_ITEM = getItem("previus_page_item");

            EMPTY_BUTTON_ITEM = Utils.setDescription(getItem("empty_button_item"), null, null, false);

            BACK_INVENTORY = getStringList("back_inventory");
            BACK_INVENTORY_ITEM = getItem("back_inventory_item");

            CLOSE_INVENTORY = getStringList("close_inventory");
            CLOSE_INVENTORY_ITEM = getItem("close_inventory_item");

            COMMAND_FOR_PLAYERS_ONLY = getString("command_for_players_only");

            LACK_OF_PERMISSION = getString("lack_of_permission");

            AMOUNT_SELECTOR_SHOW = getStringList("amount_selector_show");
            AMOUNT_SELECTOR_ADD = getStringList("amount_selector_add");
            AMOUNT_SELECTOR_REMOVE = getStringList("amount_selector_remove");

            WHITELIST_DESCRIPTION = getStringList("whitelist_description");
            BLACKLIST_DESCRIPTION = getStringList("blacklist_description");
            NULL_ELEMENT = getStringList("null_element");

            NO_VALUE_SET = getString("no_value_set");

            INVALID_NUMBER = getString("invalid_number");

            NOT_A_NUMBER = getString("not_a_number");

            INVALID_INPUT = getString("invalid_input");

            NO_TEXT_CHANGES = getString("no_text_changes");
            CONFIRM_CLICK_GUI_TITLE = getString("confirm_click_gui_title");

            CONFIRM_BUTTON_DESCRIPTION = getStringList("confirm_button_description");
            UNCONFIRM_BUTTON_DESCRIPTION = getStringList("unconfirm_button_description");
            CONFIRM_BUTTON_ITEM = getItem("confirm_button_item");
            UNCONFIRM_BUTTON_ITEM = getItem("unconfirm_button_item");
        }

        public static ItemStack getConfirmButtonItem(Player p) {
            return Utils.setDescription(CONFIRM_BUTTON_ITEM, CONFIRM_BUTTON_DESCRIPTION, p, true);
        }

        public static ItemStack getUnconfirmButtonItem(Player p) {
            return Utils.setDescription(UNCONFIRM_BUTTON_ITEM, UNCONFIRM_BUTTON_DESCRIPTION, p, true);
        }

        public static String getQuestsMenuTitle(Player player) {
            String title = Utils.fixString(getString("quests_menu_title"), player, true);
            return title == null ? "" : title;
        }

        public static String getMissionsMenuTitle(Player player) {
            String title = Utils.fixString(getString("missions_menu_title"), player, true);
            return title == null ? "" : title;
        }
    }

    public static class PlayerQuests {
        private static final String BASE_PATH = "playerquests.";
        public static EnumMap<DisplayState, ItemStack> QUEST_ACTIVE_DISPLAY_FLAG = new EnumMap<>(
                DisplayState.class);
        public static EnumMap<DisplayState, ItemStack> QUEST_INACTIVE_DISPLAY_FLAG = new EnumMap<>(
                DisplayState.class);
        public static EnumMap<DisplayState, ItemStack> MISSION_ACTIVE_DISPLAY_FLAG = new EnumMap<>(
                DisplayState.class);
        public static EnumMap<DisplayState, ItemStack> MISSION_INACTIVE_DISPLAY_FLAG = new EnumMap<>(
                DisplayState.class);

        private static List<String> getStringList(String path) {
            return GuiConfig.getStringList(BASE_PATH + path);
        }

        @SuppressWarnings("unused")
        private static String getString(String path) {
            return GuiConfig.getString(BASE_PATH + path);
        }

        private static ItemStack getItem(String path) {
            return GuiConfig.getItem(BASE_PATH + path);
        }

        private static void reload() {
            for (DisplayState state : DisplayState.values()) {
                QUEST_ACTIVE_DISPLAY_FLAG.put(state, Utils.setDescription(
                        getItem("questshowflag.active." + state.toString().toLowerCase() + ".item"),
                        getStringList("questshowflag.active." + state.toString().toLowerCase() + ".desc"), null, true));
                QUEST_INACTIVE_DISPLAY_FLAG.put(state,
                        Utils.setDescription(
                                getItem("questshowflag.inactive." + state.toString().toLowerCase() + ".item"),
                                getStringList("questshowflag.inactive." + state.toString().toLowerCase() + ".desc"),
                                null, true));
                MISSION_ACTIVE_DISPLAY_FLAG.put(state,
                        Utils.setDescription(
                                getItem("missionshowflag.active." + state.toString().toLowerCase() + ".item"),
                                getStringList("missionshowflag.active." + state.toString().toLowerCase() + ".desc"),
                                null, true));
                MISSION_INACTIVE_DISPLAY_FLAG.put(state,
                        Utils.setDescription(
                                getItem("missionshowflag.inactive." + state.toString().toLowerCase() + ".item"),
                                getStringList("missionshowflag.inactive." + state.toString().toLowerCase() + ".desc"),
                                null, true));
            }
        }

        public static ItemStack getQuestDisplayFlagItem(DisplayState state, boolean active) {
            if (active)
                return QUEST_ACTIVE_DISPLAY_FLAG.get(state);
            return QUEST_INACTIVE_DISPLAY_FLAG.get(state);
        }

        public static ItemStack getMissionDisplayFlagItem(DisplayState state, boolean active) {
            if (active)
                return MISSION_ACTIVE_DISPLAY_FLAG.get(state);
            return MISSION_INACTIVE_DISPLAY_FLAG.get(state);
        }

    }

}
