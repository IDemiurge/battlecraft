package main.libgdx.gui.dialog;

import main.libgdx.gui.layout.LayoutParser;
import main.libgdx.old.generic.Container;

/**
 * Created by JustMe on 1/5/2017.
 */
public abstract class Dialog extends Container {
    public Dialog(String imagePath) {
        super(imagePath);
    }

    public Dialog(String bgPath, LayoutParser.LAYOUT layout) {
        super(bgPath, layout);
    }
    //block other controls while active


}
