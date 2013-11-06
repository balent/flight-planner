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
package cz.balent.flight.web;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import cz.balent.flight.app.RouteBean;
import cz.balent.flight.app.SessionDataBean;
import cz.balent.flight.app.SessionPoint;
import cz.balent.flight.app.SessionRoutePart;
/**
 * Class representing page showing report with calculated flight plan.
 * 
 * @author Robert Balent
 *
 */
public class ReportPage extends AuthPage {
	private static final long serialVersionUID = -8118487639848768631L;

	@Inject
	private SessionDataBean sessionData;

	@Inject
	private RouteBean routeBean;

	private final Model<String> routeNameModel = new Model<String>() {
		private String routeName;

		public String getObject() {
			return routeName;
		}

		public void setObject(String routeName) {
			this.routeName = routeName;
		};
	};

	public ReportPage() {
		super();
		if (!sessionData.isLoggedIn()) {
			return;
		}
		ListView<SessionRoutePart> routePartView = new ListView<SessionRoutePart>(
				"repeatedRoutePart", sessionData.getRoute().getRouteParts()) {
			@Override
			protected void populateItem(ListItem<SessionRoutePart> item) {
				SessionRoutePart routePart = item.getModelObject();

				String firstUrl = "http://www.google.com/mapfiles/marker"
						+ Character.toUpperCase(routePart.getStartPoint()
								.getSymbol()) + ".png";
				String secondUrl = "http://www.google.com/mapfiles/marker"
						+ Character.toUpperCase(routePart.getEndPoint()
								.getSymbol()) + ".png";

				WebMarkupContainer firstImg = new WebMarkupContainer(
						"firstMarker");
				WebMarkupContainer secondImg = new WebMarkupContainer(
						"secondMarker");

				firstImg.add(new AttributeModifier("src", new Model<String>(
						firstUrl)));
				secondImg.add(new AttributeModifier("src", new Model<String>(
						secondUrl)));

				item.add(firstImg);
				item.add(secondImg);

				Label courseLabel = new Label("course",
						round(routePart.getCourse()));
				item.add(courseLabel);
				Label calculatedCourseLabel = new Label("calculatedCourse",
						round(routePart.getCalculatedCourse()));
				item.add(calculatedCourseLabel);
				Label distanceLabel = new Label("distance",
						mToKm1point(routePart.getDistance()));
				item.add(distanceLabel);
				Label timeLabel = new Label("time",
						timeToTime(routePart.getTime()));
				item.add(timeLabel);
				Label windDirectionLabel = new Label("windDirection",
						routePart.getWindDirection());
				item.add(windDirectionLabel);
				Label windSpeedLabel = new Label("windSpeed",
						routePart.getWindSpeed());
				item.add(windSpeedLabel);
			}
		};
		add(routePartView);

		ListView<SessionPoint> pointsView = new ListView<SessionPoint>(
				"pointView", sessionData.getPoints()) {
			@Override
			protected void populateItem(ListItem<SessionPoint> item) {
				SessionPoint point = item.getModelObject();

				String markerImgUrl = "http://www.google.com/mapfiles/marker"
						+ Character.toUpperCase(point.getSymbol()) + ".png";
				WebMarkupContainer markerImg = new WebMarkupContainer(
						"pointMarker");
				markerImg.add(new AttributeModifier("src", new Model<String>(
						markerImgUrl)));
				item.add(markerImg);
				Label latitudeLabel = new Label("pointLatitude", latToLat(point
						.getLatLng().getLat()));
				Label longitudeLabel = new Label("pointLongitude",
						lngToLng(point.getLatLng().getLng()));

				item.add(latitudeLabel);
				item.add(longitudeLabel);
			}
		};

		add(pointsView);

		add(new Label("routeDistance", mToKm1point(sessionData.getRoute()
				.getDistance())));
		add(new Label("routeTime", timeToTime(sessionData.getRoute().getTime())));
		add(new Label("routeConsumption", roundTo1Point(sessionData.getRoute()
				.getConsumption())));

		add(new Label("twistCount1", sessionData.getRoute().getTwistCount())
				.setRenderBodyOnly(true));
		add(new Label("twistCount2", sessionData.getRoute().getTwistCount())
				.setRenderBodyOnly(true));

		add(new Label("totalTime", timeToTime(sessionData.getRoute()
				.getTotalTime())));
		add(new Label("totalConsumption", roundTo1Point(sessionData.getRoute()
				.getTotalConsumption())));

		Form saveForm = new Form("saveForm") {
			@Override
			protected void onSubmit() {
				routeBean.saveRoute(routeNameModel.getObject());
				info("Route was saved successfully");
			}
		};

		add(saveForm);

		Link printMapLink = new Link("printMap") {
			@Override
			public void onClick() {
				setResponsePage(OnlyMapPage.class);
			}
		};

		printMapLink.add(new AttributeModifier("target", new Model<String>(
				"_blank")));

		saveForm.add(printMapLink);

		routeNameModel.setObject(sessionData.getRouteName());
		saveForm.add(new TextField("routeName", routeNameModel)
				.setRequired(true));
	}

	private String mToKm1point(double meters) {
		return String.valueOf(((double) (round(meters / 100)) / 10));
	}

	private String timeToTime(double time) {
		int hour;
		int minutes;
		hour = (int) Math.floor(time);
		minutes = (int) Math.floor((time - hour) * 60);

		String s = hour + "" + minutes;
		return hour + "h " + minutes + "m";
	}

	private int round(double number) {
		return (int) (number + 0.5);
	}

	private double roundTo1Point(double number) {
		return ((double) ((int) ((number * 10) + 0.5))) / 10;
	}

	private String angleToString(double latitude) {
		int degree;
		int minutes;
		int seconds;
		degree = (int) Math.floor(latitude);
		minutes = (int) Math.floor((latitude - degree) * 60);
		seconds = (int) Math.floor((((latitude - degree) * 60) - minutes) * 60);

		if (degree < 0) {
			degree = -degree;
		}

		String d = "";
		String m = "";
		String s = "";

		if (degree < 10) {
			d = "0";
		}
		if (minutes < 10) {
			m = "0";
		}
		if (seconds < 10) {
			s = "0";
		}

		return d + degree + "° " + m + minutes + "' " + s + seconds + "\"";
	}

	private String latToLat(double latitude) {
		if (latitude > 0) {
			return angleToString(latitude) + " N";
		} else {
			return angleToString(latitude) + " S";
		}
	}

	private String lngToLng(double longitude) {
		if (longitude > 0) {
			return angleToString(longitude) + " E";
		} else {
			return angleToString(longitude) + " W";
		}
	}
}
