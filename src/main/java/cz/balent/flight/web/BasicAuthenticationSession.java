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

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import cz.balent.flight.app.AuthBean;
/**
 * Helper method which is set to handle authentication request.
 * 
 * @author Robert Balent
 *
 */
public class BasicAuthenticationSession extends AuthenticatedWebSession {
	private static final long serialVersionUID = -4709228351735073225L;

	@Inject
	AuthBean authBean;

	public BasicAuthenticationSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String username, String password) {
		return authBean.authorize(username, password);
	}

	@Override
	public Roles getRoles() {
		return null;
	}
}
