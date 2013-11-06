/**
 * Copyright 2013 Robert Balent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.balent.flight.app;

import java.io.Serializable;

import cz.balent.flight.model.Plane;
/**
 * SessionRoutePart represents one part of the route. It's temporary object
 * which perform calculations for the one part of the route. For example distance,
 * new plane speed and flight direction based on weather conditions. Time for the
 * route part
 * 
 * @author Robert Balent
 *
 */
public class SessionRoutePart implements Serializable {
	private static final long serialVersionUID = -5179071927301097045L;
	
	private static double earthRadius = 6371000; // meters
	private static double earthRadiusKm = 6371;
	private SessionPoint startPoint;
	private SessionPoint endPoint;
	private int windDirection;
	private double windSpeed;
	private double course;
	private double calculatedCourse;
	private double distance;
	private double planeSpeed;
	private double calculatedSpeed;
	private double time;

	public SessionRoutePart() {
		// empty constructor
	}

	SessionRoutePart(SessionPoint startPoint, SessionPoint endpoint) {
		this.startPoint = startPoint;
		this.endPoint = endpoint;
		this.windDirection = startPoint.getWindDirection();
		this.windSpeed = startPoint.getWindSpeed();
	}

	public SessionPoint getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(SessionPoint startPoint) {
		this.startPoint = startPoint;
	}

	public SessionPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(SessionPoint endPoint) {
		this.endPoint = endPoint;
	}

	public int getWindDirection() {
		return windDirection;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public double getCourse() {
		return course;
	}

	public double getCalculatedCourse() {
		return calculatedCourse;
	}

	public double getDistance() {
		return distance;
	}

	public double getTime() {
		return time;
	}

	public void calculate(Plane plane) {
		planeSpeed = plane.getSpeed();
		distance = calculateDistance(startPoint.getLatLng().getLat(), 
									 startPoint.getLatLng().getLng(),
									 endPoint.getLatLng().getLat(),
									 endPoint.getLatLng().getLng());
		course = calculateFlightDirection();
		calculateCalculatedFlightDistanceAndDirection();
		calculateTime();
	}
	
	// Haversine method calculating distance - used to compare with general cosine method
	public double calculateDistanceHaversine(double lat1, double lng1, double lat2, double lng2) {
		double latDiff = Math.toRadians(lat2 - lat1);
		double lngDiff = Math.toRadians(lng2 - lng1);
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);
		
		double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
				+ Math.cos(lat1Rad)
				* Math.cos(lat2Rad) * Math.sin(lngDiff / 2)
				* Math.sin(lngDiff / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;
	}
	
	public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; // meters
		
		double latDiff = Math.toRadians(lat2 - lat1);
		double lngDiff = Math.toRadians(lng2 - lng1);
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);
		
		double centralAngle = Math.acos(Math.sin(lat1Rad)*Math.sin(lat2Rad) + 
				Math.cos(lat1Rad)*Math.cos(lat2Rad)*Math.cos(lngDiff));
		double distance = earthRadius * centralAngle;
		return distance;
	}

	public double calculateCalculatedFlightDirection(double planeCourse,
			double thisPlaneSpeed, double windCourse, double thisWindSpeed) {

		boolean right = true;

		double windRouteAngle = windCourse;// ((180 + windCourse) % 360);

		windRouteAngle = ((windRouteAngle + (360 - planeCourse)) % 360);

		if (windRouteAngle > 180) {
			windRouteAngle = 360 - windRouteAngle;
			right = false;
		}
		
		System.out.println("1 ");
		System.out.println("1 ");
		System.out.println("1 ");
		

		double thisWindSpeedAngle = thisWindSpeed / earthRadiusKm;
		double thisPlaneSpeedAngle = thisPlaneSpeed / earthRadiusKm;
		double newPlaneSpeed = Math.acos(
				(Math.cos(thisWindSpeedAngle)*Math.cos(thisPlaneSpeedAngle)) + 
				(Math.sin(thisWindSpeedAngle)*Math.sin(thisPlaneSpeedAngle)*Math.cos(Math.toRadians(windRouteAngle))));
		newPlaneSpeed = newPlaneSpeed * earthRadiusKm;
		
		System.out.println("New " + newPlaneSpeed);
		System.out.println(windRouteAngle + " " + thisPlaneSpeed + " " + thisWindSpeed);
		calculatedSpeed = newPlaneSpeed;
		
		double newNewPlaneSpeedAngle = newPlaneSpeed / earthRadiusKm;

		double US = Math.toDegrees(Math.acos(
				(Math.cos(thisWindSpeedAngle) - Math.cos(thisPlaneSpeedAngle) * Math.cos(newNewPlaneSpeedAngle)) /
				(Math.sin(thisPlaneSpeedAngle) * Math.sin(newNewPlaneSpeedAngle))));
		double newCourse;
		
		if (right) {
			newCourse = ((planeCourse + US) + 360) % 360;
		} else {
			newCourse = ((planeCourse - US) + 360) % 360;
		}
		return newCourse;
	}

	private void calculateCalculatedFlightDistanceAndDirection() {
		calculatedCourse = calculateCalculatedFlightDirection(course,
				planeSpeed, windDirection, knotToKilometers(windSpeed));
	}

	private void calculateTime() {
		time = (distance / calculatedSpeed) / 1000;
	}

	private double calculateFlightDirection() {
		double lat1 = startPoint.getLatLng().getLat();
		double lng1 = startPoint.getLatLng().getLng();
		double lat2 = endPoint.getLatLng().getLat();
		double lng2 = endPoint.getLatLng().getLng();
		return calculateFlightDirection(lat1, lng1, lat2, lng2);
	}

	public double calculateFlightDirection(double getLat1, double getLng1, double getLat2, double getLng2) {
		double lat1Orig = getLat1;
		double lng1Orig = getLng1;
		double lat2Orig = getLat2;
		double lng2Orig = getLng2;
		double lngOrig = lng2Orig - lng1Orig;

		double lat1 = Math.toRadians(lat1Orig);
		double lat2 = Math.toRadians(lat2Orig);
		double lng = Math.toRadians(lngOrig);

		double dLon = lng;
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(dLon);
		double brng = Math.toDegrees(Math.atan2(y, x));

		return (brng + 360) % 360;
	}

	private double knotToKilometers(double knots) {
		return 1.852 * knots;
	}
}
