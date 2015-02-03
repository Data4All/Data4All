/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.model.data;

/**
 * A bounding box (usually shortened to bbox) is an area defined by two
 * longitudes and two latitudes, where: - Latitude is a decimal number between
 * -90.0 and 90.0. - Longitude is a decimal number between -180.0 and 180.0.
 * 
 * @author fkirchge
 *
 */
public class BoundingBox {

    /**
     * Coordinates to define the area of the bounding box.
     */
    private double minlat;
    private double minlon;
    private double maxlat;
    private double maxlon;

    /**
     * Default Constructor max./min. Latitude is a decimal number between -90.0
     * and 90.0. max./min. Longitude is a decimal number between -180.0 and
     * 180.0.
     * 
     * @param minlat
     * @param minlon
     * @param maxlat
     * @param maxlon
     */
    public BoundingBox(double minlat, double minlon, double maxlat,
            double maxlon) {
        this.minlat = minlat;
        this.minlon = minlon;
        this.maxlat = maxlat;
        this.maxlon = maxlon;
    }

    public double getMinlat() {
        return minlat;
    }

    public void setMinlat(double minlat) {
        this.minlat = minlat;
    }

    public double getMinlon() {
        return minlon;
    }

    public void setMinlon(double minlon) {
        this.minlon = minlon;
    }

    public double getMaxlat() {
        return maxlat;
    }

    public void setMaxlat(double maxlat) {
        this.maxlat = maxlat;
    }

    public double getMaxlon() {
        return maxlon;
    }

    public void setMaxlon(double maxlon) {
        this.maxlon = maxlon;
    }

}
