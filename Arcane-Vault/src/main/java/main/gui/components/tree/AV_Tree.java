package main.gui.components.tree;

import main.content.CONTENT_CONSTS.WORKSPACE_GROUP;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.elements.Filter;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TreeMaster;
import main.system.auxiliary.secondary.DefaultComparator;
import main.utilities.workspace.Workspace;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class AV_Tree extends G_Panel {
	private JTree tree;
	private OBJ_TYPE type;
	private boolean colorsInverted = true;
	Workspace workspace;
	private AV_TreeCellRenderer renderer;
	private boolean simpleTree;
	private DefaultMutableTreeNode parent;
	private int i;
	private static boolean fullNodeStructureOn;

	// final DefaultTreeModel model = new DefaultTreeModel(root) {
	//
	// @Override
	// public boolean isLeaf(Object node) {
	// if (isRoot(node)) {
	// return false;
	// }
	// return super.isLeaf(node);
	// }
	//
	// private boolean isRoot(Object node) {
	// return node != null && node == getRoot();
	// }
	//
	// };

	public AV_Tree(List<String> typesDoc, String group, String type) {
		this(typesDoc, group, type, true);

	}

	public AV_Tree(Workspace workspace) {
		this.workspace = workspace;
		this.tree = buildTree(DataManager.toStringList(workspace.getTypeList()), "");
		addTree();
	}

	public AV_Tree(List<String> typesDoc, String group, String type, boolean colorsInverted) {
		this.type = ContentManager.getOBJ_TYPE(type);
		this.tree = buildTree(typesDoc, group);
		this.colorsInverted = colorsInverted;
		addTree();
	}

	private void addTree() {
		add(tree, "pos 0 0");
		tree.setLargeModel(true);
		tree.setRootVisible(false);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		renderer = new AV_TreeCellRenderer();
		tree.setCellRenderer(renderer);
		if (workspace != null) {
			renderer.setWorkspace(workspace);
		} else
			renderer.setTYPE(type);
		renderer.setColorsInverted(colorsInverted);
		tree.setUI(renderer);
	}

	public void setColorsInverted(boolean colorsInverted) {
		renderer.setColorsInverted(colorsInverted);
	}

	private JTree buildTree(List<String> simpleTree, String name) {
		JTree tree = new JTree((build(simpleTree, name)));
		return tree;
	}

	private JTree buildTree(Document doc) {
		JTree tree = new JTree((build(doc)));
		return tree;
	}

	private DefaultMutableTreeNode buildSimple(List<String> typesDoc, String name) {
		simpleTree = true;
		if (workspace == null)
			Collections.sort(typesDoc, getComparator());

		DefaultMutableTreeNode result = new DefaultMutableTreeNode(name);
		List<String> upgrades = new LinkedList<>();
		for (String node : typesDoc) {
			addNode(result, node, upgrades);
		}
		for (String typeName : upgrades) {
			addUpgradeNode(result, typeName);
		}
		return result;
	}

	private DefaultMutableTreeNode build(List<String> typesDoc, String group) {
		DefaultMutableTreeNode result = new DefaultMutableTreeNode(group);

		List<String> subGroups = new LinkedList<>();
		// if (workspace!=null) workspace.getGrouping() ;
		if (!StringMaster.isEmpty(group))
			try {
				Set<String> groups = XML_Reader.getTreeSubGroupMap().get(group);
				if (groups == null)
					groups = XML_Reader.getTreeSubGroupMap(!XML_Reader.isMacro()).get(group);
				subGroups = new LinkedList<String>(groups);
				Class<?> ENUM = EnumMaster.getEnumClass(type.getSubGroupingKey().getName());
				if (ENUM != null)
					Collections.sort(subGroups, new EnumMaster<>().getEnumSorter(ENUM));
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if (workspace != null) {
			subGroups = workspace.getSubgroups();
			if (subGroups == null)
				// TODO custom *grouping* -> enum class + property!
				if (workspace.isSearch()) {
					subGroups = ListMaster.toStringList(OBJ_TYPES.values());
				} else {
					subGroups = ListMaster.toStringList(WORKSPACE_GROUP.values());
					subGroups.add("");
				}
			// subGroups = workspace.getSubgroups();
			// subGroups = workspace.getSubgroups();

		}

		// TODO filter sub groups
		if (subGroups.size() <= 1) {
			return buildSimple(typesDoc, group);
		}
		for (String subGroup : subGroups) {
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subGroup);

			List<String> upgrades = new LinkedList<>();
			List<String> list = null;
			if (workspace == null) {
				Set<String> c = XML_Reader.getTreeSubGroupedTypeMap(XML_Reader.isMacro()).get(
						subGroup);
				if (!ListMaster.isNotEmpty(c))
					c = XML_Reader.getTreeSubGroupedTypeMap(!XML_Reader.isMacro()).get(subGroup);
				list = new LinkedList<>(c);
				Collections.sort(list, getComparator());
			} else {
				if (workspace.isSearch()) {
					list = DataManager.toStringList(new Filter<ObjType>().filter(workspace
							.getTypeList(), G_PROPS.TYPE, subGroup));
					Collections.sort(list, new EnumMaster<>().getEnumSorter(OBJ_TYPES.class));

				} else {

					PROPERTY filterValue = G_PROPS.WORKSPACE_GROUP;
					if (workspace.getSubgroupingProp() != null)
						filterValue = workspace.getSubgroupingProp();
					list = DataManager.toStringList(new Filter<ObjType>().filter(workspace
							.getTypeList(), filterValue, subGroup));
					try {
						Collections.sort(list, new EnumMaster<>()
								.getEnumSorter(WORKSPACE_GROUP.class));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			for (String typeName : list) {
				if (!typesDoc.contains(typeName))
					continue;
				addNode(subNode, typeName, upgrades);
			}
			if (!subNode.isLeaf()) {
				result.add(subNode);
			} // TODO is it ok?
			DefaultMutableTreeNode subNode2 = subNode;
			if (isFullNodeStructureOn())
				for (String typeName : upgrades) {
					subNode.add(new DefaultMutableTreeNode(typeName));
				}
			else
				while (true) {
					List<String> upgrades2 = new LinkedList<>(upgrades);
					for (String typeName : upgrades2) { // subnode could be a
														// Type
														// Node!
						if (addUpgradeNode(subNode2, typeName))
							upgrades.remove(typeName);
					}
					if (upgrades.isEmpty()) {
						parent = null;
						i = 0;
						break;
					}
					/*
					 * basically, it seems that root type count limits depth 
					 * the check 
					 * 
					 * 
					 */
					if (parent == null)
						if (subNode2 == null)
							parent = subNode2;
						else
							parent = subNode;
					subNode2 = getNextNode();
					if (subNode2 == null) {
						parent = null;
						i = 0;
						main.system.auxiliary.LogMaster.log(1, upgrades + " remain, parent="
								+ parent + i);
						for (String typeName : upgrades) {
							subNode.add(new DefaultMutableTreeNode(typeName));
						}
						break;
					}
					// tr
				}

		}
		// if (result.getChildCount() == 1)
		// return (DefaultMutableTreeNode) result.getFirstChild();
		return result;

	}

	public static boolean isFullNodeStructureOn() {
		return fullNodeStructureOn;
	}

	public static void setFullNodeStructureOn(boolean fullNodeStructureOn2) {
		fullNodeStructureOn = fullNodeStructureOn2;
	}

	private DefaultMutableTreeNode getNextNode() {
		if (parent.getChildCount() <= i)
			return null;
		i++;
		return (DefaultMutableTreeNode) parent.getChildAt(i - 1);
	}

	private boolean addUpgradeNode(DefaultMutableTreeNode subNode, String typeName) {
		DefaultMutableTreeNode node = TreeMaster.findChildNode(subNode, DataManager.getType(
				typeName).getProperty(G_PROPS.BASE_TYPE));
		if (node == null)
			return false;
		node.add(new DefaultMutableTreeNode(typeName));
		return true;
	}

	private Comparator<? super String> getComparator() {
		// if (sortAlphabetically){
		return new DefaultComparator<>();
		// }else
		// if (sortById){
		// return new IdComparator<ObjType>();
		// }
		// return null;
	}

	private void addNode(DefaultMutableTreeNode subNode, String typeName, List<String> list) {
		if (checkUpgrade(typeName)) {
			list.add(typeName);
			return;
		}
		subNode.add(new DefaultMutableTreeNode(typeName));
	}

	private boolean checkUpgrade(String typeName) {
		ObjType type = DataManager.getType(typeName);
		if (type == null)
			return false;
		return !StringMaster.isEmpty(type.getProperty(G_PROPS.BASE_TYPE));

	}

	// public static DefaultMutableTreeNode build(List<List<String>> simpleTree,
	// String name) {
	// DefaultMutableTreeNode result = new DefaultMutableTreeNode(name);
	// for (List<String> list : simpleTree) {
	// DefaultMutableTreeNode subnode=new DefaultMutableTreeNode("SUBNODE");
	// for (String node : list)
	// {
	// subnode.add(new DefaultMutableTreeNode(node));
	// }
	// result.add(subnode);
	// }
	// return result;
	// }

	public static DefaultMutableTreeNode build(Node e) {

		DefaultMutableTreeNode result = new DefaultMutableTreeNode(e.getNodeName());

		// logger.info(e.getNodeName());
		int x = e.getChildNodes().getLength();

		for (int i = 0; i < x; i++) {
			Node child = e.getChildNodes().item(i);
			if (child.getNodeName().contains("#text")) {
				continue;
			}

			result.add(build(child));
		}
		return result;

	}

	public JTree getTree() {
		return tree;
	}

	public Object getSelectedItem() {
		return tree.getSelectionPath().getLastPathComponent();
	}

	public String getSelectedItemName() {
		return ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent())
				.getUserObject()
				+ "";
	}

	public boolean isSimpleTree() {
		return simpleTree;
	}

	// if (typeName.equalsIgnoreCase("Psi Archon"))
	// typeName = "Psi Archon";
	// if (addUpgradeNode(subNode2, typeName))
	// upgrades.remove(typeName);
	// }
	// if (upgrades.isEmpty()) {
	// parent = null;
	// i = 0;
	// break;
	// }
	// if (parent == null)
	// if (subNode2 != null)
	// parent = subNode2;
	// else
	// parent = subNode;
	// subNode2 = getNextNode(parent, i);
	// if (subNode2 == null) {
	// parent = null;
	// i = 0;
	// break;
	// }
	// // tr
	// }
	//
	// }
	// // if (result.getChildCount() == 1)
	// // return (DefaultMutableTreeNode) result.getFirstChild();
	// return result;
	//
	// }
	//
	// private DefaultMutableTreeNode getNextNode
	// (DefaultMutableTreeNode parent, int i) {
	// if (parent.getChildCount() <= i)
	// {
	// getNextNode(parent.getChildAt(n)
	// parent.getChildAt(n);
	//
	// return null;
	// else n++;
	// }
	// i++;
	// return (DefaultMutableTreeNode) parent.getChildAt(i - 1);
	// }
}