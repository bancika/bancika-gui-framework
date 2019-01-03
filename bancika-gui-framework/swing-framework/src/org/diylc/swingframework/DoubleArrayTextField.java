package org.diylc.swingframework;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.diylc.appframework.images.IconLoader;

/**
 * {@link JTextField} adapted to display {@link Double}. Use {@link #getValue()}
 * and {@link #setValue(Double)} to read and write.
 * 
 * @author Branislav Stojkovic
 */
public class DoubleArrayTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	private static final Format format = new DecimalFormat("0.#####");
	public static final String VALUE_PROPERTY = "DoubleValue";

	private Double[] value;
	private JLabel errorLabel;

	// private static ScriptEngineManager factory = new ScriptEngineManager();
	// private static ScriptEngine engine =
	// factory.getEngineByName("JavaScript");

	private boolean ignoreChanges = false;

	public DoubleArrayTextField(Double value[]) {
		this();
		setValue(value);
	}

	public DoubleArrayTextField() {
		super();
		setLayout(new BorderLayout());
		errorLabel = new JLabel(IconLoader.Warning.getIcon());
		errorLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		add(errorLabel, BorderLayout.EAST);
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				textChanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				textChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textChanged();
			}
		});
	}

	public Double[] getValue() {
		return value;
	}

	public void setValue(Double[] value) {
		firePropertyChange(VALUE_PROPERTY, this.value, value);
		this.value = value;
		ignoreChanges = true;
		errorLabel.setVisible(value == null);
		try {
			StringBuilder b = new StringBuilder();
			for (Double v : value) {
				if (b.length() > 0)
					b.append(" / ");
				b.append(format.format(v));
			}			
			setText(value == null ? "" : b.toString());
		} finally {
			ignoreChanges = false;
		}
	}

	private void textChanged() {
		if (!ignoreChanges) {
			try {
				Double[] newValue;
				if (getText().trim().isEmpty()) {
					newValue = null;
				} else {
					String[] parts = getText().trim().split("/");
					List<Double> items = new ArrayList<Double>();
					for (String part : parts) {
						Object parsed = format.parseObject(part.trim());
						if (parsed instanceof Long) {
							items.add(((Long) parsed).doubleValue());
						} else if (parsed instanceof Integer) {
							items.add(((Integer) parsed).doubleValue());
						} else if (parsed instanceof Double) {
							items.add((Double) parsed);
						} else if (parsed instanceof Float) {
							items.add(((Float) parsed).doubleValue());
						} else {
							throw new RuntimeException(
									"Unrecognized data type: "
											+ parsed.getClass().getName());
						}
					}
					newValue = items.toArray(new Double[0]);
				}
				firePropertyChange(VALUE_PROPERTY, this.value, newValue);
				this.value = newValue;
				errorLabel.setVisible(false);
			} catch (Exception e) {
				e.printStackTrace();
				this.value = null;
				errorLabel.setVisible(true);
			}
		}
	}
}
