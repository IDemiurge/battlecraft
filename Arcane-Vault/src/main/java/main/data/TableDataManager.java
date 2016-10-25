package main.data;

import main.AV_DataManager;
import main.content.ContentManager;
import main.content.MACRO_OBJ_TYPES;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.parameters.G_PARAMS;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.data.xml.XML_Reader;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.simulation.SimulationManager;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class TableDataManager {

	public static final String CHARS = "chars";
	public static final String UNITS = "units";
	public static final int VALUE_COLUMN = 1;
	public static final int NAME_COLUMN = 0;
	public static final int ROW_HEIGHT = 32;
	public static final int FONT_SIZE = 16;
	private static final PARAMETER[] SHOW_FORMULA_PARAM_LIST = {
			PARAMS.FORMULA, G_PARAMS.DURATION, };
	public static int IMG_ROW = 1;

	public void init() {
		// subgroups
	}

	public static Set<ObjType> getTypes(String key) {
		return null;

	}

	public static List<String> getTreeTabGroups() {
		List<String> set = new LinkedList<String>();
		for (OBJ_TYPE type : (ArcaneVault.isMacroMode()) ? MACRO_OBJ_TYPES
				.getTypeGroups() : OBJ_TYPES.getTypeGroups()) {
			if (type.getCode() != -1)
				set.add(type.getName());
		}
		return set;
	}

	public static List<String> getTreeTabSubGroups(String key) {
		return new LinkedList<String>(XML_Reader.getSubGroups(key));
	}

	public static Vector<Vector<String>> getTypeData(Entity entity) {
		if (entity == null) {
			return null;
		}
		List<String> names = AV_DataManager.getValueNames(entity.getOBJ_TYPE());
		main.system.auxiliary.LogMaster.log(
				0,
				"VALUE NAMES FOR TYPE " + entity.getOBJ_TYPE() + ": "
						+ names.toString());
		int rows = names.size();

		Vector<Vector<String>> data = new Vector<Vector<String>>(rows);
		if (SimulationManager.isUnitType(entity.getOBJ_TYPE())
				&& ArcaneVault.isSimulationOn()) {
			try {
				fillData(SimulationManager.getUnit((ObjType) entity), names,
						data);
				return data;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			fillData(entity, names, data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	private static void fillData(Entity entity, List<String> names,
			Vector<Vector<String>> data) {

		// Map<VALUE, String> values = new HashMap<VALUE, String>();
		// values.putAll(entity.getPropMap().getMap());
		// values.putAll(entity.getParamMap().getMap());
		for (String name : names) {
			Vector<String> vector1 = new Vector<String>();
			if (name.equalsIgnoreCase("Material Quantity"))
				vector1 = new Vector<String>();
			vector1.add(name);
			VALUE val = ContentManager.getValue(name);
			String value = null;
			if (val instanceof PROPERTY) {
				value = entity.getProperty((PROPERTY) val);
			} else {
				if (ListMaster.toList(SHOW_FORMULA_PARAM_LIST).contains(val))
					value = entity.getParam((PARAMETER) val);
				else
					try {
						value = "" + entity.getIntParam((PARAMETER) val);
					} catch (Exception e) {

						e.printStackTrace();
						value = "(error)"
						// + entity.getParam((PARAMETER) val)
						;
					}

			}

			if (value == null || StringMaster.isEmpty(value))
				value = ContentManager.getValue(name).getDefaultValue();
			vector1.add(value);
			data.add(vector1);

		}

	}

	public static Collection<String> getOrderedValueList(
			Collection<VALUE> values) {
		for (VALUE value : values) {
			value.getFullName();
		}
		return null;

	}

	public static void fillTableValues(DefaultTableModel model) {

	}

	public static int getMaxWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int getPrefWidth() {
		return 0;
	}

	public static int getMinWidth() {
		// TODO Auto-generated method stub
		return 0;
	}
}