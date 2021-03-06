package libgdx.gui.panels.headquarters.creation.selection.misc;

import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.panels.headquarters.creation.HcElement;
import libgdx.gui.panels.headquarters.creation.HcHeroModel;
import libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.content.consts.Images;
import libgdx.gui.generic.btn.ButtonStyled;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/10/2018.
 */
public class HcFullPortraitPanel extends HcElement {
    private final FadeImageContainer portrait;
    private List<String> pool;
    private List<String> fullPool;
    private int index = -1;
    private String current;

    public HcFullPortraitPanel() {
        add(new SmartTextButton("Previous", ButtonStyled.STD_BUTTON.MENU, this::previous));
        add(new SmartTextButton("Next", ButtonStyled.STD_BUTTON.MENU, this::next)).row();
        add(new ImageContainer(Images.SEPARATOR)).colspan(2).row();
        add(portrait = new FadeImageContainer(getDefaultPortrait()) {
            @Override
            protected float getFadeInDuration() {
                return 1;
            }

            @Override
            protected float getFadeOutDuration() {
                return 1;
            }
        }).colspan(2).row();
        add(new ImageContainer(Images.SEPARATOR)).colspan(2).row();
        add(new SmartTextButton("Random", ButtonStyled.STD_BUTTON.MENU, this::random)).colspan(2);

       update();
    }

    private void random() {
        if (fullPool == null) {
            fullPool = initPool(true);
        }
        current = new RandomWizard<String>()
         .getRandomListItem(fullPool);
        selected(current);
    }

    public void update() {
        pool = initPool(false);
        index = -1;
        portrait.setImage(getDefaultPortrait());
    }

    private void next() {
        index++;
        if (pool.size()==index)
            index=0;
        current = pool.get(index);
        selected(current);

    }

    private void previous() {
        index--;
        if ( index<0)
            index=pool.size()-1;
        current = pool.get(index);
        selected(current);

    }

    private void selected(String current) {
        portrait.setImage(current);
        this.current=StringMaster.cropLast(current, " full") ;
        HeroCreationMaster.modified(G_PROPS.IMAGE, this.current);
    }
    private String getDefaultPortrait() {
        return getImagePath(FileManager.getFile(getPath() + "default.jpg"));
    }

    private String getPath() {
        return PathFinder.getPortraitPath();
    }

    @Override
    public void setUserObject(Object userObject) {

    }

    private List<String> initPool(boolean full) {
        List<File> files =
         full
          ?  FileManager.getFilesFromDirectory(getPath(), false, true)
          :  FileManager.getFilesFromDirectory(getFullPath(HeroCreationMaster.getModel()), false, false);
        files.removeIf(file -> {
            if ((HeroCreationMaster.getModel().getGender() == GENDER.FEMALE)
             != file.getName().toLowerCase().contains("w_")) {
                return true;
            }
            return !StringMaster.cropFormat(file.getName()).endsWith(" full");
        });
        return files.stream().map(this::getImagePath).collect(Collectors.toList());
    }

    private String getImagePath(File file) {
        return file.getPath().toLowerCase().replace(PathFinder.getImagePath().toLowerCase(), "");
    }

    private String getFullPath(HcHeroModel model) {
     return StrPathBuilder.build(
         getPath(), model.getRace().toString()
     );
    }

}
