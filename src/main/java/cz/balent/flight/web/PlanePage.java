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

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.balent.flight.app.SessionDataBean;
import cz.balent.flight.dao.PlaneManager;
import cz.balent.flight.model.Plane;
/**
 * Class representing page when user can edit his plane.
 * 
 * @author Robert Balent
 *
 */
public class PlanePage extends AuthPage implements Serializable {
	private static final long serialVersionUID = -5021209233628555002L;
	
	@Inject
	SessionDataBean sessionData;
	
	@Inject
	PlaneManager planeManager;
	
	private String planeName = "New Plane";
	private int consumption = 30;
	private int tankVolume = 200;
	private int speed = 100;
	
	public PlanePage() {
		if (sessionData.getUser() == null) {
			return;
		}
		if (sessionData.editPlane()) {
			planeName = sessionData.getActualPlane().getName();
			consumption = sessionData.getActualPlane().getConsumption();
			tankVolume = sessionData.getActualPlane().getTankVolume();
			speed = sessionData.getActualPlane().getSpeed();
		}
		
		
		Form planeForm = new Form("planeForm") {
			@Override
			protected void onSubmit() {
				Plane plane = new Plane();
				plane.setUser(sessionData.getUser());
				plane.setName(planeName);
				plane.setConsumption(consumption);
				plane.setTankVolume(tankVolume);
				plane.setSpeed(speed);
				planeManager.savePlane(plane);
				sessionData.setActualPlane(plane);
				info("Plane was saved succesfully!");
			}
		};
		
		add(planeForm);
		
		TextField planeNameField = new TextField("planeName", new PropertyModel(this, "planeName"));
		
		if (sessionData.editPlane()) {
			planeNameField.setEnabled(false);
			sessionData.setEditPlane(false);
		}
		
		TextField<Integer> consumptionField = new TextField<Integer>("consumption", new PropertyModel(this, "consumption"));
		TextField<Integer> tankVolumeField = new TextField<Integer>("tankVolume", new PropertyModel(this, "tankVolume"));
		TextField<Integer> speedField = new TextField<Integer>("speed", new PropertyModel(this, "speed"));
		
		planeNameField.setRequired(true);
		consumptionField.setRequired(true);
		tankVolumeField.setRequired(true);
		speedField.setRequired(true);
		
		RangeValidator<Integer> rv = new RangeValidator<Integer>() {
			@Override
			public Integer getMinimum() {
				return 0;
			}
		};
		consumptionField.add(rv);
		tankVolumeField.add(rv);
		speedField.add(rv);
		
		planeForm.add(planeNameField);
		planeForm.add(consumptionField);
		planeForm.add(tankVolumeField);
		planeForm.add(speedField);
	}
	
}
