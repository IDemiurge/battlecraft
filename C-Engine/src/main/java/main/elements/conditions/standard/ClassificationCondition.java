package main.elements.conditions.standard;

import main.content.CONTENT_CONSTS.CLASSIFICATIONS;
import main.content.properties.G_PROPS;
import main.elements.conditions.ConditionImpl;
import main.elements.conditions.StringComparison;
import main.elements.conditions.StringContainersComparison;

public class ClassificationCondition extends ConditionImpl {

    private final static String comparison = "{MATCH_"
            + G_PROPS.CLASSIFICATIONS + "}";
    private StringComparison c;

    public ClassificationCondition(String classification, String comparison) {
        this.c = new StringContainersComparison(true, "{" + comparison + "_"
                + G_PROPS.CLASSIFICATIONS + "}", classification, false);
    }

    public ClassificationCondition(String classification) {
        this(classification, comparison);
    }

    public ClassificationCondition(CLASSIFICATIONS classification) {
        this(classification.name());
    }

    @Override
    public boolean check() {
        return c.check(ref);
    }

    @Override
    public String toString() {
        return c.toString();
    }
}