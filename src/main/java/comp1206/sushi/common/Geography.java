package comp1206.sushi.common;

public class Geography {
	private static final int EARTH_RADIUS = 6378;
	
	public static Double distance(Postcode p1, Postcode p2) {
		
		Double startLat = p1.getLatLong().get("lat");
		Double endLat = p2.getLatLong().get("lat");
		
		Double startLong = p1.getLatLong().get("lon");
		Double endLong = p2.getLatLong().get("lon");
		
		Double dLat  = Math.toRadians((endLat - startLat));
		Double dLong = Math.toRadians((endLong - startLong));
		startLat = Math.toRadians(startLat);
		endLat = Math.toRadians(endLat);

		Double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return EARTH_RADIUS * c;
	}
	
	public static Double haversin(Double val) {
		return Math.pow(Math.sin(val / 2), 2);
	}
	

}
