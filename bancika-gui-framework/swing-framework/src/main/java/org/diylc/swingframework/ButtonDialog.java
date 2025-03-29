package org.diylc.swingframework;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public abstract class ButtonDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  public static final String OK = "OK";
  public static final String CANCEL = "Cancel";

  private JPanel containerPanel;
  private JPanel buttonPanel;

  private String[] buttonCaptions;

  private Map<String, JButton> buttonMap;

  private String selectedButtonCaption;

  private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  public static final String dispatchWindowClosingActionMapKey = "org.diylc.swingframework.dispatch:WINDOW_CLOSING";

  public static void installEscapeCloseOperation(final JDialog dialog) {
    Action dispatchClosing = new AbstractAction() {

      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent event) {
        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
      }
    };
    JRootPane root = dialog.getRootPane();
    root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
    root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
  }

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
      if (!buttonMap.isEmpty()) {
        getRootPane().setDefaultButton(buttonMap.get(buttonCaptions[0]));
      }
    }
    return buttonPanel;
  }

  abstract protected JComponent getMainComponent();

  protected boolean validateInput(String button) {
    return true;
  }
}
