package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.events.QuestItemObtainEvent;
import emanondev.deepquests.interfaces.QuestBag;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class AQuestBag<T extends User<T>> implements QuestBag<T> {

    private final T user;
    private final YMLSection section;

    public AQuestBag(@NotNull T user, @NotNull YMLSection section) {
        this.user = user;
        this.section = section;
    }

    public @NotNull YMLSection getConfig() {
        return section;
    }

    @Override
    public @NotNull T getUser() {
        return user;
    }

    @Override
    public void addQuestItem(@NotNull String id, int amount) {
        if (amount < 0)
            throw new IllegalArgumentException();
        if (amount == 0)
            return;
        section.set(id, section.getInteger(id, 0) + amount);
        Bukkit.getPluginManager().callEvent(new QuestItemObtainEvent<>(user, id, amount));
    }

    @Override
    public boolean hasQuestItem(@NotNull String id, int amount) {
        return section.getInteger(id, 0) > 0;
    }

    @Override
    public int removeQuestItem(@NotNull String id, int amount) {
        if (amount < 0)
            throw new IllegalArgumentException();
        if (amount == 0)
            return 0;
        int own = getQuestItemAmount(id);
        if (own <= amount) {
            section.set(id, null);
            return own;
        }
        section.set(id, own - amount);
        return amount;
    }

    @Override
    public int getQuestItemAmount(String id) {
        return section.getInteger(id, 0);
    }

    @Override
    public @NotNull Map<String, Integer> getQuestItems() {
        LinkedHashMap<String, Integer> items = new LinkedHashMap<>();
        for (String id : section.getKeys(false)) {
            int amount = getQuestItemAmount(id);
            if (amount > 0)
                items.put(id, amount);
        }
        return items;
    }

    @Override
    public void reset() {
        for (String id : section.getKeys(false))
            section.set(id, null);
    }

}
