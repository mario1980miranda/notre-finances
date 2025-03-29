package com.code.truck.finances.app.infrastructure.web.view;

import com.code.truck.finances.app.infrastructure.application.TransactionService;
import com.code.truck.finances.app.infrastructure.application.TransactionSummaryService;
import com.code.truck.finances.app.infrastructure.application.UserService;
import com.code.truck.finances.app.infrastructure.application.dto.TransactionDTO;
import com.code.truck.finances.app.infrastructure.application.dto.TransactionSummaryDTO;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import com.code.truck.finances.app.infrastructure.web.FinancesUI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Route(value = "summary", layout = FinancesUI.class)
@PageTitle("Transaction summary")
public class TransactionSummaryView extends VerticalLayout {

    private final TransactionService transactionService;
    private final TransactionSummaryService transactionSummaryService;
    private final UserService userService;

    private final H2 balanceHeader = new H2("Current Balance");
    private final Div balanceValue = new Div();

    private final Grid<MonthlyData> incomeGrid = new Grid<>();
    private final Grid<MonthlyData> expenseGrid = new Grid<>();

    private final UserDTO currentUser;

    @Autowired
    public TransactionSummaryView(
            TransactionService transactionService,
            TransactionSummaryService transactionSummaryService,
            UserService userService
    ) {
        this.transactionService = transactionService;
        this.transactionSummaryService = transactionSummaryService;
        this.userService = userService;

        this.currentUser = userService.getUserById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        setSizeFull();
        configureLayout();
        loadData();
    }

    private void configureLayout() {
        balanceValue.addClassName("balance-value");

        H3 incomeHeader = new H3("Monthly Income");
        configureGrid(incomeGrid, "Income");

        H3 expenseHeader = new H3("Monthly Expenses");
        configureGrid(expenseGrid, "Expense");

        add(
                balanceHeader,
                balanceValue,
                incomeHeader,
                incomeGrid,
                expenseHeader,
                expenseGrid
        );
    }

    private void configureGrid(Grid<MonthlyData> grid, String amountHeader) {
        grid.addColumn(data -> data.yearMonth().format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                .setHeader("Month")
                .setSortable(true);

        grid.addColumn(new NumberRenderer<>(
                        MonthlyData::amount,
                        NumberFormat.getCurrencyInstance(Locale.US)))
                .setHeader(amountHeader)
                .setSortable(true);

        grid.setHeight("200px");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void loadData() {
        List<TransactionDTO> transactions = transactionService.getTransactionsByUser(currentUser);

        TransactionSummaryDTO summary = transactionSummaryService.generateSummary(transactions);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.CANADA);
        String formattedBalance = currencyFormat.format(summary.getBalance());

        if (summary.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            balanceValue.setText(formattedBalance);
            balanceValue.getStyle().set("color", "var(--lumo-error-color)");
        } else {
            balanceValue.setText(formattedBalance);
            balanceValue.getStyle().set("color", "var(--lumo-success-color)");
        }

        List<MonthlyData> incomeData = convertMapToList(summary.getMonthlyIncomes());
        incomeGrid.setItems(incomeData);

        List<MonthlyData> expenseData = convertMapToList(summary.getMonthlyExpenses());
        expenseGrid.setItems(expenseData);
    }

    private List<MonthlyData> convertMapToList(Map<YearMonth, BigDecimal> map) {
        List<MonthlyData> list = new ArrayList<>();

        map.forEach((month, amount) ->
                list.add(new MonthlyData(month, amount)));

        list.sort(Comparator.comparing(MonthlyData::yearMonth).reversed());

        return list;
    }

    private record MonthlyData(YearMonth yearMonth, BigDecimal amount) {

    }
}
