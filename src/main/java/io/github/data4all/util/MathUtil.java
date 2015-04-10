/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.data4all.util;
import io.github.data4all.model.data.Node;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.location.Location;

/**
 * This class provides some calculation methods  
 * @author burghardt
 * @version 1.1 *
 */
public final class MathUtil {
    
    /** 
     * @param vector
     *            which is going to be rotated
     * @param axis
     *            around which the vector is rotated
     * @param angle
     *            rotation angle
     * @return the rotated vector
     */
    public static double[] rotate(double[] vector, double[] axis, double angle) {
        final double[][] matrix = new double[3][3];
        matrix[0][0] = axis[0] * axis[0] * (1 - Math.cos(angle))
                + Math.cos(angle);
        matrix[0][1] = axis[0] * axis[1] * (1 - Math.cos(angle)) - axis[2]
                * Math.sin(angle);
        matrix[0][2] = axis[0] * axis[2] * (1 - Math.cos(angle)) + axis[1]
                * Math.sin(angle);
        matrix[1][0] = axis[1] * axis[0] * (1 - Math.cos(angle)) + axis[2]
                * Math.sin(angle);
        matrix[1][1] = axis[1] * axis[1] * (1 - Math.cos(angle))
                + Math.cos(angle);
        matrix[1][2] = axis[1] * axis[2] * (1 - Math.cos(angle)) - axis[0]
                * Math.sin(angle);
        matrix[2][0] = axis[2] * axis[0] * (1 - Math.cos(angle)) - axis[1]
                * Math.sin(angle);
        matrix[2][1] = axis[2] * axis[1] * (1 - Math.cos(angle)) + axis[0]
                * Math.sin(angle);
        matrix[2][2] = axis[2] * axis[2] * (1 - Math.cos(angle))
                + Math.cos(angle);

        final double[] returnVec = new double[3];
        for (int i = 0; i < 3; i++) {
            double value = 0;
            for (int b = 0; b < 3; b++) {
                value += vector[b] * matrix[i][b];
            }
            returnVec[i] = value;
        }
        return returnVec;
    }

    /**
     * @param angle
     *            difference to the deviceorientation
     * @param width
     *            width or height of the devicedisplay
     * @param maxAngle
     *            maximum angle of the camera
     * @return the pixel
     */
    public static float calculatePixelFromAngle(double angle, double width,
            double maxAngle) {
        final double adjacent = (width / 2) / Math.tan(maxAngle / 2);
        return (float) (Math.tan(angle) * adjacent);
    }

    /**
     * Calculates the angle altered by the given pixel.
     * 
     * @param pixel
     *            one coordinate of the pixel
     * @param axis
     *            the axis on which the angle is altered
     * @param maxAngle
     *            maximum camera angle
     * @return altered Angle
     */
    public static double calculateAngleFromPixel(double pixel, double axis,
            double maxAngle) {
        final double adjacent = (axis / 2) / Math.tan(maxAngle / 2);
        final double opposite = pixel - (axis / 2);
        return Math.atan(opposite / adjacent);
    }

    /**
     * Calculates the fourth point in dependence of the first three points of
     * the given list.
     * 
     * @param coords
     *            A list with exact three points
     * @return a Point in a local coordinate System
     */
    public static double[] calcFourthCoord(List<double[]> coords) {
        final double[] a = coords.get(0);
        final double[] b = coords.get(1);
        final double[] c = coords.get(2);

        final double x = a[0] + (c[0] - b[0]);
        final double y = a[1] + (c[1] - b[1]);

        final double[] coord = {x,y,};
        return coord;
    }

    /**
     * calculates Node (with latitude and longitude) from coordinates in a local
     * system and the current Location.
     * 
     * @param location
     *            current location of the device
     * @param coord
     *            coordinate of the point in the local system
     * @return A Node with latitude and longitude
     */
    public static Node calculateGPSPoint(Location location, double[] coord) {
        final double lat = Math.toRadians(location.getLatitude());
        final double lon = Math.toRadians(location.getLongitude());
        // calculate the length of the current latitude line with the earth
        // radius
        final double radius = 6371004.0;
        double lonLength = radius * Math.cos(lat);
        lonLength = lonLength * 2 * Math.PI;
        // add to the current latitude the distance of the coordinate
        double lon2 = lon + Math.toRadians((coord[0] * 360) / lonLength);
        // fix the skip from -PI to +PI for the longitude
        lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
        // calculate the length of the current longitude line with the earth
        // radius
        final double latLength = radius * 2 * Math.PI;
        // add to the current Longitude the distance of the coordinate
        double lat2 = lat + Math.toRadians((coord[1] * 360) / latLength);
        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);
        // create a new Node with the latitude and longitude values
        return new Node(-1, lat2, lon2);
    }

    /**
     * transfers GPSPoints to a local Coordinate System
     * 
     * @param location
     * @param node
     * @return coord
     */
    public static double[] calculateCoordFromGPS(Location location, Node node) {
        final double lat = Math.toRadians(location.getLatitude());
        final double lon = Math.toRadians(location.getLongitude());
        // calculate the length of the current latitude line with the earth
        // radius
        final double radius = 6371004.0;
        double lonLength = radius * Math.cos(lat);
        lonLength = lonLength * 2 * Math.PI;
        final double latLength = radius * 2 * Math.PI;
        final double[] coord = new double[2];
        double test = Math.toRadians(node.getLat()) - lat;
        coord[1] = latLength * test / (Math.PI * 2);
        test = (Math.toRadians(node.getLon()) - lon);
        coord[0] = lonLength * test / (Math.PI * 2);
        return coord;
    }

    /**
     * Calculates the coliding Point for 2 lines from java-forum.com
     * 
     * @param coords
     * @return double[]
     */
    public static double[] intersectLines(List<double[]> coords) {

        final double x1 = coords.get(0)[0];
        final double x2 = coords.get(2)[0];
        final double x3 = coords.get(1)[0];
        final double x4 = coords.get(3)[0];
        final double y1 = coords.get(0)[1];
        final double y2 = coords.get(2)[1];
        final double y3 = coords.get(1)[1];
        final double y4 = coords.get(3)[1];

        final double zx = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2)
                * (x3 * y4 - y3 * x4);
        final double zy = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2)
                * (x3 * y4 - y3 * x4);

        final double n = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        final double x = zx / n;
        final double y = zy / n;
        final double[] coord = {x,y,};
        return coord;

    }

    /**
     * Transforms a quadrangle into a rectangle.
     * 
     * @param nodes
     *            List of the nodes of the quadrangle
     * @return result List of the nodes of the now rectangle.
     */
    public static List<Node> transformIntoRectangle(List<Node> nodes) {
        if (nodes.size() == 5) {
            nodes.remove(4);
            final Location location = new Location("provider");
            location.setLatitude(nodes.get(0).getLat());
            location.setLongitude(nodes.get(0).getLon());
            final List<double[]> coords = new ArrayList<double[]>();
            double x = 0;
            double y = 0;
            for (Node iter : nodes) {
                final double[] coord = MathUtil.calculateCoordFromGPS(location, iter);
                coords.add(coord);
                x += coord[0];
                y += coord[1];
            }
            x = x / 4;
            y = y / 4;
            final double[] center = MathUtil.intersectLines(coords);
            final List<Double> lengths = new ArrayList<Double>();
            final List<double[]> coords2 = new ArrayList<double[]>();
            double avgLength = 0;
            for (double[] iter : coords) {
                final double[] coord = {iter[0] - center[0],iter[1] - center[1], };
                coords2.add(coord);
                final double length = Math.sqrt(coord[0] * coord[0] + coord[1]
                        * coord[1]);
                lengths.add(length);
                avgLength += length;
            }
            avgLength = avgLength / 4;
            final List<double[]> coords3 = new ArrayList<double[]>();
            int i = 0;
            for (double[] iter : coords2) {
                final double[] coord = {iter[0] * avgLength / lengths.get(i),
                        iter[1] * avgLength / lengths.get(i), };
                i++;
                coords3.add(coord);
            }
            if (Math.abs(coords3.get(0)[0] - coords3.get(2)[0]) < 0.01
                    && Math.abs(coords3.get(0)[1] - coords3.get(2)[1]) < 0.01) {
                final double[] coord = {-coords3.get(0)[0],-coords3.get(0)[1],};
                coords3.set(0, coord);
            }
            if (Math.abs(coords3.get(1)[0] - coords3.get(3)[0]) < 0.01
                    && Math.abs(coords3.get(1)[1] - coords3.get(3)[1]) < 0.01) {
                final double[] coord = {-coords3.get(1)[0],-coords3.get(1)[1],};
                coords3.set(1, coord);
            }
            final List<Node> nodes2 = new ArrayList<Node>();
            for (double[] iter : coords3) {
                final double[] coord = {iter[0] + x ,iter[1] + y,};
                nodes2.add(MathUtil.calculateGPSPoint(location, coord));
            }
            nodes2.add(nodes2.get(0));
            return nodes2;
        }
        return nodes;
    }
    
    /**
     * calculates the Perimeter of 3 given Points
     * @param points
     *          3 Points
     * @return the perimeter
     */
    public static float perimeter(List<Point> points){
        float length = 0;
        for(int i = 0; i<=2 ; i++){
            int j = i+1;
            if(j==3) j = 2;
            int x = points.get(i).x -points.get(j).x;
            int y = points.get(i).y -points.get(j).y;
            length += Math.sqrt(x*x+y*y);
        }
        return length;
    }

}
