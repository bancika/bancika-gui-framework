package org.diylc.swingframework;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CheckBoxList extends JList {
  private static final long serialVersionUID = 1L;

  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

  public CheckBoxList(CheckListItem[] items) {
    super(items);
    
    setCellRenderer(new CheckListRenderer());
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        JList list = (JList) event.getSource();
        int index = list.locationToIndex(event.getPoint());
                                                          
        if (index < 0)
          return;
        
        CheckListItem item = (CheckListItem) list.getModel()
            .getElementAt(index);
        item.setSelected(!item.isSelected()); // Toggle selected state
        list.repaint(list.getCellBounds(index, index));// Repaint cell
      }
    });
  }
  
  public static class CheckListItem {

    private Object value;
    private boolean isSelected = false;

    public CheckListItem(Object label) {
      this.value = label;
    }

    public boolean isSelected() {
      return isSelected;
    }

    public void setSelected(boolean isSelected) {
      this.isSelected = isSelected;
    }

    @Override
    public String toString() {
      return value.toString();
    }

    public Object getValue() {
      return value;
    }
  }

  class CheckListRenderer extends JCheckBox implements ListCellRenderer {

    private static final long serialVersionUID = 1L;

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean hasFocus) {
      setEnabled(list.isEnabled());
      setSelected(((CheckListItem) value).isSelected());
      setFont(list.getFont());
      setBackground(list.getBackground());
      setForeground(list.getForeground());
      setText(value.toString());
      return this;
    }
  }
}
