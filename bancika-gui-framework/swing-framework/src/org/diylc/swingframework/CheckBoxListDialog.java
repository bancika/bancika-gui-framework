package org.diylc.swingframework;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.diylc.swingframework.CheckBoxList.CheckListItem;

public class CheckBoxListDialog extends ButtonDialog {

  private static final long serialVersionUID = 1L;
  private Object[] options;
  
  private CheckBoxList list;
  
  public CheckBoxListDialog(JFrame owner, String title, Object[] options) {
    this(owner, title, new String[] { "OK", "Cancel" }, options);
  }

  public CheckBoxListDialog(JFrame owner, String title, String[] buttonCaptions, Object[] options) {
    super(owner, title, buttonCaptions);
    this.options = options;
    
    setPreferredSize(new Dimension(320, 400));
    layoutGui();
  }
  
  private CheckBoxList getList() {
    if (list == null) {
      CheckListItem[] items = new CheckListItem[options.length];
      for (int i = 0; i < options.length; i++) {
        items[i] = new CheckListItem(options[i]);
        items[i].setSelected(false);
      }
      list = new CheckBoxList(items);
    }
    
    return list;
  }

  @Override
  protected JComponent getMainComponent() {
    JScrollPane scrollPane = new JScrollPane(getList());
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
    return scrollPane;
  }

  public Object[] getSelectedOptions() {
    List<Object> selected = new ArrayList<Object>();
    ListModel model = getList().getModel();
    for (int i = 0; i < model.getSize(); i++) { 
      CheckListItem item = (CheckListItem)model.getElementAt(i);
      if (item.isSelected())
        selected.add(item.getValue());
    }
    return selected.toArray();
  }
}
