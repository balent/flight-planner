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

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.wicketstuff.gmap.GMap;
import org.wicketstuff.gmap.api.GEvent;
import org.wicketstuff.gmap.api.GEventHandler;
import org.wicketstuff.gmap.api.GIcon;
import org.wicketstuff.gmap.api.GLatLng;
import org.wicketstuff.gmap.api.GMarker;
import org.wicketstuff.gmap.api.GMarkerOptions;
import org.wicketstuff.gmap.api.GOverlay;
import org.wicketstuff.gmap.api.GPolyline;
import org.wicketstuff.gmap.event.ClickListener;
import org.wicketstuff.gmap.event.DragEndListener;
import org.wicketstuff.gmap.event.ZoomChangedListener;

import cz.balent.flight.app.PlaneBean;
import cz.balent.flight.app.SessionDataBean;
import cz.balent.flight.app.SessionPoint;
/**
 * Class representing page responsible for creating route.
 * 
 * @author Robert Balent
 *
 */
public class CreateRoutePage extends AuthPage {
	private static final long serialVersionUID = -1885544014639754160L;

	@Inject
	private SessionDataBean sessionData;
	@Inject
	PlaneBean planeBean;
	
	private List<String> planeNames;
	private final Model<Boolean> roundTripCheck = new Model<Boolean>();
	private final Model<String> planeModel = new Model<String>();
	
	private final GMap map = new GMap("map");
	private final WebMarkupContainer repeaterParent = new WebMarkupContainer("repeaterParent");
	
	public CreateRoutePage() {
		super();
		
		if(!sessionData.isLoggedIn()) {
			return;
		}
		
		planeNames = planeBean.getPlaneNames();
		if (planeNames.size() > 0) {
			planeModel.setObject(planeNames.get(0));
		}
		
		Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				if (sessionData.getPoints().size() == 0) {
					info("Add some points to route");
					return;
				}
				if (planeModel.getObject() == null) {
					info("Please create plane before calculating route");
					return;
				}
				
				sessionData.setRoundTrip(roundTripCheck.getObject());
				sessionData.setActualPlane(planeBean.getPlane(planeModel.getObject()));
				sessionData.calculate();
				
				setResponsePage(ReportPage.class);
			}
		};
		
		add(form);
		
		repeaterParent.setOutputMarkupId(true);
		
		form.add(repeaterParent);
		
		final ListView<SessionPoint> rv = new ListView<SessionPoint>("repeatingView", sessionData.getPoints()) {
			private static final long serialVersionUID = 5435524464071694866L;

			@Override
			protected void populateItem(ListItem<SessionPoint> item) {
				final SessionPoint point = item.getModelObject();
				String imageUrl = "http://www.google.com/mapfiles/marker" + Character.toUpperCase(point.getSymbol()) + ".png";
								
				
				WebMarkupContainer imgContainer = new WebMarkupContainer("markerImage"); 
				imgContainer.add(new AttributeModifier("src", new Model<String>(imageUrl))); 
				
				item.add(imgContainer);
				
				TextField speedTextField = new TextField("windSpeed", new PropertyModel(point, "windSpeed"));
				TextField directionTextField = new TextField("windDirection", new PropertyModel(point, "windDirection"));
				
				RangeValidator<Integer> directionValidator = new RangeValidator<Integer>(0, 359);
				directionTextField.add(directionValidator);
				
				RangeValidator<Integer> speedValidator = new RangeValidator<Integer>() {
					public Integer getMinimum() {
						return 0;
					};
				};
				speedTextField.add(speedValidator);
				
				item.add(speedTextField);
				item.add(directionTextField);
				Form form = new Form("deleteForm");
				form.setOutputMarkupPlaceholderTag(false);
				item.add(form);
				
				AjaxSubmitLink deleteButton = new AjaxSubmitLink("deletePoint") {
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form form) {
						removePoint(point);
						redrawLines();
						target.add(repeaterParent);
					}
				};
				
				form.add(deleteButton);
			}
		};
		
		roundTripCheck.setObject(sessionData.isRoundTrip());
		form.add(new CheckBox("roundTripCheck", roundTripCheck));
		DropDownChoice dropDownPlanes = new DropDownChoice("planes", planeModel, planeNames);
		form.add(dropDownPlanes);
		
		rv.setOutputMarkupId(true);
		repeaterParent.add(rv);

		map.setStreetViewControlEnabled(false);
		map.setScaleControlEnabled(false);
		map.setScrollWheelZoomEnabled(true);
		map.setDoubleClickZoomEnabled(false);
		
		map.setCenter(sessionData.getMapPosition());
		map.setZoom(sessionData.getMapZoom());
		map.setOutputMarkupId(true);
		
		add(map);	

		map.add(new ClickListener() {
			@Override
			protected void onClick (AjaxRequestTarget target, GLatLng latLng) {
				
				
				if (latLng != null) {
					if (sessionData.getPoints().size() == 0) {
						sessionData.setNextSymbol('a');
					}

					char newSymbol = getNewSymbol();
					final GMarker marker = addMarkerToMap(latLng, newSymbol);
					
					if (sessionData.getPoints().size() != 0) {
						map.addOverlay(new GPolyline("red", 3, 0.5F, sessionData.getPoints().get(sessionData.getPoints().size() - 1).getLatLng(), marker.getLatLng()));
					}
					
					SessionPoint point = new SessionPoint();
					point.setLatLng(marker.getLatLng());
					point.setId(marker.getId());
					point.setSymbol(newSymbol);
					
					sessionData.addPoint(point);
					
					target.add(repeaterParent);
				}

			}
		});
		
		map.add(new DragEndListener() {
			
			@Override
			protected void onDragEnd(AjaxRequestTarget target) {
				sessionData.setMapPosition(map.getCenter());
			}
		});
		
		map.add(new ZoomChangedListener() {
			
			@Override
			protected void onZoomChanged(AjaxRequestTarget target) {
				sessionData.setMapZoom(map.getZoom());
			}
		});
		
		redrawMap();
	}
	
	private void redrawMap() {
		map.removeAllOverlays();
		SessionPoint lastPoint = null;
		for (SessionPoint point: sessionData.getPoints()) {
			GLatLng latLng = point.getLatLng();
			
			char newSymbol = point.getSymbol();
			
			GMarker marker = addMarkerToMap(latLng, newSymbol);			
			
			if (lastPoint != null) {
				map.addOverlay(new GPolyline("red", 3, 0.5F, lastPoint.getLatLng(), point.getLatLng()));
			}
			lastPoint = point;
			
			point.setId(marker.getId());
		}
	}
	
	private void redrawLines() {
		for (GOverlay overlay: map.getOverlays()) {
			if (overlay instanceof GPolyline) {
				map.removeOverlay(overlay);
			}
		}
		SessionPoint lastPoint = null;
		for (SessionPoint point: sessionData.getPoints()) {
			if (lastPoint != null) {
				map.addOverlay(new GPolyline("red", 3, 0.5F, lastPoint.getLatLng(), point.getLatLng()));
			}
			lastPoint = point;
		}
	}
	
	private void removePoint(SessionPoint point) {
		sessionData.getPoints().remove(point);
		for (GOverlay overlay: map.getOverlays()) {
			if (overlay instanceof GMarker) {
				GMarker marker = (GMarker)overlay;
				if (equalsDoubleTolerant(marker.getLatLng().getLat(), point.getLatLng().getLat()) && equalsDoubleTolerant(marker.getLatLng().getLng(), point.getLatLng().getLng())) {
					map.removeOverlay(overlay);
				}
			}
		}
		if (sessionData.getPoints().size() == 0) {
			sessionData.setNextSymbol('a');
		}
	}
	
	private void removePoint(GMarker marker) {
		SessionPoint pointToRemove = null;
		for(SessionPoint point: sessionData.getPoints()) {
			if (equalsDoubleTolerant(marker.getLatLng().getLat(), point.getLatLng().getLat()) && equalsDoubleTolerant(marker.getLatLng().getLng(), point.getLatLng().getLng())) {
				pointToRemove = point;
			}
		}
		sessionData.getPoints().remove(pointToRemove);
		map.removeOverlay(marker);
		if (sessionData.getPoints().size() == 0) {
			sessionData.setNextSymbol('a');
		}
	}
	
	private char getNewSymbol() {
		char returnSymbol = sessionData.getNextSymbol();
		if (sessionData.getNextSymbol() == 'z') {
			sessionData.setNextSymbol('a');
		} else {
			sessionData.setNextSymbol((char)(sessionData.getNextSymbol() + 1));
		}
		return returnSymbol;
	}
	
	private boolean equalsDoubleTolerant(double firstDouble, double secondDouble) {
		 double value = firstDouble - secondDouble;
		 return (value < 0.00001 && value > -0.00001);
	}
	
	private GMarker addMarkerToMap(GLatLng latLng, char newSymbol) {

		GMarkerOptions gMarkerOptions = 
				new GMarkerOptions(map, latLng, null, new GIcon(newSymbol),null).draggable(true);
		
		final GMarker marker = new GMarker(gMarkerOptions);
		
		map.addOverlay(marker);
		
		marker.addListener(GEvent.dragend, new GEventHandler() {
			@Override
			public void onEvent(AjaxRequestTarget target) {
				for (SessionPoint point : sessionData.getPoints()) {
					if (point.getId().equals(marker.getId())) {
						point.setLatLng(marker.getLatLng());
					}
				}
				redrawLines();
				target.add(repeaterParent);
			}
		});
		
		marker.addListener(GEvent.dblclick, new GEventHandler() {
			
		    @Override
		    public void onEvent(AjaxRequestTarget target) {
		        removePoint(marker); // remove point from map
		        redrawLines(); // redraw lines in map
		        target.add(repeaterParent); // update dynamic form
			}
		});
		
		return marker;
	}
}
