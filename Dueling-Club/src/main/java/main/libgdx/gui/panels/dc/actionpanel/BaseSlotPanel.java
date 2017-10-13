package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.Gdx;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.system.audio.DC_SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class BaseSlotPanel extends TablePanel {
    protected final int imageSize;
    private Map<PagesMod, TablePanel> modTableMap = new HashMap<>();

    private PagesMod activePage = PagesMod.NONE;

    public BaseSlotPanel(int imageSize) {
        this.imageSize = imageSize;
        left().bottom();
    }

    @Override
    public void clear() {
        super.clear();
        modTableMap.clear();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        PagesMod mod = PagesMod.NONE;

        PagesMod[] pagesMods = PagesMod.getValues();
        for (int i = 0, pagesModsLength = pagesMods.length; i < pagesModsLength; i++) {
            PagesMod pagesMod = pagesMods[i];
            if (Gdx.input.isKeyPressed(pagesMod.getKeyCode())) {
                mod = pagesMod;
            }
        }
        if (mod != activePage) {
            if (modTableMap.containsKey(mod)) {
                setActivePage(mod);
                DC_SoundMaster.playRandomStandardSound(
                 STD_SOUNDS.PAGE_TURNED,
                 STD_SOUNDS.PAGE_TURNED_ALT);
            }
        }
    }

    protected void initContainer(List<ActionValueContainer> sources, String emptyImagePath) {
        int pagesCount = sources.size() / getPageSize();

        if (sources.size() % getPageSize() > 0) {
            pagesCount++;
        }

        pagesCount = Math.min(PagesMod.values().length, pagesCount);

        for (int i = 0; i < pagesCount; i++) {
            final int indx = i * getPageSize();
            final int toIndx = Math.min(sources.size(), indx + getPageSize());
            addPage(sources.subList(indx, toIndx), emptyImagePath);
        }

        if (modTableMap.size() == 0) {
            addPage(Collections.EMPTY_LIST, emptyImagePath);
        }

        setActivePage(PagesMod.NONE);
    }

    private void addPage(List<ActionValueContainer> list, String emptyImagePath) {
        final TablePanel page = initPage(list, emptyImagePath);
        modTableMap.put(PagesMod.values()[modTableMap.size()], page);
        addElement(page).left().bottom();
        page.setVisible(false);
    }

    private void setActivePage(PagesMod page) {
        TablePanel view = modTableMap.get(activePage);
        if (view == null) {
            return ;
        }
        view.setVisible(false);
        activePage = page;
        modTableMap.get(activePage).setVisible(true);
    }

    private TablePanel initPage(List<ActionValueContainer> sources, String emptyImagePath) {
        TablePanel page = new TablePanel();
        int valuesSize = Math.min(getPageSize(), sources.size());
        for (int i = 0; i < valuesSize; i++) {
            final ActionValueContainer valueContainer = sources.get(i);
            if (imageSize > 0) {
                valueContainer.overrideImageSize(imageSize, imageSize);
            }
            page.add(valueContainer).left().bottom();
        }

        for (int i = sources.size(); i < getPageSize(); i++) {
            final ValueContainer container = new ValueContainer(getOrCreateR(emptyImagePath));
            container.overrideImageSize(imageSize, imageSize);
            page.add(container).left().bottom();
        }

        return page;
    }

    protected int getPageSize() {
        return 6;
    }
}
