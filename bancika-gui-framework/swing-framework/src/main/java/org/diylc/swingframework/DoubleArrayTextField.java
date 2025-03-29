package org.diylc.swingframework;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
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
			boolean first = true;
			for (Double v : value) {
				if (first)
				  first = false;
				else 
				  b.append(" / ");
				b.append(v == null ? "" : format.format(v));
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
				int index = getText().indexOf("/");
				int start = 0;
				List<Double> items = new ArrayList<Double>();
				while (index >= 0) {
				    String part = getText().substring(start, index).trim();
				    items.add(parse(part));
					start = index + 1;
					index = getText().indexOf("/", index + 1);
				}
				// get the last part
				String part = getText().substring(start).trim();
                items.add(parse(part));
				newValue = items.toArray(new Double[0]);
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
	
	private Double parse(String part) throws ParseException {
	  if (part.isEmpty()) {
       return null;
      }
      Object parsed = format.parseObject(part);
      if (parsed instanceof Long) {
          return ((Long) parsed).doubleValue();
      } else if (parsed instanceof Integer) {
          return ((Integer) parsed).doubleValue();
      } else if (parsed instanceof Double) {
          return (Double) parsed;
      } else if (parsed instanceof Float) {
          return ((Float) parsed).doubleValue();
      } else {
          throw new RuntimeException(
                  "Unrecognized data type: "
                          + parsed.getClass().getName());
      }
	}
}
