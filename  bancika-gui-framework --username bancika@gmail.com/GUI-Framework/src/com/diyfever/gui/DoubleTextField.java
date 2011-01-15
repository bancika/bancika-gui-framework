package com.diyfever.gui;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.Format;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.diyfever.gui.images.IconLoader;

/**
 * {@link JTextField} adapted to display {@link Double}. Use {@link #getValue()}
 * and {@link #setValue(Double)} to read and write.
 * 
 * @author Branislav Stojkovic
 */
public class DoubleTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	private static final Format format = new DecimalFormat();
	public static final String VALUE_PROPERTY = "DoubleValue";
	
	private Double value;
	private JLabel errorLabel;
	
	private static ScriptEngineManager factory = new ScriptEngineManager();
	private static ScriptEngine engine = factory.getEngineByName("JavaScript");

	private boolean ignoreChanges = false;

	public DoubleTextField(Double value) {
		this();
		setValue(value);
	}

	public DoubleTextField() {
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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		firePropertyChange(VALUE_PROPERTY, this.value, value);
		this.value = value;
		ignoreChanges = true;
		errorLabel.setVisible(value == null);
		try {
			setText(value == null ? "" : format.format(value));
		} finally {
			ignoreChanges = false;
		}
	}

	private void textChanged() {
		if (!ignoreChanges) {
			try {
				Double newValue = (Double) engine.eval(getText());
				firePropertyChange(VALUE_PROPERTY, this.value, newValue);
				this.value = newValue;
				errorLabel.setVisible(false);
			} catch (Exception e) {
				this.value = null;
				errorLabel.setVisible(true);
			}
		}
	}
}
