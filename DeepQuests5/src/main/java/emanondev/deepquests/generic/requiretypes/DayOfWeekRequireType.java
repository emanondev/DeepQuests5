package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.util.*;

public class DayOfWeekRequireType<T extends User<T>> extends ARequireType<T> {
    private static final String ID = "day_of_week";

    public DayOfWeekRequireType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.CLOCK;
    }

    @Override
    public List<String> getDescription() {
        return List.of("Require selected mission to have one of selected days");
    }

    @Override
    public @NotNull Require<T> getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new DayOfWeekRequire(id, manager, section);
    }

    public class DayOfWeekRequire extends ARequire<T> {

        private final EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);

        public DayOfWeekRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, DayOfWeekRequireType.this, section);
            days.addAll(this.getConfig().loadEnumSet("allowed_days", null, DayOfWeek.class));
        }

        @Override
        public boolean isAllowed(@NotNull T user) {
            return days.contains(DayOfWeek.of(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
        }


        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add("&9allowed_days");
            for (DayOfWeek day : days)
                info.add("  &9- &e" + day.name());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        public void toggleDay(DayOfWeek day) {
            if (days.contains(day))
                days.remove(day);
            else
                days.add(day);
            this.getConfig().setEnumsAsStringList("allowed_days", days);
        }

        public boolean isAllowedDay(DayOfWeek day) {
            return days.contains(day);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, new CollectionSelectorButton<DayOfWeek>("&9Day Selector",
                        new ItemBuilder(Material.CLOCK).setGuiProperty().build(), this, false) {

                    @Override
                    public Collection<DayOfWeek> getPossibleValues() {
                        return Arrays.asList(DayOfWeek.values());
                    }

                    @Override
                    public List<String> getButtonDescription() {
                        List<String> list = new ArrayList<>();
                        list.add("&6Enabled Days");
                        for (DayOfWeek day : days)
                            list.add("&9- &e" + day.name());
                        return list;
                    }

                    @Override
                    public List<String> getElementDescription(DayOfWeek element) {
                        return List.of((isAllowedDay(element) ? "&a" : "&c") + element.name());
                    }

                    @Override
                    public ItemStack getElementItem(DayOfWeek element) {
                        return new ItemBuilder(Material.CLOCK).setGuiProperty().setAmount(element.getValue()).build();
                    }

                    @Override
                    public boolean isValidContains(DayOfWeek element) {
                        return days.contains(element);
                    }

                    @Override
                    public boolean getIsWhitelist() {
                        return true;
                    }

                    @Override
                    public boolean onToggleElementRequest(DayOfWeek element) {
                        toggleDay(element);
                        return true;
                    }

                    @Override
                    public boolean onWhitelistToggle() {
                        throw new UnsupportedOperationException();
                    }
                });
            }
        }
    }
}