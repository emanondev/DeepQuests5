package emanondev.deepquests.gui.button;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.MapGui;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class StringListEditorButton extends AButton {
    private final ItemStack buttonItem;
    private final String subGuiTitle;

    private ArrayList<String> getSafeStringList() {
        List<String> rawList = getCurrentList();
        ArrayList<String> list;
        if (rawList == null)
            list = new ArrayList<>();
        else {
            list = new ArrayList<>(rawList);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, Utils.revertColors(list.get(i)));
            }
        }
        return list;
    }

    public StringListEditorButton(String subGuiTitle, ItemStack item, Gui parent) {
        super(parent);
        this.buttonItem = item;
        this.subGuiTitle = subGuiTitle;
        update();
    }

    public boolean update() {
        ArrayList<String> desc = new ArrayList<>();
        List<String> tmp = getButtonDescription();

        if (tmp != null && !tmp.isEmpty())
            desc.addAll(tmp);
        else {
            tmp = getSafeStringList();
            if (tmp.isEmpty())
                desc.add(GuiConfig.Generic.NO_VALUE_SET);
            else
                desc.addAll(tmp);
        }

        Utils.updateDescription(buttonItem, desc, null, true);
        return true;
    }

    public abstract List<String> getButtonDescription();

    public abstract List<String> getCurrentList();

    @Override
    public ItemStack getItem() {
        return buttonItem;
    }

    public abstract boolean onStringListChange(List<String> list);

    @Override
    public void onClick(Player clicker, ClickType click) {
        clicker.openInventory(new StringListEditorGui(clicker).getInventory());
    }

    private class StringListEditorGui extends MapGui {
        public StringListEditorGui(Player p) {
            super(subGuiTitle, 6, p, StringListEditorButton.this.getGui());
            this.putButton(11, new NoColorPreviewButton());
            this.putButton(29, new PreviewButton());
            this.putButton(15, new AddLineButton());
            this.putButton(24, new SetLineButton());
            this.putButton(33, new RemoveLineButton());
            this.putButton(53, new BackButton(this));
        }

        private class NoColorPreviewButton extends AButton {
            private final ItemStack item = new ItemBuilder(Material.IRON_BLOCK).setGuiProperty().build();

            public NoColorPreviewButton() {
                super(StringListEditorGui.this);
                update();
            }

            @Override
            public ItemStack getItem() {
                return item;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
            }

            public boolean update() {
                ArrayList<String> desc = getSafeStringList();
                if (desc.isEmpty())
                    desc.add(GuiConfig.Generic.NO_VALUE_SET);
                for (int i = 0; i < desc.size(); i++)
                    desc.set(i, ChatColor.WHITE + desc.get(i));
                Utils.updateDescription(item, desc, null, false);
                return true;
            }
        }

        private class PreviewButton extends AButton {
            private final ItemStack item = new ItemBuilder(Material.DIAMOND_BLOCK).setGuiProperty().build();

            public PreviewButton() {
                super(StringListEditorGui.this);
                update();
            }

            @Override
            public ItemStack getItem() {
                return item;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
            }

            public boolean update() {
                ArrayList<String> desc = getSafeStringList();
                if (desc.isEmpty())
                    desc.add(GuiConfig.Generic.NO_VALUE_SET);
                for (int i = 0; i < desc.size(); i++)
                    desc.set(i, ChatColor.WHITE + desc.get(i));
                Utils.updateDescription(item, desc, getTargetPlayer(), true);
                return true;
            }
        }

        private class AddLineButton extends TextEditorButton {

            public AddLineButton() {
                super(new ItemBuilder(Material.LIME_WOOL).setGuiProperty().build(), StringListEditorGui.this);
                Utils.updateDescription(getItem(), getButtonDescription(), getTargetPlayer(), true);
            }

            @Override
            public void onReicevedText(String text) {
                ArrayList<String> list = getSafeStringList();
                if (text == null)
                    text = "";
                list.add(text);
                if (onStringListChange(list))
                    getGui().updateInventory();
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                this.requestText(clicker, addLineChatText);
            }

            @Override
            public List<String> getButtonDescription() {
                ArrayList<String> desc = new ArrayList<>();
                desc.add("&a&lAdd &6&la New Line");
                Utils.updateDescription(getItem(), desc, getTargetPlayer(), true);
                return desc;
            }
        }

        private class RemoveLineButton extends TextEditorButton {

            public RemoveLineButton() {
                super(new ItemBuilder(Material.RED_WOOL).setGuiProperty().build(), StringListEditorGui.this);
                Utils.updateDescription(getItem(), getButtonDescription(), getTargetPlayer(), true);
            }

            @Override
            public void onReicevedText(String text) {
                if (text == null)
                    return;
                ArrayList<String> list;
                try {
                    int line = Integer.parseInt(text) - 1;
                    list = getSafeStringList();
                    list.remove(line);
                } catch (NumberFormatException e) {
                    getTargetPlayer().sendMessage(GuiConfig.Generic.NOT_A_NUMBER);
                    return;
                } catch (IndexOutOfBoundsException e) {
                    getTargetPlayer().sendMessage(GuiConfig.Generic.INVALID_NUMBER);
                    return;
                }
                onStringListChange(list);
                getGui().updateInventory();
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                ComponentBuilder comp = new ComponentBuilder(ChatColor.GOLD + "****************************\n");
                ArrayList<String> list = getSafeStringList();
                for (int i = 0; i < list.size(); i++)
                    comp.append(ChatColor.GOLD + "  Click here to remove line " + (i + 1) + "  \n")
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    "/DQText " + (i + 1)))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(Utils.fixString(list.get(i), null, true) + "\n" + ChatColor.RED
                                            + "Warning: deleting can't be undone").create()));
                comp.append("").append(ChatColor.GOLD + "    Click here to go back    \n")
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/DQText"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(ChatColor.GOLD + "Reopen the Gui").create()))
                        .append(ChatColor.GOLD + "****************************");
                this.requestText(clicker, comp.create());
            }

            @Override
            public List<String> getButtonDescription() {
                ArrayList<String> desc = new ArrayList<>();
                desc.add("&c&lRemove a &6&lLine");
                return desc;
            }
        }

        private class SetLineButton extends TextEditorButton {

            public SetLineButton() {
                super(new ItemBuilder(Material.BLUE_WOOL).setGuiProperty().build(), StringListEditorGui.this);
                Utils.updateDescription(getItem(), getButtonDescription(), getTargetPlayer(), true);
            }

            @Override
            public void onReicevedText(String text) {
                if (text == null)
                    return;
                ArrayList<String> list;
                try {
                    String rawNumber = text.split(" ")[0];
                    text = text.replaceFirst(rawNumber, "");
                    if (text.startsWith(" "))
                        text = text.replaceFirst(" ", "");
                    int line = Integer.parseInt(rawNumber) - 1;
                    list = getSafeStringList();
                    if (list.get(line) != null && list.get(line).equals(text)) {
                        getTargetPlayer().sendMessage(GuiConfig.Generic.NO_TEXT_CHANGES);
                        return;
                    }
                    list.set(line, text);
                } catch (NumberFormatException e) {
                    getTargetPlayer().sendMessage(GuiConfig.Generic.NOT_A_NUMBER);
                    return;
                } catch (IndexOutOfBoundsException e) {
                    getTargetPlayer().sendMessage(GuiConfig.Generic.INVALID_NUMBER);
                    return;
                } catch (Exception e) {
                    getTargetPlayer().sendMessage(GuiConfig.Generic.INVALID_INPUT);
                    return;
                }
                onStringListChange(list);
                getGui().updateInventory();
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                ComponentBuilder comp = new ComponentBuilder(ChatColor.GOLD + "****************************\n");
                ArrayList<String> list = getSafeStringList();
                for (int i = 0; i < list.size(); i++)
                    comp.append(ChatColor.GOLD + "  Click here to set line " + (i + 1) + "  \n")
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    "/DQText " + (i + 1) + " " + list.get(i)))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(Utils.fixString(list.get(i), null, true) + "\n" + ChatColor.RED
                                            + "Warning: this action will replace previous line").create()));
                comp.append("").append(ChatColor.GOLD + "    Click here to go back    \n")
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/DQText"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(ChatColor.GOLD + "Reopen the Gui").create()))
                        .append(ChatColor.GOLD + "****************************");
                this.requestText(clicker, comp.create());
            }

            @Override
            public List<String> getButtonDescription() {
                ArrayList<String> desc = new ArrayList<>();
                desc.add("&9&lReplace &6&lLine");
                return desc;
            }
        }
    }

    private final static BaseComponent[] addLineChatText = new ComponentBuilder(
            ChatColor.GOLD + "****************************\n" + ChatColor.GOLD + "   Click Me to add a new Line   \n"
                    + ChatColor.GOLD + "****************************")
            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dqtext "))
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ChatColor.GOLD + "Write the text to add on the new line\n"
                            + ChatColor.GOLD + "Tip: use '&' for chat formats" + ChatColor.YELLOW
                            + "/dqtext <new line>").create()))
            .create();
}