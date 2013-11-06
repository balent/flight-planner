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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;

import org.wicketstuff.gmap.api.GLatLng;

import cz.balent.flight.model.Plane;
import cz.balent.flight.model.Point;
import cz.balent.flight.model.Route;
import cz.balent.flight.model.User;
/**
 * SessionDataBean stores all temporary data of session.
 * It's responsible of creating SessionRoute object and calling 
 * calculations on it.
 * 
 * @author Robert Balent
 *
 */
@SessionScoped
public class SessionDataBean implements Serializable {
	private static final long serialVersionUID = 747815983480497965L;

	private final List<SessionPoint> points = new ArrayList<SessionPoint>();

	private GLatLng mapPosition = new GLatLng(49.653, 16.425);
	private int mapZoom = 8;
	private SessionRoute route = new SessionRoute();
	private boolean isRoundTrip = true;
	private boolean loggedIn = false;
	private User user;
	private String routeName;
	private Plane actualPlane;
	private boolean editPlane = false;
	private char nextSymbol = 'a';

	public List<SessionPoint> getPoints() {
		return points;
	}

	public void clearPoints() {
		points.clear();
	}

	public void addPoints(Route route) {
		Map<Integer, Point> points = route.getPoints();
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			SessionPoint sessionPoint = new SessionPoint();
			GLatLng latLng = new GLatLng(point.getLat(), point.getLng());
			sessionPoint.setLatLng(latLng);
			sessionPoint.setSymbol((char) ((i % 26) + 65));
			addPoint(sessionPoint);
		}
	}

	public void addPoint(SessionPoint point) {
		points.add(point);
	}

	public GLatLng getMapPosition() {
		return mapPosition;
	}

	public void setMapPosition(GLatLng mapPosition) {
		this.mapPosition = mapPosition;
	}

	public int getMapZoom() {
		return mapZoom;
	}

	public void setMapZoom(int mapZoom) {
		this.mapZoom = mapZoom;
	}

	public SessionRoute getRoute() {
		return route;
	}

	public boolean isRoundTrip() {
		return isRoundTrip;
	}

	public void setRoundTrip(boolean isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public Plane getActualPlane() {
		return actualPlane;
	}

	public void setActualPlane(Plane actualPlane) {
		this.actualPlane = actualPlane;
	}
	
	public boolean editPlane() {
		return editPlane;
	}
	
	public void setEditPlane(boolean editPlane) {
		this.editPlane = editPlane;
	}

	public void calculate() {
		route = new SessionRoute(points, isRoundTrip);
		route.calculate(actualPlane);
	}
	
	public char getNextSymbol() {
		return nextSymbol;
	}
	
	public void setNextSymbol(char nextSymbol) {
		this.nextSymbol = nextSymbol;
	}
}
