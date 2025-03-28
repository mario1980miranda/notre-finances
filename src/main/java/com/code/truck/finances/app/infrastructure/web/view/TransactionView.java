package com.code.truck.finances.app.infrastructure.web.view;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.infrastructure.application.TransactionService;
import com.code.truck.finances.app.infrastructure.application.UserService;
import com.code.truck.finances.app.infrastructure.application.dto.TransactionDTO;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import com.code.truck.finances.app.infrastructure.web.FinancesUI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Route(value = "transactions", layout = FinancesUI.class)
public class TransactionView extends VerticalLayout {
    private final TransactionService transactionService;
    private final UserService userService;

    private Grid<TransactionDTO> grid = new Grid<>(TransactionDTO.class);
    private TransactionFormView form;

    // For demonstration purposes - in a real app, this would come from authentication
    private UserDTO currentUser;

    @Autowired
    public TransactionView(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;

        // For demo purposes - in a real app, get user from security context
        this.currentUser = userService.getUserById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        setSizeFull();

        configureGrid();

        form = new TransactionFormView(transactionService, currentUser);
        form.setAfterSaveCallback(this::updateList);

        HorizontalLayout mainContent = new HorizontalLayout(grid, form);
        mainContent.setSizeFull();

        add(getToolbar(), mainContent);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("description", "amount", "date", "type");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addTransactionButton = new Button("Add Transaction");
        addTransactionButton.addClickListener(click -> {
            form.setVisible(true);
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(click -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(addTransactionButton, refreshButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(transactionService.getTransactionsByUser(currentUser));
    }
}
