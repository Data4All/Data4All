package io.github.data4all.model.data;

public class GPSBean {
	public long id;
	public double longitude;
	public double latitude;
	public long createAt;
	public float accuracy;
	public String provider;
	public long mapReference;

//	public GPSBean(double lat, double lon) {
//		this.latitude = lat;
//		this.longitude = lon;
//	}

	public GPSBean(double lat, double lon, long ca, float accuracy,
			String provider, long mr) {
		this.latitude = lat;
		this.longitude = lon;
		this.createAt = ca;
		this.accuracy = accuracy;
		this.provider = provider;
		this.mapReference = mr;
	}

}
