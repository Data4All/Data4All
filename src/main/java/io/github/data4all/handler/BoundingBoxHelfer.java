package io.github.data4all.handler;

public class BoundingBoxHelfer {
    
    public static double[] getBoundingBox(double lat,double lon,double radius){
        double result[]=new double[4];
        
        double earth_radius = 6371;
        double maxLat = lat + Math.toDegrees(radius/earth_radius);
        double minLat = lat - Math.toDegrees(radius/earth_radius);
        double maxLon = lon + Math.toDegrees(radius/earth_radius/Math.cos(Math.toRadians(lat)));
        double minLon = lon - Math.toDegrees(radius/earth_radius/Math.cos(Math.toRadians(lat)));
        result[0]=maxLat;
        result[1]=minLat;
        result[2]=maxLon;
        result[3]=minLon;
       
        return result;
    }
    
    public static void main(String[] args) {
        double [] bbox=getBoundingBox(53.03922, 8.81948, 0.5);
        for (double d : bbox) {
            System.out.println(d);
        }
    }

}
