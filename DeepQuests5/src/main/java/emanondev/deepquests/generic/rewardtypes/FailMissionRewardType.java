package emanondev.deepquests.generic.rewardtypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.TargetMissionData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DataUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FailMissionRewardType<T extends User<T>> extends ARewardType<T> {
    private final static String ID = "fail_mission";

    public FailMissionRewardType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return true;
    }

    @Override
    public Material getGuiMaterial() {
        return Material.RED_BANNER;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
                .build();
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Fail selected mission for user");
    }

    @Override
    public @NotNull FailMissionReward getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new FailMissionReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<T> reward) {
        if (!(reward instanceof FailMissionReward r))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:failed} &e{target}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{target}", DataUtils.getTargetHolder(r.getTargetMissionData()));
    }

    public class FailMissionReward extends AReward<T> {
        private final TargetMissionData<T, FailMissionReward> missionData;

        public FailMissionReward(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, FailMissionRewardType.this, section);
            missionData = new TargetMissionData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_TARGET_MISSION));
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(missionData.getInfo());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        public TargetMissionData<T, FailMissionReward> getTargetMissionData() {
            return missionData;
        }

        @Override
        public void apply(@NotNull T qPlayer, int amount) {
            if (amount <= 0)
                return;
            try {
                Mission<T> mission = missionData.getMission();
                if (mission == null)
                    new NullPointerException("Data missing or not setted still on reward " + this.getID())
                            .printStackTrace();
                qPlayer.failMission(mission);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, getTargetMissionData().getMissionSelectorButton(this));
            }
        }

    }

}