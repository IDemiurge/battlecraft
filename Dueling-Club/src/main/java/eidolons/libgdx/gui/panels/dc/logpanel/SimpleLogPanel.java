package eidolons.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;

public class SimpleLogPanel extends LogPanel {
    private Actor extendButton;

    public SimpleLogPanel() {
        super();

        extendButton = new SmartButton(STD_BUTTON.PULL);
        addActor(extendButton);
        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2,
         getHeight() + 1);
        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2-1,
         getHeight()  );

        extendButton.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float max = GdxMaster.adjustHeight(800);
                float min = GdxMaster.adjustHeight(150);
                float val = getHeight() + y;
                if (val>max)
                    return;
                setHeight(Math.min(Math.max(getHeight() + y, GdxMaster.adjustHeight(150)),
                 GdxMaster.adjustHeight(800)));
                extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2-1,
                 getHeight()  );
                updatePos = true;
            }
        });
        updateAct();
    }

    @Override
    protected void updateAct() {
        super.updateAct();
        extendButton.setSize(55, 11);
        extendButton.setZIndex(Integer.MAX_VALUE);
//        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2, getHeight() - movableHeader.getHeight() + 4);
    }
}
