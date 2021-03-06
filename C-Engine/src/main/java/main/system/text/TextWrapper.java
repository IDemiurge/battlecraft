package main.system.text;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import main.entity.Ref;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextWrapper {

    public static String[] wrapIntoArray(String text, int wrapLength) {
        return WordUtils.wrap(text, wrapLength, Strings.NEW_LINE, true).split(Strings.NEW_LINE);
    }

    public static List<String> wrap(String text, int wrapLength) {
        return wrap(text, wrapLength, null);
    }

    public static List<String> wrap(String text, int wrapLength, Ref ref) {
        if (StringMaster.isEmpty(text)) {
            return new ArrayList<>();
        }
        text = TextParser.parse(text, ref);

        if (text.contains(Strings.NEW_LINE)) {
            ArrayList<String> list = new ArrayList<>();
            for (String subString : text.split(Strings.NEW_LINE)) {
                list.addAll(Arrays.asList(WordUtils.wrap(subString, wrapLength,
                 Strings.NEW_LINE, true).split(Strings.NEW_LINE)));
            }
            return list;
        }
        return new ArrayList<>(Arrays.asList(WordUtils.wrap(text, wrapLength,
         Strings.NEW_LINE, true).split(Strings.NEW_LINE)));
    }

    public static String wrapWithNewLine(String text, int wrapLength) {
        List<String> list = wrap(text, wrapLength);

        StringBuilder result = new StringBuilder();
        for (int j = 0; j < list.size(); j++) {
            String sub = list.get(j);
            if (j != list.size() - 1)
                result.append(sub).append(Strings.NEW_LINE);
            else result.append(sub);
        }
        return result.toString();
    }

    public static String processText(int width, String text, LabelStyle style ) {
        return processText(width, text, style, false);
    }
    public static String processText(int width, String text, LabelStyle style, boolean zigZagLines ) {
        if (text.trim().isEmpty())
            return "";
        StringBuilder newText = new StringBuilder();
        int maxLength = (int) (width / style.font.getSpaceWidth()*2/3);

        for (String substring : StringMaster.splitLines(text)) {

            if (substring.length()>maxLength)
                substring = wrapWithNewLine(substring, maxLength);
            newText.append(substring).append(Strings.NEW_LINE);
        }

        if (zigZagLines) {
            StringBuilder newTextZiggy= new StringBuilder();
            int i =0;
            for (String substring : StringMaster.splitLines(newText.toString())) {
                int n = (maxLength - substring.length()) * 2 / 3;
//                if (i++%2==1){
//                    n = n / 2;
//                }
                newTextZiggy.append(StringMaster.getWhiteSpaces(n)).append(substring).append(Strings.NEW_LINE);
            }
            return newTextZiggy.substring(0, newTextZiggy.length()-1);
        }
        return newText.substring(0, newText.length()-1);
    }
}
