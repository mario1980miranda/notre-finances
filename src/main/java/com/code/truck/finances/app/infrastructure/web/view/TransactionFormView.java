package com.code.truck.finances.app.infrastructure.web.view;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.TransactionType;
import com.code.truck.finances.app.infrastructure.application.TransactionService;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionFormView extends FormLayout {

    private final TransactionService transactionService;
    private final UserDTO currentUser;

    private TextField description = new TextField("Description");
    private BigDecimalField amount = new BigDecimalField("Amount");
    private DatePicker date = new DatePicker("Date");
    private ComboBox<TransactionType> type = new ComboBox<>("Type");

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");

    private Binder<Transaction> binder = new Binder<>(Transaction.class);
    private Runnable afterSaveCallback;

    public TransactionFormView(TransactionService transactionService, UserDTO user) {
        this.transactionService = transactionService;
        this.currentUser = user;

        configureForm();
        configureBinder();

        add(description, amount, date, type, createButtonLayout());
    }

    private void configureForm() {
        setWidth("25em");

        type.setItems(TransactionType.values());
        date.setValue(LocalDate.now());
    }

    private void configureBinder() {
        binder.bindInstanceFields(this);
    }

    private HorizontalLayout createButtonLayout() {
        save.addThemeVariants();
        cancel.addThemeVariants();

        save.addClickListener(event -> save());
        cancel.addClickListener(event -> cancel());

        return new HorizontalLayout(save, cancel);
    }

    private void save() {
        try {
            String descriptionValue = description.getValue();
            BigDecimal amountValue = amount.getValue();
            LocalDate dateValue = date.getValue();
            TransactionType typeValue = type.getValue();

            if (descriptionValue == null || amountValue == null || dateValue == null || typeValue == null) {
                Notification.show("Please fill all fields");
                return;
            }

            transactionService.createTransaction(descriptionValue, amountValue, dateValue, typeValue, currentUser);
            Notification.show("Transaction saved");

            if (afterSaveCallback != null) {
                afterSaveCallback.run();
            }

            clearForm();
        } catch (Exception e) {
            Notification.show("Error saving transaction: " + e.getMessage());
        }
    }

    private void cancel() {
        clearForm();
    }

    private void clearForm() {
        description.clear();
        amount.clear();
        date.setValue(LocalDate.now());
        type.clear();
    }

    public void setAfterSaveCallback(Runnable afterSaveCallback) {
        this.afterSaveCallback = afterSaveCallback;
    }
}
