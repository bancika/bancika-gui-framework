package org.diylc.swingframework;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressDialog extends ButtonDialog {

	private static final long serialVersionUID = 1L;
	private String description;
	private boolean useProgress;

	private JPanel mainPanel;
	private JProgressBar progressBar;

	public ProgressDialog(JFrame owner, String title, String[] buttonCaptions, String description,
			boolean useProgress) {
		super(owner, title, buttonCaptions);
		this.description = description;
		this.useProgress = useProgress;

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		layoutGui();
	}

	public void setProgress(int value) {
		getProgressBar().setValue(value);
	}

	@Override
	protected JComponent getMainComponent() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new GridBagLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(2, 2, 2, 2);

			gbc.gridx = 0;
			gbc.gridy = 0;
			mainPanel.add(new JLabel(description), gbc);

			gbc.gridy = 1;
			mainPanel.add(getProgressBar(), gbc);
		}
		return mainPanel;
	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(!useProgress);
		}
		return progressBar;
	}
}
