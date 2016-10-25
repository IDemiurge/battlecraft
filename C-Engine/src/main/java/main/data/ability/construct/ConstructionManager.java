package main.data.ability.construct;

import main.data.ability.AE_Item;
import main.data.ability.Mapper;
import main.data.xml.XML_Converter;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class ConstructionManager {
    public static Object construct(Node node) {

        Construct construct = getConstruct(node);
        return construct(construct);

    }

    public static Object construct(Construct construct) {
        Object result = construct.construct();
        if (result instanceof Reconstructable)
            ((Reconstructable) result).setConstruct(construct);
        if (result == null) {
            return construct.construct();
        }
        return result;
    }

    private static Construct getConstruct(Node node) {
        AE_Item item = Mapper.getItem(node);
        if (item.isPrimitive()) {
            main.system.auxiliary.LogMaster.log(0, "PRIMITIVE: "
                    + node.getNodeName() + node.getTextContent());
            return new Construct(node.getNodeName(), node.getTextContent());
        }

        if (item.isENUM()) {
            main.system.auxiliary.LogMaster.log(0, "ENUM: "
                    + node.getNodeName() + node.getTextContent());
            return new Construct(node.getNodeName(), node.getTextContent(),
                    true);
        }
        return new Construct(node.getNodeName(), getConstructs(node));
    }

    private static List<Construct> getConstructs(Node node) {
        List<Construct> list = new LinkedList<Construct>();
        for (Node NODE : XML_Converter.getNodeList(node)) {
            Construct construct = getConstruct(NODE);
            list.add(construct);
        }
        return list;
    }
}