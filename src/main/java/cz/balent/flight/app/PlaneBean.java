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

import javax.inject.Inject;

import cz.balent.flight.dao.PlaneManager;
import cz.balent.flight.model.Plane;
/**
 * PlaneBean handles retrieving and deleting planes
 * 
 * @author Robert Balent
 *
 */
public class PlaneBean implements Serializable {
	private static final long serialVersionUID = -1889707084132442407L;

	@Inject
	PlaneManager planeManager;
	@Inject
	SessionDataBean sessionData;

	public List<String> getPlaneNames() {
		List<String> planeNames = new ArrayList<String>();
		List<Plane> planes = planeManager.getUserPlanes(sessionData.getUser());

		for (Plane plane : planes) {
			planeNames.add(plane.getName());
		}
		return planeNames;
	}

	public Plane getPlane(String planeName) {
		return planeManager.getUserPlane(sessionData.getUser(), planeName);
	}

	public void deletePlane(String planeName) {
		planeManager.deleteUserPlane(sessionData.getUser(), planeName);
	}
}
