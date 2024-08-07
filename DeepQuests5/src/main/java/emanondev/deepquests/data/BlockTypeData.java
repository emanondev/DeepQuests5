package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BlockTypeData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

    private final EnumSet<Material> materials = EnumSet.noneOf(Material.class);
    private boolean isWhitelist;

    public BlockTypeData(@NotNull E parent, @NotNull YMLSection section) {
        super(parent, section);
        isWhitelist = getConfig().getBoolean(Paths.DATA_BLOCK_TYPE_IS_WHITELIST, true);
        materials.addAll(getConfig().loadEnumSet(Paths.DATA_BLOCK_TYPE_LIST, EnumSet.noneOf(Material.class), Material.class));
    }

    public boolean isValidMaterial(Material material) {
        if (materials.isEmpty())
            return true;
        if (isWhitelist)
            return materials.contains(material);
        else
            return !materials.contains(material);
    }

    public void toggleMaterial(Material mat) {
        if (mat == null)
            return;
        if (mat.isAir() || !mat.isBlock())
            return;
        if (materials.contains(mat))
            materials.remove(mat);
        else
            materials.add(mat);
        getConfig().setEnumsAsStringList(Paths.DATA_BLOCK_TYPE_LIST, materials);
    }

    public Set<Material> getMaterials() {
        return Collections.unmodifiableSet(materials);
    }

    public boolean areMaterialsWhitelist() {
        return isWhitelist;
    }

    public void toggleWhitelist() {
        this.isWhitelist = !this.isWhitelist;
        getConfig().set(Paths.DATA_BLOCK_TYPE_IS_WHITELIST, isWhitelist);
    }

    public BlockEditorButton getBlockEditorButton(Gui parent) {
        return new BlockEditorButton(parent);
    }

    private class BlockEditorButton extends CollectionSelectorButton<Material> {

        public BlockEditorButton(Gui parent) {
            super("&9Block Selector",
                    new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build(),
                    parent, true);
        }

        @Override
        public Collection<Material> getPossibleValues() {
            List<Material> list = new ArrayList<>();
            for (Material mat : Material.values())
                if (!mat.isAir() && mat.isBlock())
                    list.add(mat);
            Collections.sort(list);
            return new LinkedHashSet<>(list);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> desc = new ArrayList<>();
            if (isWhitelist) {
                desc.add("&9Valid Materials:");
                for (Material mat : materials)
                    desc.add("  &9- &a" + mat);
            } else {
                desc.add("&9Invalid Materials:");
                for (Material mat : materials)
                    desc.add("  &9- &c" + mat);
            }

            return desc;
        }

        @Override
        public List<String> getElementDescription(Material element) {
            return Arrays.asList("&9Type: &e" + element.toString()
                    , "", "&9Translation Name: " + Translations.translate(element));
        }

        @Override
        public ItemStack getElementItem(Material element) {
            if (element.isItem())
                return new ItemBuilder(element).setGuiProperty().build();
            return new ItemBuilder(switch (element) {
                //TODO
                case BEETROOTS -> Material.BEETROOT;
                case CARROTS -> Material.CARROT;
                case POTATOES -> Material.POTATO;
                default -> Material.KNOWLEDGE_BOOK;
            }).setGuiProperty().build();
        }

        @Override
        public boolean isValidContains(Material element) {
            return isValidMaterial(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return areMaterialsWhitelist();
        }

        @Override
        public boolean onToggleElementRequest(Material element) {
            toggleMaterial(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleWhitelist();
            return true;
        }

    }


}