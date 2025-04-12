package com.code.truck.finances.app.infrastructure.web.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(
                new H1("Finance Tracker"),
                new Paragraph("Manage your personal finances"),
                createLoginLink()
        );
    }

    private Anchor createLoginLink() {
        Anchor loginLink = new Anchor("/oauth2/authorization/google", "Login with Google");
        loginLink.getElement().setAttribute("router-ignore", true);
        loginLink.addClassName("login-button");
        // Make sure the browser does a full page load for this link
        loginLink.getElement().setAttribute("target", "_self");
        return loginLink;
    }
}
