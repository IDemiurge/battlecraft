package main.gui.components.tree;

import main.content.ContentManager;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.logic.dungeon.editor.LevelEditor;
import main.gui.builders.EditViewPanel;
import main.gui.builders.TabBuilder;
import main.launch.ArcaneVault;
import main.simulation.SimulationManager;
import main.swing.SwingMaster;
import main.swing.generic.components.G_Panel;
import main.system.launch.CoreEngine;
import main.utilities.workspace.Workspace;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class AV_TreeSelectionListener implements TreeSelectionListener {

	private EditViewPanel panel;
	private JTree tree;

	public AV_TreeSelectionListener(JTree tree) {
		if (CoreEngine.isLevelEditor())
			panel = LevelEditor.getMainPanel().getInfoPanel();
		else
			panel = ArcaneVault.getMainBuilder().getEditViewPanel();
		this.tree = tree;
		// tree.setSelectionPath(path);
		// DefaultMutableTreeNode node = ArcaneVault.getMainBuilder()
		// .getSelectedNode();
		// node.getp

	}

	@Override
	public void valueChanged(TreeSelectionEvent e1) {
		if (e1 == null)
			return;
		tree.requestFocusInWindow();
		boolean dtFlag = ArcaneVault.isDirty();
		if (((JTree) e1.getSource()).getSelectionPath() == null)
			return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) (((JTree) e1.getSource())
				.getSelectionPath().getLastPathComponent());

		if (node == null)
			return;

		String name = (String) node.getUserObject();
		String tab = null;
		if (panel.isLevelEditor())
			tab = LevelEditor.getSimulation().getSelectedEntity().getOBJ_TYPE();
		else
			tab = ArcaneVault.getMainBuilder().getSelectedTabName();
		if (tab == null) {
			Workspace workspace = ArcaneVault.getWorkspaceManager().getWorkspaceByTab(
					(G_Panel) SwingMaster.getParentOfClass(tree, G_Panel.class));
			if (workspace != null)
				try {
					tab = workspace.getOBJ_TYPE(name, tab).getName();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		// SoundManager.playEffectSound(SOUNDS.WHAT,
		// DataManager.getType(name)
		// .getProperty(PROPS.SOUNDSET));

		if (SimulationManager.isUnitType(tab) && ArcaneVault.isSimulationOn()) {
			try {
				SimulationManager.initUnitObj(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ObjType type = DataManager.getType(name, tab);
		if (type == null) {
			for (TabBuilder t : ArcaneVault.getAdditionalTrees()) {
				type = DataManager.getType(name, t.getSelectedTabName());
				if (type != null)
					break;
			}
		}
		if (type == null)
			type = DataManager.getType(name);
		if (type == null)
			return;
		selectType(type, tab);
		List<ObjType> types = new LinkedList<>();
		int length = 1;
		try {
			length = e1.getPaths().length;
		} catch (Exception e) {

		}
		if (length > 2) { // TODO
			try {
				for (TreePath p : e1.getPaths()) {
					node = (DefaultMutableTreeNode) p.getLastPathComponent();
					if (node == null)
						continue;
					name = (String) node.getUserObject();
					type = DataManager.getType(name, tab);
					if (type != null)
						types.add(type);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			types.add(type);
		}
		ArcaneVault.setSelectedTypes(types);
		ArcaneVault.setDirty(dtFlag);

	}

	public void selectType(ObjType type, String tab) {
		panel.selectType(type);
	}

	public boolean isTreeEditType(String selected) {
		if (ArcaneVault.isMacroMode()) {
			return ContentManager.getOBJ_TYPE(selected).isTreeEditType();
		}
		return selected.equals(OBJ_TYPES.ABILS.getName());
	}
}
