package iscteiul.ista.qrcodesfeature.ui;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import iscteiul.ista.base.ui.component.ViewToolbar;
import iscteiul.ista.examplefeature.Task;
import iscteiul.ista.examplefeature.TaskService;
import iscteiul.ista.qrcodesfeature.QRCodeService;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("qrcodes")
@PageTitle("QR Codes")
@Menu(order = 1, icon = "vaadin:qr-code", title = "QR Codes")
public class QRCodeListView extends Main {

    public QRCodeListView(TaskService taskService, QRCodeService qrCodeService) {
        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL);

        var grid = new com.vaadin.flow.component.grid.Grid<>(Task.class, false);
        grid.setItems(q -> taskService.list(toSpringPageRequest(q)).stream());

        grid.addColumn(Task::getDescription).setHeader("Description");
        grid.addColumn(Task::getDueDate).setHeader("Due date");

        grid.addColumn(new ComponentRenderer<>(task -> {
            byte[] png = qrCodeService.generate(task);
            StreamResource res = new StreamResource("qr.png", () -> new java.io.ByteArrayInputStream(png));
            res.setContentType("image/png");
            Image img = new Image(res, "QR");
            img.setWidth("100px");
            img.setHeight("100px");
            return img;
        })).setHeader("QR");

        var layout = new VerticalLayout(new ViewToolbar("QR Codes"), grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
    }
}