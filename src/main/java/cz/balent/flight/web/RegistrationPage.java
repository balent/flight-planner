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

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.balent.flight.app.RegistrationBean;
/**
 * Class representing page which shows registration form.
 * 
 * @author Robert Balent
 *
 */
public class RegistrationPage extends TemplatePage {
	private static final long serialVersionUID = -357560348830248790L;

	private String username;
	private String password;
	private String repeatPassword;

	@Inject
	private RegistrationBean registrationBean;
	
	public RegistrationPage() {
		super();
		setVersioned(false);

		StatelessForm form = new StatelessForm("form") {
			@Override
			protected void onSubmit() {
				boolean success = registrationBean.registerUser(username, password);
				if (!success) {
					info("User already registered.");
					return;
				}

				setResponsePage(RegisteredPage.class);
			}
		};

		form.setDefaultModel(new CompoundPropertyModel(this));

		TextField userTextField = new TextField("username");
		PasswordTextField passwordTextField = new PasswordTextField("password");
		PasswordTextField repeatPasswordTextField = new PasswordTextField("repeatPassword");

		userTextField.setRequired(true);
		passwordTextField.setRequired(true);
		repeatPasswordTextField.setRequired(true);

		form.add(userTextField);
		form.add(passwordTextField);
		form.add(repeatPasswordTextField);

		form.add(new EqualPasswordInputValidator(passwordTextField, repeatPasswordTextField));

		add(form);
	}
}
