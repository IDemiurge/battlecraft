package main.utilities.sorting;

import main.swing.generic.components.G_CompHolder;
import main.swing.generic.components.G_Panel;
import main.utilities.sorting.TypeSorter.SORT_BY;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

public class SortBox extends G_CompHolder implements ActionListener {

    private static final String TEXT = "Sort by:";
    private Vector<SORT_BY> items;
    private TypeSorter sorter;

    public SortBox() {
        super();
        this.sorter = new TypeSorter();
    }

    public void initComp() {
        comp = new G_Panel();
        JLabel lbl = new JLabel(TEXT);
        comp.add(lbl);
        initItems();
        JComboBox<SORT_BY> dropbox = new JComboBox<>(items);
        dropbox.addActionListener(this);
        comp.add(dropbox, "");
    }

    private void initItems() {
        items = new Vector<>(Arrays.asList(SORT_BY.values()));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
//		sorter.appendLast(items.getOrCreate(((JComboBox<SORT_BY>) e.getSource())
//				.getSelectedIndex()));
    }
}
