package eidolons.system.text;

import eidolons.content.values.ValuePageManager;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.system.auxiliary.Strings;
import main.system.math.Formula;
import main.system.text.TextParser;

import java.util.List;

public class TextGenerator {

    private static final String PREFIX = "Provides ";

    public static String generatePerkParamBonuses(Entity e) {

        List<List<VALUE>> pages = ValuePageManager.getValuesForHCInfoPages(e
         .getOBJ_TYPE_ENUM());

        StringBuilder stringBuilder = new StringBuilder(PREFIX);
        for (List<VALUE> list : pages) {
            boolean prop = false;
            for (VALUE v : list) {
                if (v instanceof PARAMETER) {
                    if (!ContentValsManager.isValueForOBJ_TYPE(DC_TYPE.CHARS, v)) {
                        continue;
                    }
                    String amount = e.getValue(v);
                    amount = TextParser.parse(amount, e.getRef());
                    int n = new Formula(amount).getInt(e.getRef());
                    if (n != 0) {
                        stringBuilder.append(n).append(" ").append(v.getName()).append(", ");
                    }
                } else {
                    prop = true;
                    break;
                }
            }
            if (!prop && !list.equals(pages.get(pages.size() - 1))) {
                stringBuilder.append(Strings.NEW_LINE);
            }
        }
        String string = stringBuilder.toString();
        // StringMaster.replaceLast(" ," "and
        string.substring(0, string.length() - 2);
        return string;

    }

}
