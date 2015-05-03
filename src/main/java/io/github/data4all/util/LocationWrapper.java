package io.github.data4all.util;

import android.location.Location;

public class LocationWrapper {

    Location location;

    public LocationWrapper(Location current) {
        this.location=current;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Location) {
            return this.location.getLatitude() == ((Location) o).getLatitude()
                    && this.location.getLongitude() == ((Location) o)
                            .getLongitude();
        }
        if (o instanceof LocationWrapper) {
            return this.location.getLatitude() == ((LocationWrapper) o)
                    .getLocation().getLatitude()
                    && this.location.getLongitude() == ((LocationWrapper) o)
                            .getLocation().getLongitude();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}