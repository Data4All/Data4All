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
package io.github.data4all.model.data;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class TransformationParamBean implements Parcelable {

    private double height;
    private double cameraMaxRotationAngle;
    private double cameraMaxPitchAngle;
    private int photoWidth;
    private int photoHeight;
    private Location location;

    /**
     * CREATOR that generates instances of {@link TransformationParamBean} from
     * a Parcel.
     */
    public static final Parcelable.Creator<TransformationParamBean> CREATOR =
            new Parcelable.Creator<TransformationParamBean>() {
                public TransformationParamBean createFromParcel(Parcel in) {
                    return new TransformationParamBean(in);
                }

                public TransformationParamBean[] newArray(int size) {
                    return new TransformationParamBean[size];
                }
            };

    public TransformationParamBean(double height,
            double cameraMaxRotationAngle, double cameraMaxPitchAngle,
            int photoWidth, int photoHeight, Location location) {
        this.height = height;
        this.cameraMaxPitchAngle = cameraMaxPitchAngle;
        this.cameraMaxRotationAngle = cameraMaxRotationAngle;
        this.photoHeight = photoHeight;
        this.photoWidth = photoWidth;
        this.location = location;
    }

    /**
     * Constructor to create a transformationbean from a parcel.
     * 
     * @param in
     */
    private TransformationParamBean(Parcel in) {
        height = in.readDouble();
        cameraMaxRotationAngle = in.readDouble();
        cameraMaxPitchAngle = in.readDouble();
        photoWidth = in.readInt();
        photoHeight = in.readInt();
        if (in.readInt() != 0) {
            location = new Location(in.readString());
            location.setLatitude(in.readDouble());
            location.setLongitude(in.readDouble());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public double getCameraMaxPitchAngle() {
        return cameraMaxPitchAngle;
    }

    public double getCameraMaxRotationAngle() {
        return cameraMaxRotationAngle;
    }

    public double getHeight() {
        return height;
    }

    public Location getLocation() {
        return location;
    }

    public int getPhotoHeight() {
        return photoHeight;
    }

    public int getPhotoWidth() {
        return photoWidth;
    }

    public void setCameraMaxPitchAngle(float cameraMaxPitchAngle) {
        this.cameraMaxPitchAngle = cameraMaxPitchAngle;
    }

    public void setCameraMaxRotationAngle(float cameraMaxRotationAngle) {
        this.cameraMaxRotationAngle = cameraMaxRotationAngle;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPhotoHeight(int photoHeight) {
        this.photoHeight = photoHeight;
    }

    public void setPhotoWidth(int photoWidth) {
        this.photoWidth = photoWidth;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(height);
        dest.writeDouble(cameraMaxRotationAngle);
        dest.writeDouble(cameraMaxPitchAngle);
        dest.writeInt(photoWidth);
        dest.writeInt(photoHeight);
        if (location != null) {
            dest.writeInt(1);
            dest.writeString(location.getProvider());
            dest.writeDouble(location.getLatitude());
            dest.writeDouble(location.getLongitude());
        } else {
            dest.writeInt(0);
        }
    }
}
