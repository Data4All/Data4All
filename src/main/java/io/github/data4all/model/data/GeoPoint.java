package io.github.data4all.model.data;

public interface GeoPoint {

	/** @return the latitude of this point */
	public abstract double getLat();

	/** @return the longitude of this point */
	public abstract double getLon();
	
	public static interface InterruptibleGeoPoint extends GeoPoint {
		/** return true if no line should be drawn from the last point to this one */
		public abstract boolean isInterrupted();
	}
}