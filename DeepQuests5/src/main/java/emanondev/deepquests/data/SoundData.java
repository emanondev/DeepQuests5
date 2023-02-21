package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.DoubleAmountEditorButton;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SoundData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private Sound sound;
    private float volume;
    private float pitch;

    public SoundData(E parent, YMLSection section) {
        super(parent, section);
        volume = Math.min(Math.max((float) getConfig().getDouble(Paths.DATA_SOUND_VOLUME, 1D), 0.05F), 1F);
        pitch = Math.min(Math.max((float) getConfig().getDouble(Paths.DATA_SOUND_PITCH, 1D), 0.05F), 20F);
        sound = getConfig().getEnum(Paths.DATA_SOUND_NAME, null, Sound.class);
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        if (getSound() == null)
            info.add("&9Sound: &cSound not setted");
        else {
            info.add("&9Sound: &e" + getSound().toString());
            info.add("&9Volume: &e" + getVolume());
            info.add("&9Pitch: &e" + getPitch());
        }
        return info;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        if (this.sound == null && sound == null)
            return;
        if (this.sound != null && this.sound.equals(sound))
            return;
        this.sound = sound;
        getConfig().setEnumAsString(Paths.DATA_SOUND_NAME, sound);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        volume = Math.min(Math.max(volume, 0.05F), 1F);
        if (this.volume == volume)
            return;
        this.volume = volume;
        getConfig().set(Paths.DATA_SOUND_VOLUME, volume);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        pitch = Math.min(Math.max(pitch, 0.05F), 20F);
        if (this.pitch == pitch)
            return;
        this.pitch = pitch;
        getConfig().set(Paths.DATA_SOUND_PITCH, pitch);
    }

    public SoundEditorButton getSoundEditorButton(Gui parent) {
        return new SoundEditorButton(parent);
    }

    private class PitchEditor extends DoubleAmountEditorButton {

        public PitchEditor(Gui parent) {
            super("&9Pitch Editor", new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build(), parent, 0.01D, 0.1D,
                    1D);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6&lPitch Editor Button");
            desc.add("&6Click to edit");
            desc.add("&6Pitch: &e" + pitch);
            return desc;
        }

        @Override
        public double getCurrentAmount() {
            return pitch;
        }

        @Override
        public boolean onAmountChangeRequest(double i) {
            setPitch((float) i);
            return true;
        }
    }

    private class VolumeEditor extends DoubleAmountEditorButton {

        public VolumeEditor(Gui parent) {
            super("&9Volume Editor", new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build(), parent, 0.01D,
                    0.05D, 0.25D);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6&lVolume Editor Button");
            desc.add("&6Click to edit");
            desc.add("&6Volume: &e" + volume);
            return desc;
        }

        @Override
        public double getCurrentAmount() {
            return volume;
        }

        @Override
        public boolean onAmountChangeRequest(double i) {
            setVolume((float) i);
            return true;
        }
    }

    private class SoundEditorButton extends ElementSelectorButton<Sound> {

        public SoundEditorButton(Gui parent) {
            super("&9Select a sound", new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build(), parent, true, true,
                    false);
        }

        @Override
        public Collection<Sound> getPossibleValues() {
            return Arrays.asList(Sound.values());
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6Sound Editor");
            if (sound != null)
                desc.add("&9Sound: &e" + sound);
            else
                desc.add("&9Sound: &cnot set");
            return desc;
        }

        @Override
        public List<String> getElementDescription(Sound element) {
            return List.of("&9Type: &e" + element.toString());
        }

        @Override
        public ItemStack getElementItem(Sound element) {
            return new ItemBuilder(getMaterial(element)).setGuiProperty().build();
        }

        @Override
        public void onElementSelectRequest(Sound element) {
            setSound(element);
            getGui().updateInventory();
            getGui().getTargetPlayer().openInventory(getGui().getInventory());
        }

    }

    private static final Material DEF_SOUND_GUI_MATERIAL = Material.BARRIER;

    private static Material getMaterial(Sound sound) {
        if (sound.name().contains("BLOCK_")) {
            String[] args = sound.name().split("_");
            if (args[1].equals("WOODEN"))
                args[1] = "OAK";
            if (args.length >= 4)
                try {
                    Material mat = Material.valueOf(args[1] + "_" + args[2] + "_" + args[3]);
                    if (mat.isItem())
                        return mat;
                } catch (Exception ignored) {

                }
            if (args.length >= 3)
                try {
                    Material mat = Material.valueOf(args[1] + "_" + args[2]);
                    if (mat.isItem())
                        return mat;
                } catch (Exception ignored) {

                }
            try {
                Material mat = Material.valueOf(args[1]);
                if (mat.isItem())
                    return mat;
            } catch (Exception ignored) {

            }
            if (sound.name().contains("LAVA"))
                return Material.LAVA_BUCKET;
            if (sound.name().contains("WATER"))
                return Material.WATER_BUCKET;
            if (sound.name().contains("METAL_PRESSURE_PLATE"))
                return Material.HEAVY_WEIGHTED_PRESSURE_PLATE;
            if (sound.name().contains("SNOW"))
                return Material.SNOW_BLOCK;
            if (sound.name().contains("PORTAL"))
                return Material.END_PORTAL_FRAME;
            if (sound.name().contains("GATEWAY"))
                return Material.END_PORTAL_FRAME;
            if (sound.name().contains("TRIPWIRE"))
                return Material.TRIPWIRE_HOOK;
            if (sound.name().contains("WET_GRASS"))
                return Material.GRASS;
            if (sound.name().contains("WOOD"))
                return Material.OAK_WOOD;
            if (sound.name().contains("WOOL"))
                return Material.WHITE_WOOL;

            return DEF_SOUND_GUI_MATERIAL;
        }
        if (sound.name().contains("ENTITY_")) {
            String[] args = sound.name().split("_");
            if (args.length >= 3)
                try {
                    Material mat = Material.valueOf(args[1] + "_" + args[2] + "_SPAWN_EGG");
                    if (mat.isItem())
                        return mat;
                } catch (Exception ignored) {

                }
            try {
                Material mat = Material.valueOf(args[1] + "_SPAWN_EGG");
                if (mat.isItem())
                    return mat;
            } catch (Exception ignored) {

            }
            if (args.length >= 3)
                try {
                    Material mat = Material.valueOf(args[1] + "_" + args[2]);
                    if (mat.isItem())
                        return mat;
                } catch (Exception ignored) {

                }
            try {
                Material mat = Material.valueOf(args[1]);
                if (mat.isItem())
                    return mat;
            } catch (Exception ignored) {

            }
            if (sound.name().contains("DRAGON"))
                return Material.DRAGON_EGG;

            if (sound.name().contains("PLAYER"))
                return Material.PLAYER_HEAD;

            if (sound.name().contains("WITHER"))
                return Material.WITHER_SKELETON_SKULL;
            if (sound.name().contains("GOLEM"))
                return Material.JACK_O_LANTERN;
            return DEF_SOUND_GUI_MATERIAL;
        }
        if (sound.name().contains("ITEM_")) {
            String[] args = sound.name().split("_");
            if (args.length >= 3)
                try {
                    Material mat = Material.valueOf(args[1] + "_" + args[2]);
                    if (mat.isItem())
                        return mat;
                } catch (Exception ignored) {

                }
            try {
                Material mat = Material.valueOf(args[1]);
                if (mat.isItem())
                    return mat;
            } catch (Exception ignored) {

            }
            return DEF_SOUND_GUI_MATERIAL;
        }
        if (sound.name().contains("MUSIC_")) {
            try {
                Material mat = Material.valueOf(sound.name());
                if (mat.isItem())
                    return mat;
            } catch (Exception ignored) {

            }
            return Material.JUKEBOX;
        }
        return DEF_SOUND_GUI_MATERIAL;
    }

    public Button getVolumeEditorButton(Gui gui) {
        return new VolumeEditor(gui);
    }

    public Button getPitchEditorButton(Gui gui) {
        return new PitchEditor(gui);
    }
}