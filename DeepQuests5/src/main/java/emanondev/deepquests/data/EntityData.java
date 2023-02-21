package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.SpawnReasonTracker;
import emanondev.core.UtilsString;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.button.StaticFlagButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EntityData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

    private final EnumSet<EntityType> entityType = EnumSet.noneOf(EntityType.class);
    private final EnumSet<SpawnReason> spawnReasons = EnumSet.noneOf(SpawnReason.class);
    private boolean spawnReasonsIsWhitelist;
    private boolean entityTypeIsWhitelist;

    private boolean ignoreNPC;
    private String entityName;

    public EntityData(E parent, YMLSection section) {
        super(parent, section);
        entityType.addAll(getConfig().loadEnumSet(Paths.DATA_ENTITYTYPE_LIST, EnumSet.noneOf(EntityType.class),
                EntityType.class));
        spawnReasons.addAll(getConfig().loadEnumSet(Paths.DATA_ENTITY_SPAWNREASON_LIST,
                EnumSet.noneOf(SpawnReason.class), SpawnReason.class));

        entityTypeIsWhitelist = getConfig().getBoolean(Paths.DATA_ENTITYTYPE_IS_WHITELIST, true);
        spawnReasonsIsWhitelist = getConfig().getBoolean(Paths.DATA_ENTITY_SPAWNREASON_IS_WHITELIST, true);
        ignoreNPC = getConfig().getBoolean(Paths.DATA_IGNORE_NPC, true);
        entityName = UtilsString.fix(getConfig().getString(Paths.DATA_ENTITY_NAME, null), null, true);
    }

    public Set<EntityType> getTypes() {
        return Collections.unmodifiableSet(entityType);
    }

    public Set<SpawnReason> getSpawnReasons() {
        return Collections.unmodifiableSet(spawnReasons);
    }

    public boolean areTypesWhitelist() {
        return entityTypeIsWhitelist;
    }

    public boolean areSpawnReasonsWhitelist() {
        return spawnReasonsIsWhitelist;
    }

    public boolean ignoreNPC() {
        return ignoreNPC;
    }

    public boolean isValidEntity(Entity e) {
        if (!isValidEntityType(e.getType()))
            return false;
        if (entityType.contains(e.getType())) {
            if (!entityTypeIsWhitelist)
                return false;
        } else if (entityTypeIsWhitelist)
            return false;

        if (!isValidSpawnReason(SpawnReasonTracker.getSpawnReason(e)))
            return false;

        if (ignoreNPC && e.hasMetadata("NPC"))
            return false;

        if (entityName == null)
            return true;
        return entityName.equals(e.getName());
    }

    private boolean isValidEntityType(EntityType type) {
        if (entityType.isEmpty())
            return true;
        if (entityType.contains(type))
            return entityTypeIsWhitelist;
        else return !entityTypeIsWhitelist;
    }

    private boolean isValidSpawnReason(SpawnReason type) {
        if (spawnReasons.isEmpty())
            return true;
        if (spawnReasons.contains(type))
            return spawnReasonsIsWhitelist;
        else return !spawnReasonsIsWhitelist;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        if (entityType.isEmpty())
            info.add("&9Any EntityType is &aAllowed");
        else {
            if (areTypesWhitelist()) {
                info.add("&9Entity types Allowed:");
                for (EntityType type : entityType)
                    info.add("&9  - &a" + type.toString());
            } else {
                info.add("&9Entity types Unallowed:");
                for (EntityType type : entityType)
                    info.add("&9  - &c" + type.toString());
            }
        }
        if (!spawnReasons.isEmpty()) {
            if (areSpawnReasonsWhitelist()) {
                info.add("&9SpawnReasons Allowed:");
                for (SpawnReason type : spawnReasons)
                    info.add("&9  - &a" + type.toString());
            } else {
                info.add("&9SpawnReasons Unallowed:");
                for (SpawnReason type : spawnReasons)
                    info.add("&9  - &c" + type.toString());
            }
        }
        if (ignoreNPC())
            info.add("&9NPC are &cignored");
        else
            info.add("&9NPC are &acounted");
        if (getEntityName() != null)
            info.add("&9Entity name must be equal to '&r" + getEntityName() + "&9'");
        return info;
    }

    public void toggleEntityType(EntityType type) {
        if (!type.isAlive())
            return;
        if (entityType.contains(type))
            entityType.remove(type);
        else
            entityType.add(type);
        getConfig().setEnumsAsStringList(Paths.DATA_ENTITYTYPE_LIST, entityType);
    }

    public void toggleEntityTypeWhitelist() {
        entityTypeIsWhitelist = !entityTypeIsWhitelist;
        getConfig().set(Paths.DATA_ENTITYTYPE_IS_WHITELIST, entityTypeIsWhitelist);
    }

    public void toggleSpawnReason(SpawnReason type) {
        if (spawnReasons.contains(type))
            spawnReasons.remove(type);
        else
            spawnReasons.add(type);
        getConfig().setEnumsAsStringList(Paths.DATA_ENTITY_SPAWNREASON_LIST, spawnReasons);
    }

    public void toggleSpawnReasonWhitelist() {
        spawnReasonsIsWhitelist = !spawnReasonsIsWhitelist;
        getConfig().set(Paths.DATA_ENTITY_SPAWNREASON_IS_WHITELIST, spawnReasonsIsWhitelist);
    }

    public void toggleIgnoreNPC() {
        ignoreNPC = !ignoreNPC;
        getConfig().set(Paths.DATA_IGNORE_NPC, ignoreNPC);
    }

    public void setEntityName(String name) {
        this.entityName = name;
        getConfig().set(Paths.DATA_ENTITY_NAME, entityName);
    }

    public String getEntityName() {
        return entityName;
    }

    public Button getEntityTypeButton(Gui gui) {
        return new EntityTypeButton(gui);
    }

    public Button getSpawnReasonButton(Gui gui) {
        return new SpawnReasonButton(gui);
    }

    public Button getIgnoreNPCFlagButton(Gui gui) {
        return new IgnoreCitizenTargetsButton(gui);
    }

    // TODO entityName
    private class IgnoreCitizenTargetsButton extends StaticFlagButton {

        public IgnoreCitizenTargetsButton(Gui parent) {
            super(Utils.setDescription(new ItemBuilder(Material.PLAYER_HEAD).build(),
                            Arrays.asList("&6&lCitizen Npc Flag", "&6Click to toggle",
                                    "&7Now Citizen NPC &acount &7as valid Targets"),
                            null, true),
                    Utils.setDescription(
                            new ItemBuilder(Material.PLAYER_HEAD).build(), Arrays.asList("&6&lCitizen Npc Flag",
                                    "&6Click to toggle", "&7Now Citizen NPC &cwon't count &7as valid Targets"),
                            null, true),
                    parent);
        }

        @Override
        public boolean getCurrentValue() {
            return ignoreNPC();
        }

        @Override
        public boolean onValueChangeRequest(boolean value) {
            toggleIgnoreNPC();
            return true;
        }
    }

    private class SpawnReasonButton extends CollectionSelectorButton<SpawnReason> {

        public SpawnReasonButton(Gui parent) {
            super("&6SpawnReason Button", new ItemBuilder(Material.ZOMBIE_SPAWN_EGG).setGuiProperty().build(), parent,
                    true);
        }

        @Override
        public Collection<SpawnReason> getPossibleValues() {
            LinkedHashSet<SpawnReason> set = new LinkedHashSet<>();
            Collections.addAll(set, SpawnReason.values());
            return set;
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6SpawnReason Button");

            if (spawnReasons.isEmpty())
                list.add("&9Any SpawnReason is &aAllowed");
            else {
                if (areTypesWhitelist()) {
                    list.add("&9SpawnReasons Allowed:");
                    for (SpawnReason type : spawnReasons)
                        list.add("&9  - &a" + type.toString());
                } else {
                    list.add("&9SpawnReasons Unallowed:");
                    for (SpawnReason type : spawnReasons)
                        list.add("&9  - &c" + type.toString());
                }
            }
            list.add("");
            list.add("&7Click to edit");
            return list;
        }

        @Override
        public List<String> getElementDescription(SpawnReason type) {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6SpawnReason: '&e" + type.toString() + "&6'");
            if (isValidSpawnReason(type))
                desc.add("&7This type is &aAllowed");
            else
                desc.add("&7This type is &cUnallowed");
            desc.add("");
            desc.addAll(getDescription(type));
            return desc;
        }

        @Override
        public ItemStack getElementItem(SpawnReason element) {
            return getGuiItem(element);
        }

        @Override
        public boolean isValidContains(SpawnReason element) {
            return isValidSpawnReason(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return areTypesWhitelist();
        }

        @Override
        public boolean onToggleElementRequest(SpawnReason element) {
            toggleSpawnReason(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleSpawnReasonWhitelist();
            return true;
        }
    }

    private class EntityTypeButton extends CollectionSelectorButton<EntityType> {

        public EntityTypeButton(Gui parent) {
            super("&6EntityType Button", new ItemBuilder(Material.ZOMBIE_HEAD).setGuiProperty().build(), parent, true);
        }

        @Override
        public Collection<EntityType> getPossibleValues() {
            TreeSet<EntityType> set = new TreeSet<>((type1, type2) -> type1.name().compareToIgnoreCase(type2.name()));
            for (EntityType type : EntityType.values())
                if (type.isAlive())
                    set.add(type);
            return set;
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6EntityType Button");

            if (entityType.isEmpty())
                list.add("&9Any EntityType is &aAllowed");
            else {
                if (areTypesWhitelist()) {
                    list.add("&9Entity types Allowed:");
                    for (EntityType type : entityType)
                        list.add("&9  - &a" + type.toString());
                } else {
                    list.add("&9Entity types Unallowed:");
                    for (EntityType type : entityType)
                        list.add("&9  - &c" + type.toString());
                }
            }
            list.add("");
            list.add("&7Click to edit");
            return list;
        }

        @Override
        public List<String> getElementDescription(EntityType type) {

            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6Entity Type: '&e" + type.toString() + "&6'");
            if (isValidEntityType(type))
                desc.add("&7This type is &aAllowed");
            else
                desc.add("&7This type is &cUnallowed");
            desc.add("");
            desc.add("&9Translation Name: " + Translations.translate(type));
            return desc;
        }

        @Override
        public ItemStack getElementItem(EntityType element) {
            return getGuiItem(element);
        }

        @Override
        public boolean isValidContains(EntityType element) {
            return isValidEntityType(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return areTypesWhitelist();
        }

        @Override
        public boolean onToggleElementRequest(EntityType element) {
            toggleEntityType(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleEntityTypeWhitelist();
            return true;
        }
    }

    private ItemStack getGuiItem(EntityType element) {
        switch (element) {
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case HUSK:
            case DROWNED:
                return new ItemBuilder(Material.ROTTEN_FLESH).setGuiProperty().build();
            case WITCH:
                return new ItemBuilder(Material.GLASS_BOTTLE).setGuiProperty().build();
            case SPIDER:
            case CAVE_SPIDER:
                return new ItemBuilder(Material.SPIDER_EYE).setGuiProperty().build();
            case SLIME:
                return new ItemBuilder(Material.SLIME_BALL).setGuiProperty().build();
            case SKELETON:
                return new ItemBuilder(Material.BONE).setGuiProperty().build();
            case SILVERFISH:
                return new ItemBuilder(Material.STONE_BRICKS).setGuiProperty().build();
            case GIANT:
                return new ItemBuilder(Material.BONE_BLOCK).setGuiProperty().build();
            case ENDERMITE:
                return new ItemBuilder(Material.ENDER_PEARL).setGuiProperty().build();
            case ENDERMAN:
                return new ItemBuilder(Material.ENDER_PEARL).setGuiProperty().build();
            case CREEPER:
                return new ItemBuilder(Material.GUNPOWDER).setGuiProperty().build();
            case WOLF:
                return new ItemBuilder(Material.BONE).setGuiProperty().build();
            case VILLAGER:
                return new ItemBuilder(Material.EMERALD).setGuiProperty().build();
            case SNOWMAN:
                return new ItemBuilder(Material.SNOWBALL).setGuiProperty().build();
            case IRON_GOLEM:
                return new ItemBuilder(Material.IRON_BLOCK).setGuiProperty().build();
            case WITHER:
                return new ItemBuilder(Material.NETHER_STAR).setGuiProperty().build();
            case ENDER_DRAGON:
                return new ItemBuilder(Material.DRAGON_EGG).setGuiProperty().build();
            case SQUID:
                return new ItemBuilder(Material.INK_SAC).setGuiProperty().build();
            case GUARDIAN:
                return new ItemBuilder(Material.PRISMARINE_SHARD).setGuiProperty().build();
            case BAT:
                return new ItemBuilder(Material.STONE).setGuiProperty().build();
            case OCELOT:
                return new ItemBuilder(Material.RED_BED).setGuiProperty().build();
            case PIG:
                return new ItemBuilder(Material.PORKCHOP).setGuiProperty().build();
            case SHEEP:
                return new ItemBuilder(Material.WHITE_WOOL).setGuiProperty().build();
            case RABBIT:
                return new ItemBuilder(Material.RABBIT_FOOT).setGuiProperty().build();
            case MUSHROOM_COW:
                return new ItemBuilder(Material.RED_MUSHROOM).setGuiProperty().build();
            case COW:
                return new ItemBuilder(Material.MILK_BUCKET).setGuiProperty().build();
            case CHICKEN:
                return new ItemBuilder(Material.CHICKEN).setGuiProperty().build();
            case PLAYER:
                return new ItemBuilder(Material.PLAYER_HEAD).setGuiProperty().build();
            case BLAZE:
                return new ItemBuilder(Material.BLAZE_ROD).setGuiProperty().build();
            case ZOMBIFIED_PIGLIN:
                return new ItemBuilder(Material.GOLD_NUGGET).setGuiProperty().build();
            case MAGMA_CUBE:
                return new ItemBuilder(Material.MAGMA_CREAM).setGuiProperty().build();
            case GHAST:
                return new ItemBuilder(Material.GHAST_TEAR).setGuiProperty().build();
            case WITHER_SKELETON:
                return new ItemBuilder(Material.NETHER_BRICK).setGuiProperty().build();
            case HORSE:
                return new ItemBuilder(Material.SADDLE).setGuiProperty().build();
            case DONKEY:
                return new ItemBuilder(Material.SADDLE).setGuiProperty().build();
            case MULE:
                return new ItemBuilder(Material.SADDLE).setGuiProperty().build();
            case SKELETON_HORSE:
                return new ItemBuilder(Material.SADDLE).setGuiProperty().build();
            case ZOMBIE_HORSE:
                return new ItemBuilder(Material.SADDLE).setGuiProperty().build();
            case ELDER_GUARDIAN:
                return new ItemBuilder(Material.PRISMARINE_CRYSTALS).setGuiProperty().build();
            // 1.9
            case SHULKER:
                return new ItemBuilder(Material.PURPLE_SHULKER_BOX).setGuiProperty().build();
            // 1.10
            case POLAR_BEAR:
                return new ItemBuilder(Material.SNOW_BLOCK).setGuiProperty().build();
            case STRAY:
                return new ItemBuilder(Material.BONE).setGuiProperty().build();
            // 1.11
            case LLAMA:
                return new ItemBuilder(Material.ORANGE_CARPET).setGuiProperty().build();
            case EVOKER:
                return new ItemBuilder(Material.EVOKER_SPAWN_EGG).setGuiProperty().build();
            case VEX:
                return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
            case VINDICATOR:
                return new ItemBuilder(Material.IRON_AXE).setGuiProperty().build();
            // 1.12
            case PARROT:
                return new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build();
            case ILLUSIONER:
                return new ItemBuilder(Material.LINGERING_POTION).setGuiProperty().build();

            case COD:
                return new ItemBuilder(Material.COD_BUCKET).setGuiProperty().build();
            case DOLPHIN:
                return new ItemBuilder(Material.BUBBLE_CORAL).setGuiProperty().build();

            case PHANTOM:
                return new ItemBuilder(Material.PHANTOM_MEMBRANE).setGuiProperty().build();

            case PUFFERFISH:

                return new ItemBuilder(Material.PUFFERFISH_BUCKET).setGuiProperty().build();

            case SALMON:
                return new ItemBuilder(Material.SALMON_BUCKET).setGuiProperty().build();

            case TROPICAL_FISH:
                return new ItemBuilder(Material.TROPICAL_FISH_BUCKET).setGuiProperty().build();
            case TURTLE:
                return new ItemBuilder(Material.SCUTE).setGuiProperty().build();
            default:
                break;

        }
        return new ItemBuilder(Material.BARRIER).setGuiProperty().build();
    }

    @SuppressWarnings("deprecation")
    private ItemStack getGuiItem(SpawnReason element) {
        switch (element) {
            case BREEDING:
            case CHUNK_GEN:
            case DEFAULT:
            case JOCKEY:
            case LIGHTNING:
            case OCELOT_BABY:
            case SHOULDER_ENTITY:
                return new ItemBuilder(Material.GRANITE).setGuiProperty().build();
            case REINFORCEMENTS:
                return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
            case INFECTION:
                return new ItemBuilder(Material.ROTTEN_FLESH).setGuiProperty().build();
            case NATURAL:
                return new ItemBuilder(Material.GRASS_BLOCK).setGuiProperty().build();
            case NETHER_PORTAL:
                return new ItemBuilder(Material.OBSIDIAN).setGuiProperty().build();
            case BUILD_IRONGOLEM:
                return new ItemBuilder(Material.IRON_BLOCK).setGuiProperty().build();
            case BUILD_SNOWMAN:
                return new ItemBuilder(Material.SNOW_BLOCK).setGuiProperty().build();
            case BUILD_WITHER:
                return new ItemBuilder(Material.SOUL_SAND).setGuiProperty().build();
            case CURED:
                return new ItemBuilder(Material.GOLDEN_APPLE).setGuiProperty().build();
            case CUSTOM:
                return new ItemBuilder(Material.STRUCTURE_BLOCK).setGuiProperty().build();
            case DISPENSE_EGG:
                return new ItemBuilder(Material.DISPENSER).setGuiProperty().build();
            case DROWNED:
                return new ItemBuilder(Material.TRIDENT).setGuiProperty().build();
            case EGG:
                return new ItemBuilder(Material.EGG).setGuiProperty().build();
            case ENDER_PEARL:
                return new ItemBuilder(Material.ENDER_PEARL).setGuiProperty().build();
            case MOUNT:
                return new ItemBuilder(Material.SADDLE).setGuiProperty().build();
            case SHEARED:
                return new ItemBuilder(Material.SHEARS).setGuiProperty().build();
            case SILVERFISH_BLOCK:
                return new ItemBuilder(Material.STONE).setGuiProperty().build();
            case SLIME_SPLIT:
                return new ItemBuilder(Material.SLIME_BLOCK).setGuiProperty().build();
            case SPAWNER:
                return new ItemBuilder(Material.SPAWNER).setGuiProperty().build();
            case SPAWNER_EGG:
                return new ItemBuilder(Material.EVOKER_SPAWN_EGG).setGuiProperty().build();
            case TRAP:
                break;
            case VILLAGE_DEFENSE:
                return new ItemBuilder(Material.EMERALD).setGuiProperty().build();
            case VILLAGE_INVASION:
                return new ItemBuilder(Material.EMERALD).setGuiProperty().build();
            default:
                break;

        }
        return new ItemBuilder(Material.BARRIER).setGuiProperty().build();
    }

    @SuppressWarnings("deprecation")
    private ArrayList<String> getDescription(SpawnReason reason) {
        ArrayList<String> desc = new ArrayList<>();
        desc.add("");
        switch (reason) {
            case BREEDING:
                desc.add("&7When an animal breeds to create a child");
                break;
            case BUILD_IRONGOLEM:
                desc.add("&7When an iron golem is spawned by being built");
                break;
            case BUILD_SNOWMAN:
                desc.add("&7When a snowman is spawned by being built");
                break;
            case BUILD_WITHER:
                desc.add("&7When a wither boss is spawned by being built");
                break;
            case CHUNK_GEN:
                desc.add("&7When a creature spawns due to chunk generation");
                break;
            case CURED:
                desc.add("&7When a villager is cured from infection");
                break;
            case CUSTOM:
                desc.add("&7When a creature is spawned by plugins");
                break;
            case DEFAULT:
                desc.add("&7When an entity is missing a SpawnReason");
                break;
            case DISPENSE_EGG:
                desc.add("&7When a creature is spawned");
                desc.add("&7by a dispenser dispensing an egg");
                break;
            case EGG:
                desc.add("&7When a creature spawns from an egg");
                break;
            case ENDER_PEARL:
                desc.add("&7When an entity is spawned as");
                desc.add("&7a result of ender pearl usage");
                break;
            case INFECTION:
                desc.add("&7When a zombie infects a villager");
                break;
            case JOCKEY:
                desc.add("&7When an entity spawns as");
                desc.add("&7a jockey of another entity");
                desc.add("&7(mostly spider jockeys)");
                break;
            case LIGHTNING:
                desc.add("&7When a creature spawns because");
                desc.add("&7of a lightning strike");
                break;
            case MOUNT:
                desc.add("&7When an entity spawns as");
                desc.add("&7a mount of another entity");
                desc.add("&7(mostly chicken jockeys)");
                break;
            case NATURAL:
                desc.add("&7When something spawns from natural means");
                break;
            case NETHER_PORTAL:
                desc.add("&7When a creature is spawned by nether portal");
                break;
            case OCELOT_BABY:
                desc.add("&7When an ocelot has a baby");
                desc.add("&7spawned along with them");
                break;
            case REINFORCEMENTS:
                desc.add("&7When an entity calls for reinforcements");
                break;
            case SHOULDER_ENTITY:
                desc.add("&7When an entity is spawned as a");
                desc.add("&7result of the entity it is being");
                desc.add("&7perched on jumping or being damaged");
                break;
            case SILVERFISH_BLOCK:
                desc.add("&7When a silverfish spawns from a block");
                break;
            case SLIME_SPLIT:
                desc.add("&7When a slime splits");
                break;
            case SPAWNER:
                desc.add("&7When a creature spawns from a spawner");
                break;
            case SPAWNER_EGG:
                desc.add("&7When a creature spawns from a Spawner Egg");
                break;
            case TRAP:
                desc.add("&7When an entity spawns as");
                desc.add("&7a trap for players approaching");
                break;
            case VILLAGE_DEFENSE:
                desc.add("&7When an iron golem is spawned to defend a village");
                break;
            case VILLAGE_INVASION:
                desc.add("&7When a zombie is spawned to invade a village");
                break;
            case DROWNED:
                desc.add("&7When a creature is spawned by another");
                desc.add("&7entity drowning");
                break;
            case SHEARED:
                desc.add("&7When an cow is spawned by shearing");
                desc.add("&7a mushroom cow");
                break;
            default:
                break;
        }
        return desc;
    }
}
