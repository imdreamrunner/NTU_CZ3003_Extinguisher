package cms.dataClasses.oldStuff;

import java.util.ArrayList;

public class PolygonalLocation extends Location {
	public static class Pair{
		private float lat;
		private float lng;
		
		public float getLat() {
			return lat;
		}
		public void setLat(float lat) {
			this.lat = lat;
		}
		public float getLng() {
			return lng;
		}
		public void setLng(float lng) {
			this.lng = lng;
		}
	}
	private ArrayList<Pair> points;

	public ArrayList<Pair> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Pair> points) {
		this.points = points;
	}
}
