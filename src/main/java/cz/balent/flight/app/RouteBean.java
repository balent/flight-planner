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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cz.balent.flight.dao.RouteManager;
import cz.balent.flight.model.Point;
import cz.balent.flight.model.Route;
/**
 * Bean handles saving and loading routes from and to session data bean.
 * 
 * @author Robert Balent
 *
 */
public class RouteBean implements Serializable {
	private static final long serialVersionUID = 3893438485523190569L;

	@Inject
	SessionDataBean sessionData;
	
	@Inject
	RouteManager routeManager;
	
	public void saveRoute(String name) {
		sessionData.setRouteName(name);
		Route route = new Route();
		route.setName(name);
		route.setUser(sessionData.getUser());
		Map<Integer, Point> points = new HashMap<Integer, Point>();
		List<SessionPoint> sessionPoints = sessionData.getPoints();
		for (int i = 0; i < sessionPoints.size(); i++) {
			SessionPoint sessionPoint = sessionPoints.get(i);
			Point point = new Point();
			point.setLat(sessionPoint.getLatLng().getLat());
			point.setLng(sessionPoint.getLatLng().getLng());
			points.put(i, point);
		}
		route.setPoints(points);
		routeManager.saveRoute(route);
	}
	
	public void loadRoute(String name) {
		Route route = routeManager.getUserRoute(sessionData.getUser(), name);
		sessionData.clearPoints();
		sessionData.addPoints(route);
		sessionData.setRouteName(name);
	}
	
	public List<String> getRouteNames() {
		List<String> routeNames = new ArrayList<String>();
		
		List<Route> routes = routeManager.getUserRoutes(sessionData.getUser());
		for (Route route: routes) {
			routeNames.add(route.getName());
		}
		return routeNames;
	}

	public void deleteRoute(String name) {
		routeManager.deleteUserRoute(sessionData.getUser(), name);
	}
}
