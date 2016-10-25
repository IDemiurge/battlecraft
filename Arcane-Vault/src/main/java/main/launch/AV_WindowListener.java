package main.launch;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import main.gui.components.controls.ModelManager;

public class AV_WindowListener implements WindowListener {
	private boolean cancelled = false;
	private JFrame window;

	public AV_WindowListener(JFrame window) {
		this.window = window;
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (ArcaneVault.isDirty()) {
			promptForSave();
		}
		if (!cancelled) {
			window.dispose();
			System.exit(0);
		}

		cancelled = false;
	}

	private void promptForSave() {

		int result = JOptionPane.showConfirmDialog(window, "Save?");
		if (result == JOptionPane.YES_OPTION) {
			ModelManager.saveAll();
		} else if (result == JOptionPane.CLOSED_OPTION
				|| result == JOptionPane.CANCEL_OPTION) {
			cancelled = true;

		}

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
