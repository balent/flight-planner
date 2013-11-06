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

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.cdi.ConversationPropagation;
import org.apache.wicket.markup.html.WebPage;


/**
 * Main configuration class which configure whole web application
 * and other pages behaviour
 * 
 * @author Robert Balent
 *
 */
public class FlightWebApplication extends AuthenticatedWebApplication {

	@Override
	public Class getHomePage() {
		return InfoPage.class;
	}

	@Override
	protected void init() {
		super.init();

		BeanManager beanManager;
		try {
			beanManager = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			throw new IllegalStateException("Unable to obtain CDI BeanManager", e);
		}

		new CdiConfiguration(beanManager).setPropagation(
				ConversationPropagation.NONBOOKMARKABLE).configure(this);

		mountPage("login", LogInPage.class);
		mountPage("registration", RegistrationPage.class);
		mountPage("registered", RegisteredPage.class);
		mountPage("report", ReportPage.class);
		mountPage("create", CreateRoutePage.class);
		mountPage("user", UserPage.class);
		mountPage("plane", PlanePage.class);
		mountPage("print", OnlyMapPage.class);

		getApplicationSettings().setPageExpiredErrorPage(LogInPage.class);
		getApplicationSettings().setAccessDeniedPage(LogInPage.class);


	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LogInPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return BasicAuthenticationSession.class;
	}
}
