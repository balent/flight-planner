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

import org.apache.wicket.markup.html.WebPage;
import org.wicketstuff.gmap.GMap;
import org.wicketstuff.gmap.api.GIcon;
import org.wicketstuff.gmap.api.GLatLng;
import org.wicketstuff.gmap.api.GMarker;
import org.wicketstuff.gmap.api.GMarkerOptions;
import org.wicketstuff.gmap.api.GPolyline;

import cz.balent.flight.app.SessionDataBean;
import cz.balent.flight.app.SessionPoint;
/**
 * Class representin page which shows page with map only. Ready for print.
 * 
 * @author Robert Balent
 *
 */
public class OnlyMapPage extends WebPage {
	private static final long serialVersionUID = -6572947643036332558L;

	@Inject
	private SessionDataBean sessionData;

	private final GMap map = new GMap("map");

	public OnlyMapPage() {
		super();

		if (!sessionData.isLoggedIn()) {
			setResponsePage(LogInPage.class);
			return;
		}

		map.setVersioned(false);

		map.setStreetViewControlEnabled(false);
		map.setScrollWheelZoomEnabled(true);
		map.setDoubleClickZoomEnabled(false);

		map.setCenter(sessionData.getMapPosition());
		map.setZoom(sessionData.getMapZoom());
		map.setOutputMarkupId(true);
		add(map);
		drawMap();
	}

	private void drawMap() {
		SessionPoint lastPoint = null;
		for (SessionPoint point : sessionData.getPoints()) {
			GLatLng latLng = point.getLatLng();
			char symbol = point.getSymbol();
			final GMarker marker = new GMarker(new GMarkerOptions(map, latLng,
					null, new GIcon(symbol), null));

			if (lastPoint != null) {
				map.addOverlay(new GPolyline("red", 3, 0.5F, lastPoint
						.getLatLng(), point.getLatLng()));
			}

			lastPoint = point;

			map.addOverlay(marker);
		}
	}
}
