package main.game.module.adventure.gui.party;

import main.content.PARAMS;
import main.content.VALUE;
import main.content.values.parameters.MACRO_PARAMS;
import main.entity.obj.unit.Unit;
import main.swing.components.panels.DC_ItemPanel;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.GuiManager;

import java.util.Arrays;
import java.util.List;

public class HeroSlidePanel extends G_PagePanel<Object> {

    private static final VALUE[] HERO_HEADER_VALUE_LIST = {
            MACRO_PARAMS.TRAVEL_SPEED, MACRO_PARAMS.CONSUMPTION,
            PARAMS.DETECTION, PARAMS.STEALTH,};
    Unit hero;

    public HeroSlidePanel(Unit hero2) {
        super(0, false, 5);
        this.hero = hero2;
    }

    public static int width() {
        return 3 * GuiManager.getSmallObjSize();
    }

    public HERO_SLIDE_VIEWS getCurrentSlideView() {
        return HERO_SLIDE_VIEWS.values()[getCurrentIndex()];
    }

    @Override
    public int getPanelHeight() {
        return VISUALS.PORTRAIT_BORDER.getHeight();
    }

    @Override
    public int getPanelWidth() {

        return width();
    }

    protected void addControls() {
        backButton = getButton(false);
        forwardButton = getButton(true);
        addControl(backButton, false, 0, 0);
        addControl(forwardButton, true,
                getPanelWidth() - backButton.getImageWidth(), 0);
        // TODO string in between
        String text = hero.getName()
                + "'s "
                + StringMaster.getWellFormattedString(getCurrentSlideView()
                .toString());
        // if (text.length()>30) text = "";
        add(new TextCompDC(null, text, 17), "pos " + CONTROLS_POS + ".x2-20 "
                + "0");
    }

    protected String getPagePos() {
        return "pos 0 " + CONTROLS_POS + ".y2";
    }

    @Override
    protected void resetData() {
        super.resetData();
        setDirty(true);
    }

    @Override
    protected G_Component createPageComponent(List<Object> list) {
        switch (getCurrentSlideView()) {
            case CLASSES:
                return new HeroObjList<>(hero.getClasses(), hero);
            case INVENTORY:
                return new HeroObjList<>(hero.getInventory(),
                        hero);
            case QUICK_ITEMS:
                return new HeroObjList<>(hero.getQuickItems(),
                        hero);
            case SPELLBOOK:
                return new HeroObjList<>(hero.getSpells(), hero);
            case ACTIONS:
                // dismiss, talk, order
                break;
            case PARAMETERS:
                // = new ValueIconPanel(hero)
                Header header = new Header(
                        Arrays.asList(HERO_HEADER_VALUE_LIST), hero);
                header.refresh();
                return header;
            case STATUS:
                HeroBarPanel bars = new HeroBarPanel();
                bars.setObj(hero);
                bars.refresh();
                return bars.getPanel();
            case ITEMS:
                // new G_Panel("flowy", items, stuff);
                DC_ItemPanel items = new DC_ItemPanel(hero.getGame());
                // jewelry? quick item button? open inventory? item-info
                // (gold/weight/...)
                items.setObj(hero);
                items.refresh();
                return items;
            default:
                break;
        }
        return null;
    }

    @Override
    protected List<List<Object>> getPageData() {
        return new ListMaster().splitList(1,
                Arrays.asList(HERO_SLIDE_VIEWS.values()));
    }

    public enum HERO_SLIDE_VIEWS {
        PARAMETERS,
        STATUS,
        ITEMS,
        ACTIONS,
        CLASSES,
        INVENTORY,
        QUICK_ITEMS,
        SPELLBOOK,
    }
}