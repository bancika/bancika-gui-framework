package org.diylc.swingframework;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.diylc.appframework.miscutils.Utils;


/**
 * {@link JLabel} customized to show hyperlinks. Foreground color is set to blue
 * by default and link is underlined.
 * 
 * @author Branislav Stojkovic
 */
public class LinkLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a hyperlink with the specified url and protocol. For web page
	 * links use <br>
	 * <br>
	 * <code>protocol = "http://", url = "www.some-site.com"</code><br>
	 * <br>
	 * For email use<br>
	 * <br>
	 * <code>protocol = "mailto:", url = "somebody@some-site.com"</code> <br>
	 * <br>
	 * Other constructors should not be used as they will create a plain
	 * {@link JLabel}.
	 * 
	 * @param protocol
	 * @param url
	 */
	public LinkLabel(final String protocol, final String url) {
		super("<html><u>" + url + "</u></html>");
		setForeground(Color.blue);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Utils.openURL(protocol + url);
				} catch (Exception e1) {
					Logger.getLogger(LinkLabel.class).error("Could not launch default browser",
							e1);
				}
			}
		});
	}

	@Deprecated
	public LinkLabel() {
		super();
	}

	@Deprecated
	public LinkLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	@Deprecated
	public LinkLabel(Icon image) {
		super(image);
	}

	@Deprecated
	public LinkLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	@Deprecated
	public LinkLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	@Deprecated
	public LinkLabel(String text) {
		super(text);
	}
}
