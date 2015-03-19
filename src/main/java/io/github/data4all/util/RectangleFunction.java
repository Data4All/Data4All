/**
 * 
 */
package io.github.data4all.util;

import io.github.data4all.model.data.Node;

import java.util.List;

/**
 * @author Richard
 *
 */
public class RectangleFunction {

    public static List<Node> makeRectangular(List<Node> nodes) {
        if (nodes.size() >= 4) {

            Node a = nodes.get(0);
            Node b = nodes.get(1);
            Node c = nodes.get(2);
            Node d = nodes.get(3);

            double ax = a.getLat();
            double ay = a.getLon();
            double bx = b.getLat();
            double by = b.getLon();
            double cx = c.getLat();
            double cy = c.getLon();
            double dx = d.getLat();
            double dy = d.getLon();

            double abx = bx - ax;
            double aby = by - ay;

            double bcx = cx - bx;
            double bcy = cy - by;

            double cdx = dx - cx;
            double cdy = dy - cy;

            double dax = ax - dx;
            double day = ay - dy;

        }
        return nodes;
    }

}
