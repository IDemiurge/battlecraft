package main.test.auto;

import main.content.CONTENT_CONSTS.WEAPON_GROUP;
import main.content.CONTENT_CONSTS.WORKSPACE_GROUP;
import main.content.CONTENT_CONSTS2.AUTO_TEST_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.DC_FeatObj;
import main.entity.type.ObjType;
import main.system.FilterMaster;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;
import main.test.auto.AutoTest.TEST_ARGS;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AutoTestFactory {
    AutoTestMaster master;
    private OBJ_TYPES TYPE;
    private List<ObjType> testTypes;
    private String[] presetTypes;

    public AutoTestFactory(AutoTestMaster master) {
        this.master = master;
    }

    static AUTO_TEST_TYPE getType(Entity e) {
        if (e.getProperty(G_PROPS.PASSIVES).contains("ActionMod(")) {
            return AUTO_TEST_TYPE.ACTION_SKILL;
        }
        return AUTO_TEST_TYPE.PASSIVE_MEASURE;
    }

    private ObjType getWeaponTypeForGroup(String group) {
        return DataManager.getType(new EnumMaster<WEAPON_GROUP>().retrieveEnumConst(
                WEAPON_GROUP.class, group).getDefaultType(), OBJ_TYPES.WEAPONS);
    }

    public Entity initEntity(ObjType type) {
        switch ((OBJ_TYPES) type.getOBJ_TYPE_ENUM()) {
            case CLASSES:
            case SKILLS:
                return new DC_FeatObj(type, new Ref(master.getSource()));
        }
        return null;
    }

    private String[] getWorkspaceTypes() {
        String xml = FileManager.readFile(PathFinder.getWorkspacePath() + getTYPE() + ".xml");
        List<String> parts = StringMaster.openContainer(xml, "METADATA: ");
        xml = parts.get(0);
        try {
            testTypes = XML_Converter.getTypeListFromXML(xml, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        testTypes = (List<ObjType>) FilterMaster.filterByProp(testTypes, G_PROPS.WORKSPACE_GROUP
                .getName(), WORKSPACE_GROUP.TEST.name());
        testTypes = (List<ObjType>) SortMaster.sortByValue(testTypes, getSortValue(getTYPE()),
                false);
        testTypes = (List<ObjType>) SortMaster.sortByValue(testTypes, PARAMS.AUTO_TEST_ID, true);
        return DataManager.toStringList(testTypes).toArray(new String[testTypes.size()]);
    }

    private PARAMETER getSortValue(OBJ_TYPES TYPE) {
        switch (TYPE) {

        }
        return PARAMS.XP_COST;
    }

    public String[] config() {
        if (presetTypes != null)
            return presetTypes;
        if (master.isWorkspace())
            return getWorkspaceTypes();

        if (master.isSkill())
            return AutoTestMaster.SKILL_PRESET_TEST_TYPES.split(";");
        return AutoTestMaster.CLASS_PRESET_TEST_TYPES.split(";");

    }

    public List<AutoTest> initTests() {
        List<AutoTest> tests = new LinkedList<>();
        testTypes = DataManager.toTypeList(Arrays.asList(config()), getTYPE());
        for (ObjType type : testTypes) {
            AutoTest test = createTest(type);
            tests.add(test);
        }
        return tests;
    }

    public AutoTest createTest(ObjType type) {
        List<String> list = new LinkedList<>();
        list.add(TEST_ARGS.NAME + ":" + type.getName());
        initArgList(list, type);
        String args = new StringMaster().constructContainer(list);
        AUTO_TEST_TYPE t = new EnumMaster<AUTO_TEST_TYPE>().retrieveEnumConst(AUTO_TEST_TYPE.class,
                type.getProperty(PROPS.AUTO_TEST_TYPE));
        if (t == null)
            t = getType(type);
        AutoTest test = new AutoTest(type, args, t, master);
        return test;
    }

    private void initArgList(List<String> list, ObjType testType) {
        String weapon = testType.getProperty(PROPS.AUTO_TEST_WEAPON);
        String actionNames = "";
        if (weapon.isEmpty()) {
            if (testType.getName().contains("Specialization")) {
                weapon = testType.getName().replace(" Specialization", "");
            }

            searchLoop:
            for (String part : StringMaster.openContainer(testType
                    .getProperty(G_PROPS.PASSIVES))) {
                if (part.contains("ActionMod(")) {
                    for (String s : StringMaster.openContainer(StringMaster.getSubString(false,
                            part, "ActionMod(", ",", null), StringMaster.AND_SEPARATOR))
                        actionNames += s + ";";
                    // find weapon that has all actions involved?
                    // ALT: find base specialization?
                    loop:
                    for (ObjType t : DataManager.getTypes(OBJ_TYPES.WEAPONS)) {
                        for (String s : StringMaster.openContainer(actionNames)) {

                            String actions = t.getProperty(PROPS.WEAPON_ATTACKS);
                            if (!actions.contains(s))
                                continue loop;
                            else
                                weapon = t.getName();
                        }
                        weapon = t.getName();
                        break searchLoop;
                    }
                }
            }
        }
        if (master.isSkill()) // TODO
            list.add(TEST_ARGS.TEST_SKILLS + StringMaster.PAIR_SEPARATOR + testType.getName());
        if (!weapon.isEmpty())
            list.add(TEST_ARGS.WEAPON + StringMaster.PAIR_SEPARATOR + weapon);
        if (!actionNames.isEmpty())
            list.add(TEST_ARGS.ACTION_NAMES + StringMaster.PAIR_SEPARATOR + actionNames);

        // list.add(TEST_ARGS.WEAPON2 + StringMaster.PAIR_SEPARATOR + weapon);

    }

    public String[] getPresetTypes() {
        return presetTypes;
    }

    public void setPresetTypes(String[] presetTypes) {
        this.presetTypes = presetTypes;
    }

    public OBJ_TYPES getTYPE() {
        return TYPE;
    }

    public void setTYPE(OBJ_TYPES tYPE) {
        TYPE = tYPE;
    }

}