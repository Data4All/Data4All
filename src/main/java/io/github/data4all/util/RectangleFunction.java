/**
 * 
 */
package io.github.data4all.util;

import io.github.data4all.model.data.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Richard
 *
 */
public class RectangleFunction {

    /**
     * Transforms a quadrangle into a rectangle.
     * 
     * @param nodes
     *            List of the nodes of the quadrangle
     * @return result List of the nodes of the now rectangle.
     */
    public static List<Node> transformIntoRectangle(List<Node> nodes) {
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

            double abLength = Math.sqrt((abx * abx) + (aby * aby));
            double bcLength = Math.sqrt((bcx * bcx) + (bcy * bcy));
            double cdLength = Math.sqrt((cdx * cdx) + (cdy * cdy));
            double daLength = Math.sqrt((dax * dax) + (day * day));

            double alpha = Math.acos(((-1 * dax * abx) + (-1 * day * aby)
                    / (daLength * abLength)));
            double beta = Math.acos(((-1 * abx * bcx) + (-1 * aby * bcy))
                    / (abLength * bcLength));
            double gamma = Math.acos(((-1 * bcx * cdx) + (-1 * bcy * cdy))
                    / (bcLength * cdLength));
            double delta = Math.acos(((-1 * cdx * dax) + (-1 * cdy * day)
                    / (cdLength * daLength)));

            double alphaMinus = (Math.PI / 2) - alpha;
            double betaMinus = (Math.PI / 2) - beta;
            double gammaMinus = (Math.PI / 2) - gamma;
            double deltaMinus = (Math.PI / 2) - delta;

            List<Node> result = new LinkedList<Node>();
            double temp;
            switch (closestTo90(alphaMinus, betaMinus, gammaMinus, deltaMinus)) {
            case 1:
                temp = abx;
                abx = (temp * Math.cos(alphaMinus))
                        - (aby * Math.sin(alphaMinus));
                aby = (temp * Math.sin(alphaMinus))
                        + (aby * Math.cos(alphaMinus));

                bx = ax + abx;
                by = ay + aby;

                cx = bx + (-1) * dax;
                cy = by + (-1) * day;

                break;
            case 2:
                temp = bcx;
                bcx = (temp * Math.cos(betaMinus))
                        - (bcy * Math.sin(betaMinus));
                bcy = (temp * Math.sin(betaMinus))
                        + (bcy * Math.cos(betaMinus));

                cx = bx + bcx;
                cy = by + bcy;

                dx = cx + (-1) * abx;
                dy = cy + (-1) * aby;

                break;
            case 3:
                temp = cdx;
                cdx = (temp * Math.cos(gammaMinus))
                        - (cdy * Math.sin(gammaMinus));
                cdy = (temp * Math.sin(gammaMinus))
                        + (cdy * Math.cos(gammaMinus));

                dx = cx + cdx;
                dy = cy + cdy;

                ax = dx + (-1) * bcx;
                ay = dy + (-1) * bcy;

                break;
            case 4:
                temp = dax;
                dax = (temp * Math.cos(deltaMinus))
                        - (day * Math.sin(deltaMinus));
                day = (temp * Math.sin(deltaMinus))
                        + (day * Math.cos(deltaMinus));

                ax = dx + dax;
                ay = dy + day;

                bx = ax + (-1) * cdx;
                by = ay + (-1) * cdy;

                break;
            }

            a.setLat(ax);
            a.setLon(ay);

            b.setLat(bx);
            b.setLon(by);

            c.setLat(cx);
            c.setLon(cy);

            d.setLat(dx);
            d.setLon(dy);

            result.add(a);
            result.add(b);
            result.add(c);
            result.add(d);
            result.add(a);

            return result;
        }
        return nodes;
    }

    private static int closestTo90(double a, double b, double c, double d) {

        if (Math.abs(a) > Math.abs(b)) {
            if (Math.abs(b) > Math.abs(c)) {
                if (Math.abs(c) > Math.abs(d)) {
                    return 4;
                } else {
                    return 3;
                }
            } else {
                if (Math.abs(b) > Math.abs(d)) {
                    return 4;
                } else {
                    return 2;
                }
            }
        } else {
            if (Math.abs(a) > Math.abs(c)) {
                if (Math.abs(c) > Math.abs(d)) {
                    return 4;
                } else {
                    return 3;
                }
            } else {
                if (Math.abs(a) > Math.abs(d)) {
                    return 4;
                } else {
                    return 1;
                }
            }
        }
    }
}
