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

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import cz.balent.flight.app.PlaneBean;
import cz.balent.flight.app.RouteBean;
import cz.balent.flight.app.SessionDataBean;
/**
 * Class representing page which shows information about user and his
 * stored planes and routes.
 * 
 * @author Robert Balent
 *
 */
public class UserPage extends AuthPage implements Serializable {
	private static final long serialVersionUID = -1967881726435022961L;

	@Inject
	private RouteBean routeBean;
	@Inject
	private PlaneBean planeBean;
	@Inject
	private SessionDataBean sessionData;

	private List<String> routeNames = routeBean.getRouteNames();
	private List<String> planeNames = planeBean.getPlaneNames();

	private final Model<String> routeChoiceModel = new Model<String>();
	private final Model<String> planeChoiceModel = new Model<String>();
	
	public UserPage() {
		super();
		if (sessionData.getUser() == null) {
			return;
		}
		if (routeNames.size() > 0) {
			routeChoiceModel.setObject(routeNames.get(0));
		}
		if (planeNames.size() > 0) {
			planeChoiceModel.setObject(planeNames.get(0));
		}

		Form routeForm = new Form("routeForm") {
			@Override
			protected void onSubmit() {

			}
		};

		Button newRouteButton = new Button("newRoute") {
			@Override
			public void onSubmit() {
				sessionData.clearPoints();
				sessionData.setRouteName("");
				setResponsePage(CreateRoutePage.class);
			}
		};
		Button editRouteButton = new Button("editRoute") {
			@Override
			public void onSubmit() {
				if (routeChoiceModel.getObject() != null) {
					routeBean.loadRoute(routeChoiceModel.getObject());
					setResponsePage(CreateRoutePage.class);
				} else {
					info("You have no saved route, create one!");
				}
			}
		};
		Button deleteRouteButton = new Button("deleteRoute") {
			@Override
			public void onSubmit() {
				if (routeChoiceModel.getObject() != null) {
					routeBean.deleteRoute(planeChoiceModel.getObject());
					setResponsePage(UserPage.class);
				} else {
					info("Can't delete. You have no routes.");
				}
			}
		};

		add(routeForm);

		routeForm.add(newRouteButton);
		routeForm.add(editRouteButton);
		routeForm.add(deleteRouteButton);

		DropDownChoice dropDownRoutes = new DropDownChoice("routeNames",
				routeChoiceModel, routeNames);

		routeForm.add(dropDownRoutes);

		Label nameLabel = new Label("userName", sessionData.getUser().getId());
		nameLabel.setRenderBodyOnly(true);
		add(nameLabel);

		Form planeForm = new Form("planeForm");
		add(planeForm);

		DropDownChoice dropDownPlanes = new DropDownChoice("planeNames",
				planeChoiceModel, planeNames);
		planeForm.add(dropDownPlanes);

		Button newPlaneButton = new Button("newPlane") {
			@Override
			public void onSubmit() {
				setResponsePage(PlanePage.class);
			}
		};
		Button editPlaneButton = new Button("editPlane") {
			@Override
			public void onSubmit() {
				if (planeChoiceModel.getObject() != null) {
					sessionData.setActualPlane(planeBean
							.getPlane(planeChoiceModel.getObject()));
					sessionData.setEditPlane(true);
					setResponsePage(PlanePage.class);
				} else {
					info("Can't edit. You have no planes.");
				}
			}
		};
		Button deletePlaneButton = new Button("deletePlane") {
			@Override
			public void onSubmit() {
				if (planeChoiceModel.getObject() != null) {
					if (sessionData.getActualPlane() != null
							&& planeChoiceModel.getObject().equals(
									sessionData.getActualPlane().getName())) {
						sessionData.setActualPlane(null);
					}
					planeBean.deletePlane(planeChoiceModel.getObject());

					setResponsePage(UserPage.class);
				} else {
					info("Can't delete. You have no planes.");
				}
			}
		};
		planeForm.add(newPlaneButton);
		planeForm.add(editPlaneButton);
		planeForm.add(deletePlaneButton);
	}

}
