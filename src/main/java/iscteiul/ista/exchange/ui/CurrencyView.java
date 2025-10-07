package iscteiul.ista.exchange.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import iscteiul.ista.exchange.Currency;
import iscteiul.ista.exchange.CurrencyService;

import java.math.BigDecimal;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("currency")
@PageTitle("Currency")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Currency")
public class CurrencyView extends Main {

    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "JPY", "CHF", "BRL"};

    private final CurrencyService service;
    private final ComboBox<String> from;
    private final ComboBox<String> to;
    private final NumberField amount;
    private final Button convert;
    private final Grid<Currency> historyGrid;

    public CurrencyView(CurrencyService service) {
        this.service = service;

        from = new ComboBox<>("De", CURRENCIES);
        to = new ComboBox<>("Para", CURRENCIES);
        amount = new NumberField("Valor");
        amount.setStep(1.0);
        amount.setClearButtonVisible(true);
        amount.setMin(0.0);

        convert = new Button("Converter", e -> onConvert());
        convert.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        historyGrid = new Grid<>(Currency.class, false);
        historyGrid.setItems(query -> service.list(toSpringPageRequest(query)).stream());
        historyGrid.addColumn(Currency::getFromCurrency).setHeader("De").setAutoWidth(true);
        historyGrid.addColumn(Currency::getToCurrency).setHeader("Para").setAutoWidth(true);
        historyGrid.addColumn(c -> toStringOrEmpty(c.getAmount())).setHeader("Valor").setAutoWidth(true);
        historyGrid.addColumn(c -> toStringOrEmpty(c.getResult())).setHeader("Resultado").setAutoWidth(true);
        historyGrid.setSizeFull();

        setSizeFull();
        add(from, to, amount, convert, historyGrid);
    }

    private void onConvert() {
        try {
            var f = from.getValue();
            var t = to.getValue();
            var v = amount.getValue();

            if (f == null || t == null || v == null) {
                Notification.show("Preenche 'De', 'Para' e 'Valor'");
                return;
            }

            var currency = service.createConversion(f, t, BigDecimal.valueOf(v));
            Notification.show("Resultado: " + currency.getResult() + " " + t);
            historyGrid.getDataProvider().refreshAll();
            clearForm();
        } catch (Exception ex) {
            Notification.show("Erro na conversão: " + ex.getMessage());
        }
    }

    private void clearForm() {
        from.clear();
        to.clear();
        amount.clear();
    }

    private static String toStringOrEmpty(BigDecimal v) {
        return Optional.ofNullable(v).map(BigDecimal::toPlainString).orElse("");
    }
}
