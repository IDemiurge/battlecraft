package main.game.logic.dungeon.editor.gui;

import main.content.CONTENT_CONSTS.PALETTE;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlefield.XDimension;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagedListPanel;
import main.system.auxiliary.EnumMaster;
import main.utilities.workspace.Workspace;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PagedPaletteTab extends G_PagedListPanel<ObjType> {

	public static final int PAGE_SIZE = 60;
	XDimension objSize;
	private OBJ_TYPES type;
	private String[] groups;
	private Workspace ws;

	public List<String> getGroups(PALETTE p) {
		switch (p) {
			case DARK:
				return new LinkedList<String>(Arrays.asList(new String[] {}));
		}
		return null;

	}

	public PagedPaletteTab(OBJ_TYPES type) {
		super(PAGE_SIZE, true, 3);
		this.type = type;
		// this.groups = groups;
	}

	public PagedPaletteTab(Workspace ws) {
		super(PAGE_SIZE, true, 3);
		this.ws = ws;
	}

	@Override
	public boolean isAutoSizingOn() {
		return true;
	}

	@Override
	protected boolean isAddControlsAlways() {
		return false;
	}

	@Override
	protected G_Component createPageComponent(List<ObjType> list) {
		return new PaletteList(list);
	}

	@Override
	protected List<List<ObjType>> getPageData() {
		if (ws != null) {
			return splitList(ws.getTypeList());
		}
		List<String> types = DataManager.getTypeNames(type);
		Collections.sort(types, new EnumMaster<>().getEnumTypesSorter(isSubGroupSorting(),
				getType()));
		return splitList(DataManager.toTypeList(types, getType()));
		// List<List<ObjType>> lists = new LinkedList<>();
		// List<ObjType> types = DataManager.getTypes(type);
		// lists.add(DataManager.getTypes(type));
		// for (String group : groups) {
		// // if (group.equalsIgnoreCase("All"))
		// // else
		// lists.add(DataManager.getTypesGroup(type, group));
		// }
		// return lists;
	}

	private boolean isSubGroupSorting() {
		return true;
	}

	public OBJ_TYPES getType() {
		return type;
	}

	public Workspace getWs() {
		return ws;
	}

}