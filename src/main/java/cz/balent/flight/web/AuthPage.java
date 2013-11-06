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

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
/**
 * This page is template page for all authenticated pages. It checks
 * it user is logged in and forbid access to users not logged in.
 * 
 * @author Robert Balent
 *
 */
public class AuthPage extends TemplatePage {
	private static final long serialVersionUID = -2694675507383111697L;

	@Override
	protected void onConfigure() {
		AuthenticatedWebApplication app = 
				(AuthenticatedWebApplication) Application.get();

		if (!AuthenticatedWebSession.get().isSignedIn()) {
			app.restartResponseAtSignInPage();
		}
	};
}
