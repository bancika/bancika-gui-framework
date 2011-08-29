package org.diylc.swingframework;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class ButtonDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static final String OK = "OK";
	public static final String CANCEL = "Cancel";

	private JPanel containerPanel;
	private JPanel buttonPanel;

	private String[] buttonCaptions;

	private Map<String, JButton> buttonMap;

	private String selectedButtonCaption;

	public ButtonDialog(JFrame owner, String title, String[] buttonCaptions) {
		super(owner, title);

		setModal(true);
		setResizable(false);

		this.buttonCaptions = buttonCaptions;

		this.buttonMap = new HashMap<String, JButton>();
	}

	protected void layoutGui() {
		setContentPane(getContainerPanel());
		pack();
		setLocationRelativeTo(getParent());
	}

	public JButton getButton(String caption) {
		return buttonMap.get(caption);
	}

	public String getSelectedButtonCaption() {
		return selectedButtonCaption;
	}

	private JPanel getContainerPanel() {
		if (containerPanel == null) {
			containerPanel = new JPanel(new BorderLayout());
			containerPanel.add(getMainComponent(), BorderLayout.CENTER);
			containerPanel.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return containerPanel;
	}

	public JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			for (String caption : buttonCaptions) {
				final String command = caption;
				JButton button = new JButton(caption);
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (validateInput(command)) {
							selectedButtonCaption = command;
							ButtonDialog.this.setVisible(false);
						}
					}
				});
				buttonPanel.add(button);
				buttonMap.put(command, button);
			}
		}
		return buttonPanel;
	}

	abstract protected JComponent getMainComponent();

	protected boolean validateInput(String button) {
		return true;
	}
}
