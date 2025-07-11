package emanondev.deepquests.generic.rewardtypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DataUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class QuestsPointsRewardType<T extends User<T>> extends ARewardType<T> {
    private final static String ID = "quests_points";

    public QuestsPointsRewardType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return false;
    }

    @Override
    public Material getGuiMaterial() {
        return Material.GOLD_NUGGET;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7give missions points");
    }

    @Override
    public @NotNull QuestsPointsReward getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new QuestsPointsReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<T> reward) {
        if (!(reward instanceof QuestsPointsReward r))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:obtained} &e{amount}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
    }

    public class QuestsPointsReward extends AReward<T> {
        private final AmountData<T, QuestsPointsReward> amountData;

        public QuestsPointsReward(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, QuestsPointsRewardType.this, section);
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_AMOUNT));
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add("&9Points: &e%amount%");
            return info;
        }

        public AmountData<T, QuestsPointsReward> getAmountData() {
            return amountData;
        }

        @Override
        public void apply(@NotNull T qPlayer, int amount) {
            if (amount <= 0)
                return;
            try {
                qPlayer.setPoints(qPlayer.getPoints() + amount * amountData.getAmount());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27,
                        amountData.getAmountEditorButton("&9Points Selector",
                                Arrays.asList("&6Points Selector", "&9Points: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}