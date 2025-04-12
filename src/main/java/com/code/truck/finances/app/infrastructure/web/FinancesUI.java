package com.code.truck.finances.app.infrastructure.web;

import com.code.truck.finances.app.infrastructure.application.UserService;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import com.code.truck.finances.app.infrastructure.web.view.TransactionSummaryView;
import com.code.truck.finances.app.infrastructure.web.view.TransactionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@CssImport("./styles/styles.css")
@PermitAll
public class FinancesUI extends AppLayout {

    private final UserService userService;

    @Autowired
    public FinancesUI(UserService userService) {

        this.userService = userService;

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Finance Tracker");
        logo.addClassNames("text-l", "m-m");

        // Get current user
        UserDTO currentUser = userService.getCurrentUser();
        Span userGreeting = new Span("Welcome, " + currentUser.getUsername());

        Button logoutButton = new Button("Logout", e -> {
            getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"));
        });

        HorizontalLayout userInfo = new HorizontalLayout(userGreeting, logoutButton);
        userInfo.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, userInfo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        Tabs tabs = getTabs();
        addToDrawer(tabs);
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(createTab("Transactions", TransactionView.class));
        tabs.add(createTab("Summary", TransactionSummaryView.class));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        return tab;
    }
}
