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

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;

/**
 * This plane is template for each other page. It centrally handles pages design
 * and the links of the menu. Checks if is the user logged in or logged out
 * and shows only relevant links. 
 * 
 * @author Robert Balent
 *
 */
public class TemplatePage extends WebPage {
	private static final long serialVersionUID = -896709476775505045L;

    private static final JavaScriptResourceReference BOOTSTRAP_JS = new JavaScriptResourceReference(TemplatePage.class, "resources/js/bootstrap.min.js");
    private static final UrlResourceReference JQUERY_JS = new UrlResourceReference(Url.parse("https://code.jquery.com/jquery.js"));
    private static final PackageResourceReference BOOTSTRAP_CSS = new PackageResourceReference(TemplatePage.class, "resources/css/bootstrap.css");
    private static final PackageResourceReference TEMPLATE_CSS = new PackageResourceReference(TemplatePage.class, "resources/template/starter-template.css");
//    private static final PackageResourceReference CSS_REFERENCE = new PackageResourceReference(TemplatePage.class, "resources/style.css");

	public TemplatePage() {
		super();

        StatelessLink infoLink = new StatelessLink("info") {
            @Override
            public void onClick() {
                setResponsePage(InfoPage.class);
            }
        };
        add(infoLink);

        StatelessLink infoLink2 = new StatelessLink("info2") {
            @Override
            public void onClick() {
                setResponsePage(InfoPage.class);
            }
        };
        add(infoLink2);

		StatelessLink createRouteLink = new StatelessLink("createRoute") {
			@Override
			public void onClick() {
				setResponsePage(CreateRoutePage.class);
			}
		};
		add(createRouteLink);

		StatelessLink reportLink = new StatelessLink("report") {
			@Override
			public void onClick() {
				setResponsePage(ReportPage.class);
			}
		};
		add(reportLink);

		StatelessLink userPageLink = new StatelessLink("userPage") {
			@Override
			public void onClick() {
				setResponsePage(UserPage.class);
			}
		};
		add(userPageLink);

		StatelessLink planePageLink = new StatelessLink("planePage") {
			@Override
			public void onClick() {
				setResponsePage(PlanePage.class);
			}
		};
		add(planePageLink);

		StatelessLink logInPageLink = new StatelessLink("logInPage") {
			@Override
			public void onClick() {
				setResponsePage(LogInPage.class);
			}
		};
		add(logInPageLink);

		StatelessLink registerPageLink = new StatelessLink("registerPage") {
			@Override
			public void onClick() {
				setResponsePage(RegistrationPage.class);
			}
		};
		add(registerPageLink);

		StatelessLink logoutLink = new StatelessLink("logout") {
			@Override
			public void onClick() {
				AuthenticatedWebSession.get().invalidate();
				setResponsePage(InfoPage.class);
			}
		};
		add(logoutLink);

		FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setMaxMessages(1);
		add(feedbackPanel);

		if (AuthenticatedWebSession.get().isSignedIn()) {
			logInPageLink.setVisible(false);
			registerPageLink.setVisible(false);
		} else {
			createRouteLink.setVisible(false);
			reportLink.setVisible(false);
			userPageLink.setVisible(false);
			planePageLink.setVisible(false);
			logoutLink.setVisible(false);
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
//        response.render(CssHeaderItem.forReference(CSS_REFERENCE));
        response.render(JavaScriptHeaderItem.forReference(JQUERY_JS));
        response.render(JavaScriptHeaderItem.forReference(BOOTSTRAP_JS));
        response.render(CssHeaderItem.forReference(BOOTSTRAP_CSS));
        response.render(CssHeaderItem.forReference(TEMPLATE_CSS));

	}
}
