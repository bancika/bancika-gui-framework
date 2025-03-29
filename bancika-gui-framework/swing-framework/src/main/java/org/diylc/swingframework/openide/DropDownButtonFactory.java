package org.diylc.swingframework.openide;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

public final class DropDownButtonFactory {
  public static final String PROP_DROP_DOWN_MENU = "dropDownMenu";

  public static JButton createDropDownButton(Icon icon, JPopupMenu dropDownMenu) {
    return new DropDownButton(null, icon, dropDownMenu);
  }
  
  public static JButton createDropDownButton(String text, Icon icon, JPopupMenu dropDownMenu) {
    return new DropDownButton(text, icon, dropDownMenu);
  }
}
