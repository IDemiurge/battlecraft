package main.system.text;

import main.client.DC_Engine;
import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.*;
import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextMaster {
    private static final String descrHeaderSeparator = "<>";
    public static String[] props = {"Lore", "Description",};
    private static String locale = "english";
    private static Map<OBJ_TYPE, List<String>> extractedTypesMap = new XLinkedMap<>();
    private static PROPERTY[] extract_props = {G_PROPS.DESCRIPTION, G_PROPS.LORE, G_PROPS.TOOLTIP,};
    private static OBJ_TYPE[] extractedTypes = {OBJ_TYPES.CHARS, OBJ_TYPES.UNITS,
            OBJ_TYPES.CLASSES, OBJ_TYPES.SKILLS, OBJ_TYPES.SPELLS, OBJ_TYPES.DEITIES,};
    private static String[] extractedTypeGroups = {"Background"};

    public static void generateMissingDescrTemplate() {
        OBJ_TYPES TYPE = OBJ_TYPES.SKILLS;
        generateMissingDescrTemplate(TYPE, 1, null, false);
        generateMissingDescrTemplate(TYPE, 1, null, true);
        Map<String, Set<String>> tabGroupMap = XML_Reader.getTabGroupMap();
        for (String sub : tabGroupMap.get(TYPE.getName())) {
//            if (!ContentMaster.basicScope.contains(sub))
//                continue;
            generateMissingDescrTemplate(TYPE, 1, sub, false);
            generateMissingDescrTemplate(TYPE, 1, sub, true);
        }
    }

    public static void generateMissingDescrTemplate(OBJ_TYPES TYPE, int circle, String groups,
                                                    boolean appendLogic) {
        List<ObjType> list = new LinkedList<>();
        List<ObjType> incomplete = new LinkedList<>();
        for (ObjType type : DataManager.getTypes(TYPE)) {
            if (type.getIntParam(PARAMS.CIRCLE) > circle)
                continue;
            if (groups != null)
                if (!groups.contains(type.getGroupingKey()))
                    continue;
            String descr = type.getDescription();
            if (descr.isEmpty()) {
                list.add(type);
                continue;
            }
            if (descr.contains("."))
                continue;
            if (descr.contains("{") && descr.contains("}"))
                continue;

            incomplete.add(type);
        }
        String text = "";
        String suffix = "";
        if (groups != null)
            suffix = groups;

        String prefix = "missing descr";
        writeDescrReqFile(list, circle, suffix, prefix, false, appendLogic);

        prefix = "incomplete descr";
        writeDescrReqFile(incomplete, circle, suffix, prefix, true, appendLogic);

    }

    private static void writeDescrReqFile(List<ObjType> list, int circle, String suffix,
                                          String prefix, boolean incomplete, boolean appendLogic) {
        String text = "";
        for (ObjType sub : list) {
            text += descrHeaderSeparator + sub.getName() + descrHeaderSeparator
                    + StringMaster.NEW_LINE;
            if (incomplete)
                text += sub.getProperty(G_PROPS.DESCRIPTION) + StringMaster.NEW_LINE;

            if (appendLogic)
                text += sub.getProperty(G_PROPS.PASSIVES) + StringMaster.NEW_LINE;
        }
        if (appendLogic)
            suffix += " with logic";
        String filepath = PathFinder.getTextPath() + prefix + "" + suffix + " up to " + circle
                + " circle.txt";
        FileManager.write(text, filepath);
    }

    private static void extractTypeText() {
        DC_Engine.init(false);
        int i = 0;
        for (OBJ_TYPE k : extractedTypes) {
            List<String> value = null;
            if (extractedTypeGroups.length > i)
                value = StringMaster.openContainer(extractedTypeGroups[i]);
            extractedTypesMap.put(k, value);
            i++;
        }

        for (PROPERTY prop : extract_props)
            for (OBJ_TYPE t : extractedTypesMap.keySet()) {

                List<String> list = extractedTypesMap.get(t);
                if (list == null)
                    extractTypeText(t, null, prop);
                else
                    for (String c : list) {
                        extractTypeText(t, c, prop);
                    }
            }

    }

    private static void extractTypeText(OBJ_TYPE t, String group, PROPERTY prop) {
        String content = "";
        String filepath = getPropsPath();
        for (ObjType type : DataManager.getTypes(t)) {
            if (group != null) {

            }
            content += getDescriptionOpener(type.getName()) + type.getProperty(prop)

            ;

        }
        FileManager.write(content, filepath + t.getName() + " " + prop.getName() + " merge.txt");
    }

    public static void init(String lang) {
        if (lang != null)
            locale = lang;
        // String testdata = FileManager.readFile(getCompendiumPath() +
        // "l5.odt");
        // for (String prop : props) {
        // initEntityPropText(prop);
        // }
        // initValueDescriptions();

    }

    public static void initValueDescriptionsFolder() {
        for (PARAMS p : PARAMS.values()) {
            String path = getParamsPath();
            String name = p.getName();
            if (p.isMastery())
                path += "mastery\\";
            if (p.isAttribute())
                path += "attributes\\";
            if (p.isDynamic())
                name = name.replace("c ", "");
            String descr = FileManager.readFile(path + name);
            if (!descr.isEmpty())
                p.setDescr(descr);
        }
        for (PROPS p : PROPS.values()) {
            String path = getPropsPath();
            String name = p.getName();
            if (p.isPrinciple())
                path += "principles\\";
            String descr = FileManager.readFile(path + name);
            if (!descr.isEmpty())
                p.setDescr(descr);
        }
    }

    public static void writeAllToFolder() {
        for (ATTRIBUTE p : ATTRIBUTE.values()) {
            FileManager.write(p.getParameter().getDescription(), getParamsPath() + "attributes\\"
                    + p.toString() + ".txt");
        }
        for (PRINCIPLES p : PRINCIPLES.values()) {
            FileManager.write(p.getDescription(), getPropsPath() + "principles\\" + p.toString()
                    + ".txt");
        }
    }

    public static void initEntityPropText(String prop) {
        String filepath = getTextPath() + "types\\" + prop + "\\";
        for (File dir : FileManager.getFilesFromDirectory(filepath, true)) {
            if (!dir.isDirectory())
                continue;
            OBJ_TYPES T = OBJ_TYPES.getType(dir.getName());
            for (File file : FileManager.getFilesFromDirectory(filepath + dir.getName(), false)) {

                ObjType type = DataManager.getType(file.getName(), T);
                if (type != null)
                    type.setProperty(prop, FileManager.readFile(file));

            }
        }
    }

    public static void main(String[] args) {
        PathFinder.init();

        generateMissingDescrTemplate();
        // processDescriptionFile(FileManager.getFile("X:\\Dropbox\\" +
        // "FocusWriting\\2016-7\\"
        // + "param descr.odt"));
        // extractTypeText();
        // merge();
    }

    private static void merge() {
        String mergeContents = "";
        for (File f : FileManager.getFilesFromDirectory("X:\\Dropbox\\"
                + "FocusWriting\\2016-Lore\\", true)) {
            mergeContents += getOdtDescriptionFilesContents(f.getPath());
        }

        FileManager.write(mergeContents, "X:\\Dropbox\\FocusWriting\\2016-Lore\\" + "descr merge "
                + TimeMaster.getFormattedTimeAlt(false) + ".txt");
    }

    public static void processDescriptionFile(File file) {

        String content = FileManager.readFile(file);
        String path = file.getPath().replace(file.getName(), "");
        for (String description : content.split(getDescriptionSeparator())) {
            String name = content.split(getNameSeparator())[0];
            description = description.replace(name, "");
            FileManager.write(description, path + "\\" + name + ".txt");
        }

    }

    private static String getDescriptionOpener(String name) {
        return getDescriptionSeparator() + name + getNameSeparator();
    }

    private static String getOdtSpecialFilesContents(String path) {
        String contents = "";
        for (File f : FileManager.getFilesFromDirectory(path, false)) {

            if (checkSpecialFileName(f.getName()))
                contents += getNameSeparator() + f.getName() + " " + getNameSeparator() + "\n"
                        + FileManager.readFile(f);

        }
        return contents;
    }

    private static String getOdtDescriptionFilesContents(String path) {
        String contents = "";
        for (File f : FileManager.getFilesFromDirectory(path, false)) {
            if (checkDescrFileName(f.getName()))
                contents += getDescriptionFileSeparator() + f.getName() + " "
                        + getDescriptionFileSeparator() + "\n" + FileManager.readFile(f);

        }
        return contents;
    }

    private static boolean checkDescrFileName(String name) {
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("dscr"))
            return true;
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("d"))
            return true;
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("dcr"))
            return true;
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("descr"))
            return true;
        if (StringMaster.getStringBeforeNumeralsAndSymbols(name).equalsIgnoreCase("description"))
            return true;
        return false;
    }

    private static boolean checkSpecialFileName(String name) {
        if (checkDescrFileName(name))
            return false;
        String stdFileNames = "l,r,a,re,j,mr,dr,e,i,wr,m,fw,q,gr,meta,s,b,wb,mc,mm,";
        String beforeNumeralsAndSymbols = StringMaster.getStringBeforeNumeralsAndSymbols(name);
        if (beforeNumeralsAndSymbols.isEmpty())
            return false;
        for (String substring : StringMaster.openContainer(stdFileNames, ",")) {
            if (beforeNumeralsAndSymbols.equalsIgnoreCase(substring))
                return false;
        }
        if (beforeNumeralsAndSymbols.contains("dev"))
            return false;
        if (beforeNumeralsAndSymbols.contains("meta"))
            return false;
        if (beforeNumeralsAndSymbols.contains("re"))
            return false;
        return true;
    }

    public static void mergeOdtFiles(String path, String prefix, String newName, boolean recursive) {

        String mergeContents = "";
        // path= "";
        String contents = getOdtAllFileContents(path, prefix);
        mergeContents += contents;
        FileManager.write(mergeContents, path + newName + " merge " + TimeMaster.getFormattedDate()
                + ".txt");

    }

    private static String getOdtAllFileContents(String path, String prefix) {
        return getOdtAllFileContents(path, prefix, null);
    }

    private static String getOdtAllFileContents(String path, String prefix, Integer lengthLimit) {
        String contents = "";
        for (File f : FileManager.getFilesFromDirectory(path, false)) {

            if (StringMaster.isEmpty(prefix) || f.getName().startsWith(prefix))
                if (lengthLimit == null || f.getName().length() < lengthLimit)
                    contents += getNameSeparator() + f.getName() + " " + getNameSeparator() + "\n"
                            + FileManager.readFile(f);

        }
        return contents;
    }

    private static String getDescriptionFileSeparator() {
        return "*** ";
    }

    private static String getNameSeparator() {
        return ":: ";
    }

    private static String getDescriptionSeparator() {
        return "<>";
    }

    public static void writeEntityTextToFolder(String prop) {
        String filepath = getTextPath() + "types\\" + prop + "\\";
        for (String t : XML_Reader.getTypeMaps().keySet()) {
            Map<String, ObjType> map = XML_Reader.getTypeMaps().get(t);
            for (String name : map.keySet()) {
                ObjType type = map.get(name);
                String content = type.getProperty(prop);
                FileManager.write(content, filepath + name + ".txt");
            }

        }
    }

    public static String getParamsPath() {
        return getTextPath() + "parameters\\";
    }

    public static String getPropsPath() {
        return getTextPath() + "properties\\";
    }

    public static String getCompendiumPath() {

        return getTextPath() + "compendium\\";
    }

    public static String getTextPath() {
        return PathFinder.getEnginePath() + PathFinder.getTextPath() + locale + "\\";
    }

}