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

import org.wicketstuff.gmap.api.GLatLng;
/**
 * Represents one point in route with temporary session data like
 * wind speed, direction and symbol on map.
 * 
 * @author Robert Balent
 *
 */
public class SessionPoint implements Serializable {
	private static final long serialVersionUID = 7757604814505092601L;
	private String id;
	private GLatLng latLng;
	private int windSpeed;
	private int windDirection;
	private char symbol;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GLatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(GLatLng latLng) {
		this.latLng = latLng;
	}

	public int getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(int windSpeed) {
		this.windSpeed = windSpeed;
	}

	public int getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(int windDirection) {
		this.windDirection = windDirection;
	}

	public char getSymbol() {
		return symbol;
	}

	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}
}
