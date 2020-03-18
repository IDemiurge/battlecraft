package main.level_editor.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import main.system.threading.WaitMaster;

import java.util.Arrays;
import java.util.Collection;

public abstract class ChooserDialog<T, T1 extends Actor> extends ValueTable<T, T1> {

    public static final WaitMaster.WAIT_OPERATIONS SELECTION = WaitMaster.WAIT_OPERATIONS.SELECTION;
    private T selected;
    private TablePanel scrolledTable;
    private ScrollPane scroll;

    public ChooserDialog(int wrap, int size) {
        super(wrap, size);
        setVisible(false);
        setBackground(NinePatchFactory.getLightPanelFilledDrawable());
    }

    @Override
    protected T1 createElement(T datum) {
        T1 e = createElement_(datum);
        e.addListener(createItemSelectListener(e, datum));
        return e;
    }

    @Override
    protected TablePanel getContentTable() {
        if (isScrolled()){
            return getScrolledTable();
        }
        return super.getContentTable();
    }

    protected boolean isScrolled() {
        return false;
    }

    @Override
    public void clearChildren() {
        super.clearChildren();
        if (scroll!=null ){
            add(scroll);
        }
    }

    @Override
    public void init() {
        super.init();

        if (isInstaOk()) {
            return;
        }
        row();
        addNormalSize(new SmartButton(ButtonStyled.STD_BUTTON.OK, () -> ok())).left() ;
        addNormalSize(new SmartButton(ButtonStyled.STD_BUTTON.CANCEL, () -> cancel())).right();
    }

    protected abstract T1 createElement_(T datum);

    private void cancel() {
        chosen(null);
        close();
    }
    private void ok() {
        chosen(selected);
        close();
    }

    private void chosen(T selected) {
        WaitMaster.receiveInput(SELECTION, selected);
    }

    private void close() {
        fadeOut();
    }

    public T choose(T[] from) {
       return  choose(Arrays.asList(from));
    }
    public T choose(Collection<T> from) {
        show();
        setUserObject(from);
        T res = (T) WaitMaster.waitForInput(SELECTION);
        close();
        return res;
    }

    protected EventListener createItemSelectListener(T1 actor, T item) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                selected = item;
                if (isInstaOk())
                {
                    ActionMaster.addScaleAction(actor, 0, 1f);
                    ok();
                }
            }
        };
    }

    protected boolean isInstaOk() {
        return false;
    }

    private void show() {
        fadeIn();
    }


    public TablePanel getScrolledTable() {
        if (scrolledTable == null) {
            scrolledTable = new TablePanelX();
            scroll = new ScrollPane ( scrolledTable);
            add(scroll);
        }
        return scrolledTable;
    }

}