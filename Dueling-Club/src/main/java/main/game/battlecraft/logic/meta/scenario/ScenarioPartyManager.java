package main.game.battlecraft.logic.meta.scenario;

import main.client.cc.logic.party.PartyObj;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.battlecraft.logic.meta.universal.PartyManager;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.text.NameMaster;

import java.util.List;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioPartyManager extends PartyManager<ScenarioMeta> {

    public ScenarioPartyManager(ScenarioMetaMaster master) {
        super(master);
    }

    @Override
    public void preStart() {
//  TODO       is this the right time to set it?

    }

    @Override
    public int getPartyLevel() {
        return getMetaGame().getMissionIndex();
    }

    @Override
    public String checkLeveledHeroVersionNeeded(String heroName) {

       int i = getMetaGame().getMissionIndex();
//        getMetaDataManager().getMissionName()
        while (i>0){
            heroName =   NameMaster.appendVersionToName(heroName, i+1);
            if (DataManager.isTypeName(heroName, DC_TYPE.CHARS))
                break;
            i--;
        }

        return super.checkLeveledHeroVersionNeeded(heroName);
    }

    @Override
    public ScenarioMetaDataManager getMetaDataManager() {
        return (ScenarioMetaDataManager) super.getMetaDataManager();
    }

    @Override
    public PartyObj initPlayerParty() {
        if (!getMaster().getMetaGame().isPartyRespawn())
            return null ;
        //preset
        //choice
        //already as Unit?
        ObjType type = getMetaGame().getScenario().getPartyType();
        chooseOneHero= CoreEngine.isExe();
        randomOneHero= !CoreEngine.isExe();
        if (type == null) {
            String string = getMetaGame().getScenario().getProperty(PROPS.SCENARIO_PARTY);
            type = new ObjType("dummy", DC_TYPE.PARTY);
            type.setProperty(PROPS.MEMBERS, string);
            //new ? choice?
        }
        if (getGame().getMetaMaster().getPartyManager().isRandomOneHero()||
         getGame().getMetaMaster().getPartyManager().isChooseOneHero()) {
            List<String> members =  StringMaster.openContainer(type.getProperty(PROPS.MEMBERS));
            if (getGame().getMetaMaster().getPartyManager().isRandomOneHero()) {
                String hero = new RandomWizard<String>().getRandomListItem(
                 members);
                type.setProperty(PROPS.MEMBERS , hero);
            } else {
                String hero = ListChooser.chooseType(
                 members, DC_TYPE.CHARS);
                type.setProperty(PROPS.MEMBERS , hero);
            }
        }
        party = new PartyObj(type);

//        if (party.getNextMission().isEmpty()) {
//            String missions = StringMaster.joinList(getMetaGame().getScenario().getAvailableMissions());
//            party.setProperty(PROPS.PARTY_MISSIONS_NEXT,
//             missions
//             StringMaster.openContainer(getMetaGame().getScenario().
//              getProperty(PROPS.SCENARIO_MISSIONS)).get(0)
//             , true);
//        }

        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));

        party.setProperty(PROPS.PARTY_MISSION,
         StringMaster.openContainer(getMetaGame().getScenario().
          getProperty(PROPS.SCENARIO_MISSIONS)).get(0), true);
        return party;

    }

}

