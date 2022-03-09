package emanondev.deepquests.generic.rewardtypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.SoundData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundRewardType<T extends User<T>> extends ARewardType<T> {
    public SoundRewardType(QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return true;
    }

    private final static String ID = "sound";

    @Override
    public Material getGuiMaterial() {
        return Material.NOTE_BLOCK;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7play sound for players");
    }

    @Override
    public SoundReward getInstance(int id, QuestManager<T> manager, YMLSection section) {
        return new SoundReward(id, manager, section);
    }

    public class SoundReward extends AReward<T> {
        private final SoundData<T, SoundReward> soundData;

        public SoundReward(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, SoundRewardType.this, section);
            soundData = new SoundData<>(this, getConfig().loadSection(Paths.REWARD_INFO_SOUND));
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(soundData.getInfo());
            return info;
        }

        public SoundData<T, SoundReward> getSoundData() {
            return soundData;
        }

        @Override
        public void apply(T user, int amount) {
            if (amount <= 0)
                return;
            try {
                if (soundData.getSound() == null)
                    throw new NullPointerException("sound not setted on reward " + this.getID());
                for (Player p : user.getPlayers()) {
                    if (p == null)
                        return;
                    p.playSound(p.getLocation(), soundData.getSound(), soundData.getVolume(), soundData.getPitch());
                }
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
                this.putButton(27, soundData.getSoundEditorButton(this));
                this.putButton(28, soundData.getVolumeEditorButton(this));
                this.putButton(29, soundData.getPitchEditorButton(this));
            }
        }
    }

    @Override
    public String getDefaultFeedback(Reward<T> reward) {
        if (!(reward instanceof SoundReward r))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:playing} &e{sound}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{sound}",
                r.getSoundData().getSound() == null ? "?" : r.getSoundData().getSound().toString().toLowerCase());
    }
}
