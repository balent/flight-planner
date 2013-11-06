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

import static org.junit.Assert.*;

import org.junit.Test;

public class SessionRoutePartTest {

	@Test
	public void testCalculateFlightDirection() {
		SessionRoutePart routePart = new SessionRoutePart();

		// north-east
		double result1 = routePart.calculateFlightDirection(20, 20, 30, 30);
		
		// south-east
		double result2 = routePart.calculateFlightDirection(20, 20, 10, 30);
		
		// south-west
		double result3 = routePart.calculateFlightDirection(20, 20, 10, 10);
		
		// north-east
		double result4 = routePart.calculateFlightDirection(20, 20, 30, 10);
		
		// north
		double result5 = routePart.calculateFlightDirection(20, 20, 30, 20);
		
		// east
		double result6 = routePart.calculateFlightDirection(0, 0, 0, 30);
		
		// south
		double result7 = routePart.calculateFlightDirection(20, 20, 10, 20);
		
		// west
		double result8 = routePart.calculateFlightDirection(0, 20, 0, 10);
		
		// some random
		double result9 = routePart.calculateFlightDirection(49.643, 14.914, 49.636, 17.007);
		
		assertTrue(result1 > 0 && result1 < 90);
		assertTrue(result2 > 90 && result2 < 180);
		assertTrue(result3 > 180 && result3 < 270);
		assertTrue(result4 > 270 && result4 < 360);
		assertEquals(0, result5, 0.00001);
		assertEquals(90, result6, 0.00001);
		assertEquals(180, result7, 0.00001);
		assertEquals(270, result8, 0.00001);
		assertTrue(result9 > 0 && result9 < 180);
	}
}
