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

import java.util.List;

public class StartMissionRewardType<T extends User<T>> extends ARewardType<T> {
    public StartMissionRewardType(QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return true;
    }

    private final static String ID = "start_mission";

    @Override
    public Material getGuiMaterial() {
        return Material.BLUE_BANNER;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
                .build();
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7start selected mission for player");
    }

    @Override
    public StartMissionReward getInstance(int id, QuestManager<T> manager, YMLSection section) {
        return new StartMissionReward(id, manager, section);
    }

    public class StartMissionReward extends AReward<T> {
        private final TargetMissionData<T, StartMissionReward> missionData;

        public StartMissionReward(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, StartMissionRewardType.this, section);
            missionData = new TargetMissionData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_TARGET_MISSION));
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(missionData.getInfo());
            return info;
        }

        public TargetMissionData<T, StartMissionReward> getTargetMissionData() {
            return missionData;
        }

        @Override
        public void apply(T user, int amount) {
            if (amount <= 0)
                return;
            try {
                Mission<T> mission = missionData.getMission();
                if (mission == null)
                    new NullPointerException("Data missing or not setted still on reward " + this.getID())
                            .printStackTrace();
                user.startMission(mission, null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, missionData.getMissionSelectorButton(this));
            }
        }
    }

    @Override
    public String getDefaultFeedback(Reward<T> reward) {
        if (!(reward instanceof StartMissionReward r))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:started} &e{target}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{target}", DataUtils.getTargetHolder(r.getTargetMissionData()));
    }

}