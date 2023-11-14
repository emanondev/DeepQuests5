package emanondev.deepquests.player.rewardtypes;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsInventory;
import emanondev.core.UtilsInventory.ExcessManage;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.ItemStackData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.player.QuestPlayer;
import emanondev.deepquests.utils.DataUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ItemStackRewardType extends ARewardType<QuestPlayer> {
    private final static String ID = "itemstack";

    public ItemStackRewardType(QuestManager<QuestPlayer> manager) {
        super(ID, manager);
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return false;
    }

    @Override
    public Material getGuiMaterial() {
        return Material.DIAMOND;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("&7Reward the player with an Item");
    }

    @Override
    public @NotNull ItemStackReward getInstance(int id, @NotNull QuestManager<QuestPlayer> manager, @NotNull YMLSection section) {
        return new ItemStackReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<QuestPlayer> reward) {
        if (!(reward instanceof ItemStackRewardType.ItemStackReward))
            return null;
        ItemStackReward r = (ItemStackReward) reward;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:obtained} &e{item} &ax &e{amount}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{item}", DataUtils.getItemsHolder(r.getItemStackData()))
                .replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
    }

    public class ItemStackReward extends AReward<QuestPlayer> {
        private ItemStackData<QuestPlayer, ItemStackReward> stackData;
        private AmountData<QuestPlayer, ItemStackReward> amountData;

        public ItemStackReward(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, ItemStackRewardType.this, section);
            stackData = new ItemStackData<QuestPlayer, ItemStackReward>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_ITEMSTACK));
            amountData = new AmountData<QuestPlayer, ItemStackReward>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add("  &9Amount: &e" + amountData.getAmount());
            info.addAll(stackData.getInfo());
            return info;
        }

        @Override
        public void apply(@NotNull QuestPlayer qPlayer, int amount) {
            if (amount <= 0 || stackData.getItem() == null || amountData.getAmount() <= 0)
                return;
            UtilsInventory.giveAmount(qPlayer.getPlayer(), stackData.getItem(), amountData.getAmount() * amount,
                    ExcessManage.DROP_EXCESS);
        }

        public AmountData<QuestPlayer, ItemStackReward> getAmountData() {
            return amountData;
        }

        public ItemStackData<QuestPlayer, ItemStackReward> getItemStackData() {
            return stackData;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                stackData.setupButtons(this, 27);
                this.putButton(28,
                        amountData.getAmountEditorButton("&9Amount Selector",
                                Arrays.asList("&6Amount Selector", "&9Amount: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}