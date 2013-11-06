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

import javax.inject.Inject;

import cz.balent.flight.dao.PlaneManager;
import cz.balent.flight.dao.UserManager;
import cz.balent.flight.model.Plane;
import cz.balent.flight.model.User;
/**
 * Bean is responsible for adding user to database 
 * and creating default plane.
 * 
 * @author Robert Balent
 *
 */
public class RegistrationBean implements Serializable {

	private static final long serialVersionUID = -2193095543721129113L;

	@Inject
	UserManager userManager;

	@Inject
	PlaneManager planeManager;

	public Boolean registerUser(String username, String password) {
		User user = new User();
		user.setId(username);
		user.setPassword(password);
		user = userManager.create(user);

		if (user == null) {
			return false;
		}

		// adding default plane
		Plane plane = new Plane();
		plane.setName("WT-9 Dynamic");
		plane.setConsumption(30);
		plane.setSpeed(100);
		plane.setTankVolume(200);
		plane.setUser(user);
		planeManager.createPlane(plane);

		return true;
	}
}
