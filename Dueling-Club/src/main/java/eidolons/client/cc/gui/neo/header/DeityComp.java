package eidolons.client.cc.gui.neo.header;

import eidolons.entity.obj.unit.Unit;
import main.content.values.properties.G_PROPS;
import main.swing.generic.components.G_CompHolder;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DeityComp extends G_CompHolder implements MouseListener {
    final static VISUALS V = VISUALS.DEITY;
    private static final String X = "53";
    private static final String Y = "12";
    int emblemSize = 32;

    boolean faithless;
    private Unit hero;
    private HeroHeader header;

    public DeityComp(HeroHeader header, Unit hero) {
        this.header = header;
        this.hero = hero;
        // TODO display description if selected? Aye!

        comp = new G_Panel(V);
        refresh();

    }

    @Override
    public void refresh() {
        comp.removeAll();
        JLabel deityLbl;
        // if (hero.getDeity() == null) {
        ImageIcon icon = ImageManager.getIcon(hero.getProperty(G_PROPS.EMBLEM));

        if (!ImageManager.isValidIcon(icon)) {
            if (hero.getDeity() != null) {
                icon = (ImageManager.getIcon(hero.getDeity().getType().getProperty(G_PROPS.EMBLEM)));
            } else {
                icon = ImageManager.getIcon(STD_IMAGES.DEATH.getPath());
            }
        }
        deityLbl = new JLabel(icon);

        deityLbl.addMouseListener(this);
        comp.add(deityLbl, "pos " + X + " " + Y);
        comp.revalidate();
        comp.repaint();
    }

    public void handleClick() {
        header.deityClicked();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        handleClick();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
