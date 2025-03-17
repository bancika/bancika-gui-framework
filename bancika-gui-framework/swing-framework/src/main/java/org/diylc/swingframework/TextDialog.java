package org.diylc.swingframework;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;


public class TextDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JEditorPane htmlLabel;

	private String htmlText;

	public TextDialog(JComponent owner, String htmlText, String title, Dimension preferredSize) {
		super(SwingUtilities.getWindowAncestor(owner));
		this.htmlText = htmlText;
		setTitle(title);

		setModal(true);

		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		holderPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
//
//		holderPanel.add(new JLabel(latestVersionUrl == null ? "Most recent updates on this computer:" : "These updates are available for your computer:"),
//				BorderLayout.NORTH);

		final JScrollPane scrollPane = new JScrollPane(getHtmlLabel());
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.);		
		holderPanel.add(scrollPane, BorderLayout.CENTER);
		holderPanel.add(createButtonPanel(), BorderLayout.SOUTH);

		setContentPane(holderPanel);

		setPreferredSize(preferredSize == null ? new Dimension(480, 400) : preferredSize);

		pack();
		setLocationRelativeTo(getOwner());
		
		SwingUtilities.invokeLater(new Runnable() {
		  
		   public void run() { 
		     scrollPane.getVerticalScrollBar().setValue(0);
		   }
		});
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));	

		JButton cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TextDialog.this.setVisible(false);
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
