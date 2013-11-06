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

import cz.balent.flight.model.Plane;
/**
 * This bean is responsible of creating temporary object for each
 * part of the route on which it will call calculate() methods. 
 * 
 * Also here are performed calculations related for the whole route like
 * total consumption, total time and distance. 
 * 
 * @author Robert Balent
 *
 */
public class SessionRoute implements Serializable {
	private static final long serialVersionUID = -197863685484370240L;

	private List<SessionRoutePart> routeParts = new ArrayList<SessionRoutePart>();
	private List<SessionPoint> points = new ArrayList<SessionPoint>();
	private double distance;
	private double time;
	private double consumption;
	private double totalTime;
	private double totalConsumption;
	private int twistCount = 0;

	public SessionRoute(List<SessionPoint> routePoints, boolean isRoundTrip) {
		addPoints(routePoints, isRoundTrip);
	}

	public SessionRoute() {
		// empty constructor
	}

	private void addPoint(SessionPoint routePoint) {
		if (points.size() > 0) {
			routeParts.add(new SessionRoutePart(points.get(points.size() - 1),
					routePoint));
		}
		points.add(routePoint);
	}

	private void addPoints(List<SessionPoint> routePoints, boolean isRoundTrip) {
		for (SessionPoint point : routePoints) {
			addPoint(point);
		}
		if (isRoundTrip) {
			routeParts.add(new SessionRoutePart(points.get(points.size() - 1),
					points.get(0)));
		}
	}

	public List<SessionRoutePart> getRouteParts() {
		return routeParts;
	}

	public List<SessionPoint> getPoints() {
		return points;
	}

	public double getDistance() {
		return distance;
	}

	public double getTime() {
		return time;
	}

	public double getConsumption() {
		return consumption;
	}

	public double getTotalConsumption() {
		return totalConsumption;
	}

	public int getTwistCount() {
		return twistCount;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void calculate(Plane plane) {
		for (SessionRoutePart routePart : routeParts) {
			routePart.calculate(plane);
		}
		distance = 0;
		time = 0;
		for (SessionRoutePart routePart : routeParts) {
			distance += routePart.getDistance();
			time += routePart.getTime();
		}

		consumption = plane.getConsumption() * time;

		totalTime = time + ((double) 10) / 60;
		double lastCourse = 0;
		for (int i = 0; i < routeParts.size(); i++) {
			SessionRoutePart routePart = routeParts.get(i);
			if (i > 0) {
				double routeCourse = routePart.getCourse();
				double diff = lastCourse - routeCourse;
				if (diff < 0) {
					diff = -diff;
				}
				if (diff > 180) {
					diff = 360 - diff;
				}
				if (diff > 60) {
					twistCount++;
				}
			}

			lastCourse = routePart.getCourse();
		}
		totalTime += ((double) twistCount) / 60;
		totalConsumption = plane.getConsumption() * totalTime;
	}
}
