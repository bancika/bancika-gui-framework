package org.diylc.swingframework.ruler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.diylc.appframework.images.IconLoader;
import org.diylc.swingframework.IDrawingProvider;
import org.diylc.swingframework.images.CursorLoader;


/**
 * Improved version of {@link JScrollPane} that features:
 * 
 * <ul>
 * <li>horizontal and vertical rulers with cursor position indicators</li>
 * <li>button in the top-left corner to choose ruler units</li>
 * <li>button in the bottom-right corner to navigate through the viewport</li>
 * </ul>
 * 
 * Note: when doing drag&drop in the viewport component sometimes Java doesn't fire
 * <code>mouseDragged</code> events. To overcome this, component can notify about drag movements by
 * firing property change events. Property name is "dragPoint" and x, y coordinates are stored as
 * oldValue and newValue respectively. <br>
 * <br>
 * Note to self: try to come up with something more elegant.
 * 
 * @author Branislav Stojkovic
 */
public class RulerScrollPane extends JScrollPane {

  private static final long serialVersionUID = 1L;

  private Ruler horizontalRuler;
  private Ruler verticalRuler;
  private JButton unitButton;
  private JButton navigateButton;
  private Corner topRightCorner;
  private Corner bottomLeftCorner;

  private List<IRulerListener> listeners;
  
  // mouse scroll mode
  private boolean mouseScrollMode = false;
  private Point mouseScrollPrevLocation = null;
  private static double MOUSE_SCROLL_SPEED = 0.8;

  public RulerScrollPane(Component view) {
    this(view, new ComponentThumbnailProvider(view));
  }

  public RulerScrollPane(Component view, final IDrawingProvider provider) {
    this(view, provider, 0, 0);
  }

  public RulerScrollPane(final Component view, final IDrawingProvider provider, double cmSpacing, double inSpacing) {
    super(view);

    horizontalRuler = new Ruler(Ruler.HORIZONTAL, true, cmSpacing, inSpacing);
    verticalRuler = new Ruler(Ruler.VERTICAL, true, cmSpacing, inSpacing);
    setColumnHeaderView(horizontalRuler);
    setRowHeaderView(verticalRuler);

    listeners = new ArrayList<IRulerListener>();
    
    view.addMouseListener(new MouseAdapter() {
          
      @Override
      public void mouseClicked(MouseEvent e) {
        if (!mouseScrollMode && e.getButton() == MouseEvent.BUTTON2) {
          mouseScrollPrevLocation = null;
          mouseScrollMode = true;
          view.setCursor(CursorLoader.ScrollCenter.getCursor());
          e.consume();
        } else if (mouseScrollMode) {
          mouseScrollMode = false;
          view.setCursor(Cursor.getDefaultCursor());
          e.consume();
        }
      }
    });
    
    view.addMouseMotionListener(new MouseAdapter() {

      @Override
      public void mouseMoved(MouseEvent e) {
        if (mouseScrollMode) {
          if (mouseScrollPrevLocation != null) {
            int dx = (int) ((e.getPoint().x - mouseScrollPrevLocation.x) * MOUSE_SCROLL_SPEED);
            int dy = (int) ((e.getPoint().y - mouseScrollPrevLocation.y) * MOUSE_SCROLL_SPEED);            
            getHorizontalScrollBar().setValue(getHorizontalScrollBar().getValue() + dx);
            getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + dy);
            
            if (Math.abs(dx) > 2 * Math.abs(dy))
              view.setCursor(dx > 0 ? CursorLoader.ScrollE.getCursor() : CursorLoader.ScrollW.getCursor());
            else if (Math.abs(dy) > 2 * Math.abs(dx))
              view.setCursor(dy > 0 ? CursorLoader.ScrollS.getCursor() : CursorLoader.ScrollN.getCursor());
            else if (dx > 0 && dy > 0)
              view.setCursor(CursorLoader.ScrollSE.getCursor());
            else if (dx > 0 && dy < 0)
              view.setCursor(CursorLoader.ScrollNE.getCursor());
            else if (dx < 0 && dy < 0)
              view.setCursor(CursorLoader.ScrollNW.getCursor());
            else if (dx < 0 && dy > 0)
              view.setCursor(CursorLoader.ScrollSW.getCursor());
//            else 
//              view.setCursor(CursorLoader.ScrollCenter.getCursor());
          }
          mouseScrollPrevLocation = e.getPoint();
          e.consume();
        }
      }
    });

    view.addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(ComponentEvent e) {
        updateRulerSize(e.getComponent().getWidth(), e.getComponent().getHeight());
      }
    });
    updateRulerSize(view.getWidth(), view.getHeight());

    unitButton = new JButton("cm");
    unitButton.setToolTipText("Toggle metric/imperial system");
    unitButton.setMargin(new Insets(0, 0, 0, 0));
    unitButton.setFont(unitButton.getFont().deriveFont(9f));
    unitButton.setFocusable(false);
    unitButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (horizontalRuler.isMetric()) {
          horizontalRuler.setIsMetric(false);
          verticalRuler.setIsMetric(false);
          unitButton.setText("in");
        } else {
          horizontalRuler.setIsMetric(true);
          verticalRuler.setIsMetric(true);
          unitButton.setText("cm");
        }
        for (IRulerListener listener : listeners) {
          listener.unitsChanged(horizontalRuler.isMetric());
        }
      }
    });

    navigateButton = new JButton(IconLoader.MoveSmall.getIcon());
    navigateButton.setToolTipText("Auto-scroll");
    navigateButton.setFocusable(false);
    navigateButton.setMargin(new Insets(0, 0, 0, 0));
    navigateButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        NavigateDialog navigateDialog = new NavigateDialog(RulerScrollPane.this, provider);
        navigateDialog.setVisible(true);
        navigateDialog.setLocationRelativeTo(navigateButton);
      }
    });

    topRightCorner = new Corner(Ruler.HORIZONTAL);
    bottomLeftCorner = new Corner(Ruler.VERTICAL);

    setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, unitButton);
    setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, topRightCorner);
    setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, bottomLeftCorner);
    setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, navigateButton);

    view.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("dragPoint")) {
          horizontalRuler.setIndicatorValue(((Long) evt.getOldValue()).intValue());
          horizontalRuler.repaint();
          verticalRuler.setIndicatorValue(((Long) evt.getNewValue()).intValue());
          verticalRuler.repaint();
        }
      }
    });

    view.addMouseMotionListener(new MouseAdapter() {

      @Override
      public void mouseMoved(MouseEvent e) {
        horizontalRuler.setIndicatorValue(e.getX());
        horizontalRuler.repaint();
        verticalRuler.setIndicatorValue(e.getY());
        verticalRuler.repaint();
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        horizontalRuler.setIndicatorValue(e.getX());
        horizontalRuler.repaint();
        verticalRuler.setIndicatorValue(e.getY());
        verticalRuler.repaint();
      }

      @Override
      public void mouseExited(MouseEvent e) {
        horizontalRuler.setIndicatorValue(-1);
        horizontalRuler.repaint();
        verticalRuler.setIndicatorValue(-1);
        verticalRuler.repaint();
      }
    });

    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    getHorizontalScrollBar().setUnitIncrement(50);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    getVerticalScrollBar().setUnitIncrement(50);
  }

  public void setRulerVisible(boolean visible) {
    if (visible) {
      setColumnHeaderView(horizontalRuler);
      setRowHeaderView(verticalRuler);
      setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, unitButton);
      setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, topRightCorner);
      setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, bottomLeftCorner);
    } else {
      setColumnHeaderView(null);
      setRowHeaderView(null);
      setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, null);
      setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, null);
      setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, null);
    }
  }

  public void setSelectionRectangle(Rectangle2D rect) {
    horizontalRuler.setSelectionRect(rect);
    verticalRuler.setSelectionRect(rect);
  }
  
  public void setUseHardwareAcceleration(boolean useHardwareAcceleration) {
    this.horizontalRuler.setUseHardwareAcceleration(useHardwareAcceleration);
    this.verticalRuler.setUseHardwareAcceleration(useHardwareAcceleration);
  }
  
  public void setZeroLocation(Point2D location) {
    this.horizontalRuler.setZeroLocation(location.getX());
    this.verticalRuler.setZeroLocation(location.getY());
  }

  public boolean addUnitListener(IRulerListener e) {
    return listeners.add(e);
  }

  public boolean removeUnitListener(IRulerListener o) {
    return listeners.remove(o);
  }

  public void setZoomLevel(double zoomLevel) {
    horizontalRuler.setZoomLevel(zoomLevel);
    verticalRuler.setZoomLevel(zoomLevel);
  }

  public void setMetric(boolean isMetric) {
    if (isMetric) {
      horizontalRuler.setIsMetric(true);
      verticalRuler.setIsMetric(true);
      unitButton.setText("cm");
    } else {
      horizontalRuler.setIsMetric(false);
      verticalRuler.setIsMetric(false);
      unitButton.setText("in");
    }
    for (IRulerListener listener : listeners) {
      listener.unitsChanged(isMetric);
    }
  }
    
  public boolean isMouseScrollMode() {
    return mouseScrollMode;
  }

  protected void updateRulerSize(int width, int height) {
    horizontalRuler.setPreferredWidth(width);
    horizontalRuler.invalidate();
    verticalRuler.setPreferredHeight(height);
    verticalRuler.invalidate();
  }

  class Corner extends JComponent {

    private static final long serialVersionUID = 1L;
    private final int orientation;

    public Corner(int orientation) {
      this.orientation = orientation;
    }

    protected void paintComponent(Graphics g) {
      g.setColor(Ruler.COLOR);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.black);
      if (orientation == Ruler.HORIZONTAL) {
        g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
      } else if (orientation == Ruler.VERTICAL) {
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
      }
    }
  }
}
