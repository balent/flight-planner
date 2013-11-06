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
import cz.balent.flight.model.User;
/**
 * Get user from UserManager and check if the password match.
 * 
 * @author Robert Balent
 *
 */
public class AuthBean implements Serializable {
	private static final long serialVersionUID = 3967838734210507659L;

	@Inject
	UserManager userManager;
	@Inject
	PlaneManager planeManager;

	public boolean authorize(String username, String password) {
		User user = userManager.getUser(username);

		if (user != null && password.equals(user.getPassword())) {
			return true;
		}
		return false;
	}
}
