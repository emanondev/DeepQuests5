package emanondev.deepquests.data;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JobTypeData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private Job job = null;

    public JobTypeData(E parent, YMLSection section) {
        super(parent, section);
        try {
            String jobName = section.getString(Paths.DATA_JOB_TYPE, null);
            if (jobName != null)
                job = Jobs.getJob(jobName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        if (this.job == job)
            return;
        if (this.job != null && this.job.equals(job))
            return;
        this.job = job;
        getConfig().set(Paths.DATA_JOB_TYPE, job == null ? null : job.getName());
    }

    public Button getJobSelector(Gui gui) {
        return new JobSelector(gui);
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        if (job != null)
            list.add("&9Job: &e" + job.getName());
        else
            list.add("&9Job: &cnot set");
        return list;
    }

    private class JobSelector extends ElementSelectorButton<Job> {

        public JobSelector(Gui parent) {
            super("&9Select a Job", new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build(), parent, true, true,
                    false);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = getInfo();
            list.add(0, "&6Job Selector");
            return list;
        }

        @Override
        public List<String> getElementDescription(Job element) {
            ArrayList<String> list = new ArrayList<>();
            list.add("&9Job: &e" + job.getName());
            return list;
        }

        @Override
        public ItemStack getElementItem(Job element) {
            return new ItemBuilder(element.getGuiItem()).setGuiProperty().build();
        }

        @Override
        public void onElementSelectRequest(Job element) {
            setJob(element);
            getGui().updateInventory();
            getGui().getTargetPlayer().openInventory(getGui().getInventory());
        }

        @Override
        public Collection<Job> getPossibleValues() {
            return Jobs.getJobs();
        }

    }

}
