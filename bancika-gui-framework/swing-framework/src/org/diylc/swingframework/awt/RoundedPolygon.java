package org.diylc.swingframework.awt;

import java.awt.Point;
import java.awt.geom.Path2D;

public class RoundedPolygon extends Path2D.Double {

  private static final long serialVersionUID = 1L;

  public RoundedPolygon(Point[] points, double[] radiuses) {
    super();
    moveTo(points[0].x, points[0].y);
    for (int i = 0; i < points.length - 1; i++) {
      Point first = points[i];
      Point second = points[i + 1 >= points.length ? i + 1 - points.length : i + 1];
      Point third = points[i + 2 >= points.length ? i + 2 - points.length : i + 2];
      
      Point p1 = new Point(0, 0);
      Point p2 = new Point(0, 0);
      locateTwoPoints(first, second, third, radiuses.length <= i ? radiuses[radiuses.length - 1] : radiuses[i], p1, p2);
      lineTo(p1.x, p1.y);
      quadTo(second.x, second.y, p2.x, p2.y);      
    }
    closePath();
  }
  
  private void locateTwoPoints(Point first, Point second, Point third, double radius, Point p1, Point p2) {
    double d1 = distance(first, second);
    double d2 = distance(second, third);
    double t1 = 1 - radius / d1;
    double t2 = radius / d2;
    interpolate(first, second, t1, p1);
    interpolate(second, third, t2, p2);
  }
  
  private void interpolate(Point p1, Point p2, double t, Point p) {
    p.setLocation((int)Math.round(p1.x * (1-t) + p2.x * t), (int)Math.round(p1.y * (1-t) + p2.y * t));
  }
  
  private double distance(Point p1, Point p2) {
    return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
  }
}
