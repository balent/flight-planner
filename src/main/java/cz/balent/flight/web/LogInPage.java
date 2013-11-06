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
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;

import cz.balent.flight.app.SessionDataBean;
import cz.balent.flight.dao.PlaneManager;
import cz.balent.flight.dao.UserManager;
/**
 * Class representing log in page.
 * 
 * @author Robert Balent
 *
 */
public class LogInPage extends TemplatePage {
	private static final long serialVersionUID = 4300235653628667122L;

	private String username;
	private String password;

	@Inject
	private SessionDataBean sessionData;
	@Inject
	private UserManager userManager;
	@Inject
	private PlaneManager planeManager;
	
	public LogInPage() {
		super();
		setVersioned(false);

		StatelessForm form = new StatelessForm("form") {
			@Override
			protected void onSubmit() {
				if (Strings.isEmpty(username))
					return;
				boolean authResult = AuthenticatedWebSession.get().signIn(username, password);
				if (authResult) {
					sessionData.setLoggedIn(true);
					sessionData.setUser(userManager.getUser(username));
					
					setResponsePage(CreateRoutePage.class);
				} else {
					info("Wrong username or password!");
				}
			}
		};
		
		form.setDefaultModel(new CompoundPropertyModel(this));
		form.add(new TextField("username").setRequired(true));
		form.add(new PasswordTextField("password"));
		add(form);
	}
}
