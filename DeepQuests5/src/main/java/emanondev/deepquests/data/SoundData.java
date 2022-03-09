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
                } catch (Exception e) {

                }
            if (args.length >= 3)
                try {
                    Material mat = Material.valueOf(args[1] + "_" + args[2]);
                    if (mat.isItem())
                        return mat;
                } catch (Exception e) {

                }
            try {
                Material mat = Material.valueOf(args[1]);
                if (mat.isItem())
                    return mat;
            } catch (Exception e) {

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
                } catch (Exception e) {

                }
            try {
                Material mat = Material.valueOf(args[1] + "_SPAWN_EGG");
                if (mat.isItem())
                    return mat;
            } catch (Exception e) {

            }
            if (args.length >= 3)
                try {
                    Material mat = Material.valueOf(args[1] + "_" + args[2]);
                    if (mat.isItem())
                        return mat;
                } catch (Exception e) {

                }
            try {
                Material mat = Material.valueOf(args[1]);
                if (mat.isItem())
                    return mat;
            } catch (Exception e) {

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
                } catch (Exception e) {

                }
            try {
                Material mat = Material.valueOf(args[1]);
                if (mat.isItem())
                    return mat;
            } catch (Exception e) {

            }
            return DEF_SOUND_GUI_MATERIAL;
        }
        if (sound.name().contains("MUSIC_")) {
            try {
                Material mat = Material.valueOf(sound.name());
                if (mat.isItem())
                    return mat;
            } catch (Exception e) {

            }
            return Material.JUKEBOX;
        }
        return DEF_SOUND_GUI_MATERIAL;
        /*
         * switch (sound) { case BLOCK_BUBBLE_COLUMN_BUBBLE_POP: case
         * BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT: case BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE:
         * case BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT: case
         * BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE: break; case ENCHANT_THORNS_HIT: return
         * Material.DIAMOND_CHESTPLATE; case ENTITY_BOAT_PADDLE_LAND: case
         * ENTITY_BOAT_PADDLE_WATER: return Material.OAK_BOAT; case
         * ENTITY_EXPERIENCE_ORB_PICKUP: return Material.EXPERIENCE_BOTTLE; case
         * ENTITY_FISHING_BOBBER_RETRIEVE: case ENTITY_FISHING_BOBBER_SPLASH: case
         * ENTITY_FISHING_BOBBER_THROW: case ENTITY_FISH_SWIM: return
         * Material.FISHING_ROD; case ENTITY_GENERIC_BIG_FALL: case ENTITY_GENERIC_BURN:
         * case ENTITY_GENERIC_DEATH: case ENTITY_GENERIC_DRINK: case
         * ENTITY_GENERIC_EAT: case ENTITY_GENERIC_EXPLODE: case
         * ENTITY_GENERIC_EXTINGUISH_FIRE: case ENTITY_GENERIC_HURT: case
         * ENTITY_GENERIC_SMALL_FALL: case ENTITY_GENERIC_SPLASH: case
         * ENTITY_GENERIC_SWIM: break; case ENTITY_HOSTILE_BIG_FALL: case
         * ENTITY_HOSTILE_DEATH: case ENTITY_HOSTILE_HURT: case
         * ENTITY_HOSTILE_SMALL_FALL: case ENTITY_HOSTILE_SPLASH: case
         * ENTITY_HOSTILE_SWIM: break; case ENTITY_ILLUSIONER_AMBIENT: case
         * ENTITY_ILLUSIONER_CAST_SPELL: case ENTITY_ILLUSIONER_DEATH: case
         * ENTITY_ILLUSIONER_HURT: case ENTITY_ILLUSIONER_MIRROR_MOVE: case
         * ENTITY_ILLUSIONER_PREPARE_BLINDNESS: case ENTITY_ILLUSIONER_PREPARE_MIRROR:
         * return Material.EVOKER_SPAWN_EGG; case ENTITY_ITEM_BREAK: return
         * Material.ITEM_FRAME; case ENTITY_ITEM_PICKUP: break; case
         * ENTITY_LEASH_KNOT_BREAK: case ENTITY_LEASH_KNOT_PLACE: return Material.LEAD;
         * case ENTITY_LIGHTNING_BOLT_IMPACT: case ENTITY_LIGHTNING_BOLT_THUNDER: case
         * ENTITY_LINGERING_POTION_THROW: break; case ITEM_ARMOR_EQUIP_CHAIN: case
         * ITEM_ARMOR_EQUIP_DIAMOND: case ITEM_ARMOR_EQUIP_ELYTRA: case
         * ITEM_ARMOR_EQUIP_GENERIC: case ITEM_ARMOR_EQUIP_GOLD: case
         * ITEM_ARMOR_EQUIP_IRON: case ITEM_ARMOR_EQUIP_LEATHER: case
         * ITEM_ARMOR_EQUIP_TURTLE: return Material.LEATHER_CHESTPLATE; case
         * ITEM_AXE_STRIP: return Material.WOODEN_AXE; case ITEM_BOTTLE_EMPTY: case
         * ITEM_BOTTLE_FILL: case ITEM_BOTTLE_FILL_DRAGONBREATH: return
         * Material.GLASS_BOTTLE; case ITEM_FIRECHARGE_USE: return Material.FIRE_CHARGE;
         * case ITEM_FLINTANDSTEEL_USE: return Material.FLINT_AND_STEEL; case
         * ITEM_HOE_TILL: return Material.IRON_HOE; case ITEM_SHOVEL_FLATTEN: return
         * Material.IRON_SHOVEL; case ITEM_TOTEM_USE: return Material.TOTEM_OF_UNDYING;
         * }
         */
    }

    public Button getVolumeEditorButton(Gui gui) {
        return new VolumeEditor(gui);
    }

    public Button getPitchEditorButton(Gui gui) {
        return new PitchEditor(gui);
    }
}