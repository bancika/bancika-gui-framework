package org.diylc.swingframework.update;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.diylc.appframework.miscutils.Utils;


class UpdateDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JEditorPane htmlLabel;

	private String htmlText;
	private String latestVersionUrl;

	public UpdateDialog(JComponent owner, String htmlText, String latestVersionUrl) {
		super(SwingUtilities.getWindowAncestor(owner));
		this.htmlText = htmlText;
		this.latestVersionUrl = latestVersionUrl;
		setTitle("Update Details");

		setModal(true);

		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		holderPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		holderPanel.add(new JLabel("These updates are available for your computer:"),
				BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(getHtmlLabel());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		holderPanel.add(scrollPane, BorderLayout.CENTER);
		holderPanel.add(createButtonPanel(), BorderLayout.SOUTH);

		setContentPane(holderPanel);

		setPreferredSize(new Dimension(300, 400));

		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

		JButton downloadButton = new JButton("Download");
		downloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Utils.openURL(latestVersionUrl);
					UpdateDialog.this.setVisible(false);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(UpdateDialog.this,
							"Could not launch default browser. To downlaod the latest version visit "
									+ latestVersionUrl);
					Logger.getLogger(UpdateDialog.class).error("Could not launch default browser",
							e1);
				}
			}
		});
		buttonPanel.add(downloadButton);

		buttonPanel.add(Box.createHorizontalStrut(4));

		JButton cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UpdateDialog.this.setVisible(false);
			}
		});
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	public JEditorPane getHtmlLabel() {
		if (htmlLabel == null) {
			htmlLabel = new JEditorPane();
			htmlLabel.setEditable(false);
			htmlLabel.setContentType("text/html");
			htmlLabel.setText(htmlText);
			htmlLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		}
		return htmlLabel;
	}
}
