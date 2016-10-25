package main.client.cc.gui.misc;

import main.client.cc.CharacterCreator;
import main.client.cc.logic.HeroLevelManager;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.properties.G_PROPS;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HeaderPanel extends G_Panel implements MouseListener,
        ActionListener {

    private static final String SAVE = "Save";
    private static final String BACK = "Back";
    private static final String DELETE = "Delete";
    private static final String LEVEL_UP = "Level Up";
    private static final String NEW = "New";
    private static final String RENAME = "Rename";
    private static final String SAVE_AS = "Save As";
    private static final String OPEN = "Open";
    boolean editable;
    String[] controls = {SAVE, SAVE_AS, OPEN, BACK, DELETE, LEVEL_UP, RENAME,
            NEW};
    VALUE[] displayedValuesTop = {G_PROPS.RACE, G_PROPS.RANK,};
    VALUE[] displayedValuesBottom = {PARAMS.HERO_LEVEL, G_PROPS.DEITY,
            G_PROPS.PRINCIPLES,}; // classes
    private DC_HeroObj hero;
    private JLabel portrait;
    private JLabel emblem;
    private JLabel title;

    // TODO AV header page
    private G_Panel controlPanel;
    private G_Panel bottomValues;
    private G_Panel topValues;

    public HeaderPanel(DC_HeroObj hero) {
        super("flowy");
        this.hero = hero;
        refresh();
    }

    private void addComponents() {
        add(title, "id t, pos 0 0 ");
        add(topValues, "id tv, pos 0 t.y2 ");
        add(portrait, "id p, pos 0 tv.y2 ");
        add(bottomValues, "id bv, pos 0 p.y2 ");
        add(controlPanel, "id cp, pos 0 bv.y2 ");

    }

    @Override
    public void refresh() {
        // TODO optimize
        initComponents();
        removeAll();
        addComponents();
        revalidate();
        repaint();
    }

    private void initComponents() {
        this.portrait = new JLabel(hero.getIcon());
        this.title = new JLabel(hero.getName());
        topValues = initValues(displayedValuesTop, true);
        bottomValues = initValues(displayedValuesBottom, false);
        this.controlPanel = initControlPanel();

        if (editable) {
            addMouseListener();
            // addControls();
        }

    }

    private G_Panel initControlPanel() {
        G_Panel panel = new G_Panel("flowy");
        for (String c : controls) {
            JButton button = new JButton(c);
            button.setActionCommand(c);
            button.addActionListener(this);
            panel.add(button, "sg buttons");
        }
        return panel;
    }

    private G_Panel initValues(VALUE[] values, boolean top) {
        G_Panel panel = new G_Panel(
                // (top) ? "" :
                "flowy");
        for (VALUE v : values) {
            String text =

                    hero.getValue(v);
            if (!top) {
                text = v.getName() + ": " + text;
            }
            JLabel lbl = new JLabel(text);

            // if (!top)
            panel.add(lbl);

        }
        return panel;

    }

    private void addMouseListener() {
        portrait.addMouseListener(this);
        title.addMouseListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        handleCommand(((JButton) e.getSource()).getActionCommand());

    }

    private void handleCommand(String actionCommand) {
        switch (actionCommand) {
            case SAVE:
                CharacterCreator.save(CharacterCreator.getSelectedHeroType());
                break;
            case OPEN:
                ObjType type = CharacterCreator.selectHero();
                CharacterCreator.open(type);
                break;
            case SAVE_AS:
                CharacterCreator.saveAs(CharacterCreator.getSelectedHeroType());
                break;
            case BACK:
                CharacterCreator.getHeroManager().stepBack(hero);
                break;
            case DELETE:
                // ask
                break;
            case LEVEL_UP:
                // always ready?
                HeroLevelManager.levelUp(hero);
                break;
            case NEW:
                // ask save
                break;
            case RENAME:
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//		TODO change portrait on click!
// if (e.getSource() == portrait) {
//			String newImgPath = new ImageChooser().launch(GuiManager
//					.getFullObjSize(), hero.getImagePath(), this);
//			hero.setProperty(G_PROPS.IMAGE, newImgPath);
//			return;
//		}
//		if (e.getSource() == title) {
//			String newImgPath = new TextEditor().launch(GuiManager
//					.getFullObjSize(), hero.getImagePath(), this);
//			hero.setProperty(G_PROPS.IMAGE, newImgPath);
//			return;
//		}
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}