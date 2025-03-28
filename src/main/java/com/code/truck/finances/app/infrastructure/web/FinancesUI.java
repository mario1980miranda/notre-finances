package com.code.truck.finances.app.infrastructure.web;

import com.code.truck.finances.app.infrastructure.web.view.TransactionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

@Route("")
public class FinancesUI extends AppLayout {

    public FinancesUI() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Finance Tracker");
        logo.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
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
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        return tab;
    }

//    @Override
//    protected void onDetach(DetachEvent detachEvent) {
//        // Properly cleanup Vaadin resources when UI is detached
//        UI ui = getUI().orElse(null);
//        if (ui != null) {
//            ui.getPage().executeJs("window.onbeforeunload = null;");
//        }
//
//        VaadinSession session = getSession();
//        if (session != null && session.getSession() != null) {
//            session.close();
//        }
//
//        super.onDetach(detachEvent);
//    }
}
