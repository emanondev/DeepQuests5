package emanondev.deepquests.command;

public class DeepQuestImport {
}/* extends ACommand {

	//private boolean imported = false;
	public DeepQuestImport() {
		super("deepquestimport", Arrays.asList("dqimport"), Perms.ADMIN_EDITOR);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quests.get().reload();
		//if (imported == false) {
		try {
			Importer.importOld(sender);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//imported = true;
			sender.sendMessage("Quest importate");
		//}
		//else
		//	sender.sendMessage("Hai già importato precedentemente le quest, non ripetere il comando");
	}
}*/