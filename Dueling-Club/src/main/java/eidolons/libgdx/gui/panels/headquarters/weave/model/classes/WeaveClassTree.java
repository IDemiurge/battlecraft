package eidolons.libgdx.gui.panels.headquarters.weave.model.classes;

import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveTree;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveClassTree extends WeaveTree{
    private final CLASS_GROUP group;

    public WeaveClassTree(CLASS_GROUP sub) {
        group= sub;
    }


    @Override
    protected Object getRootArg() {
        return group;
    }
    @Override
    protected boolean isSkill() {
        return false;
    }
    @Override
    protected List<ObjType> initData(HqHeroDataSource userObject) {
        return  DataManager.getTypesSubGroup(DC_TYPE.SKILLS, group.name());
    }
}
