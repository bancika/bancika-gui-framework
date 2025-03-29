package org.diylc.swingframework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;
	private JPanel buttonPanel;

	private final String appName;
	private final Icon icon;
	private final String version;
	private final String author;
	private final String url;
	private final String mail;
	private final String htmlContent;

	public AboutDialog(JFrame parent, String appName, Icon icon, String version, String author,
			String url, String mail, String htmlContent) {
		super(parent, "About");
		this.appName = appName;
		this.icon = icon;
		this.version = version;
		this.author = author;
		this.url = url;
		this.mail = mail;
		this.htmlContent = htmlContent;

		setModal(true);
		setResizable(false);
		setLayout(new BorderLayout());
		add(getMainPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);

		setPreferredSize(new Dimension(320, 240));

		pack();
		setLocationRelativeTo(parent);
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new GridBagLayout());
			mainPanel.setBackground(Color.white);
			mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(10, 8, 10, 4);

			JLabel iconLabel = new JLabel(icon);

			gbc.gridheight = 2;
			gbc.weightx = 0;
			gbc.weighty = 0;
			mainPanel.add(iconLabel, gbc);

			JLabel appNameLabel = new JLabel(appName);
			appNameLabel.setForeground(Color.red.darker());
			appNameLabel.setFont(appNameLabel.getFont().deriveFont(18f).deriveFont(Font.BOLD));

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridheight = 1;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(10, 4, 0, 4);
			mainPanel.add(appNameLabel, gbc);

			JLabel versionLabel = new JLabel("version " + version);
			versionLabel.setForeground(Color.lightGray);
			versionLabel.setFont(versionLabel.getFont().deriveFont(Font.BOLD));

			gbc.gridy = 1;
			gbc.insets = new Insets(0, 4, 8, 4);
			mainPanel.add(versionLabel, gbc);

			JLabel authorLabel = new JLabel(author);

			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(1, 8, 1, 4);
			mainPanel.add(authorLabel, gbc);

			JLabel urlLabel = new LinkLabel("http://", url);

			gbc.gridy = 3;
			mainPanel.add(urlLabel, gbc);

			JLabel mailLabel = new LinkLabel("mailto:", mail);

			gbc.gridy = 4;
			mainPanel.add(mailLabel, gbc);

			String html = "<head><title>About</title>" + "<style type=\"text/css\">"
					+ "body {font-family: Tahoma; font-size: 8.5px; } </style></head><body>"
					+ htmlContent + "</body></html>";
			JEditorPane editorPane = new JEditorPane("text/html", html);
			editorPane.setEditable(false);
			editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBorder(null);

			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridx = 0;
			gbc.gridy = 5;
			gbc.insets = new Insets(8, 4, 1, 4);
			mainPanel.add(scrollPane, gbc);
		}
		return mainPanel;
	}

	public JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					AboutDialog.this.setVisible(false);
				}
			});

			buttonPanel.add(closeButton);
		}
		return buttonPanel;
	}
}
