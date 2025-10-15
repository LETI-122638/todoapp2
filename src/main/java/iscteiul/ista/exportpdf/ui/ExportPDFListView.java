package iscteiul.ista.exportpdf.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import iscteiul.ista.exportpdf.ExportPDF;
import iscteiul.ista.exportpdf.ExportPDFService;

import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.UI;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Route("PDF")
@PageTitle("Export PDF")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Export PDF")
public class ExportPDFListView extends Main {

    private final ExportPDFService exportPDFService;

    private final TextField fileNameField;
    private final TextField bodyField;
    private final Button exportBtn;
    private final Grid<ExportPDF> taskGrid;

    public ExportPDFListView(ExportPDFService exportPDFService) {
        this.exportPDFService = exportPDFService;

        // === Campos de PDF ===
        fileNameField = new TextField("Nome do ficheiro");
        fileNameField.setPlaceholder("Ex: relatorio.pdf");
        fileNameField.setWidth("300px");

        bodyField = new TextField("Texto do PDF");
        bodyField.setPlaceholder("Escreve o texto a incluir no PDF");
        bodyField.setWidth("500px");

        exportBtn = new Button("Gerar e Exportar PDF");
        exportBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportBtn.addClickListener(e -> onExportPDF());

        // === Grid de tarefas ===
        taskGrid = new Grid<>(ExportPDF.class, false);
        taskGrid.setSizeFull();

        // === Layout ===
        setSizeFull();
        add(fileNameField, bodyField, exportBtn, taskGrid);
    }

    private void onExportPDF() {
        try {
            String fileName = fileNameField.getValue().isBlank() ? "documento.pdf" : fileNameField.getValue();
            if (!fileName.toLowerCase().endsWith(".pdf")) fileName += ".pdf";
            String body = bodyField.getValue().isBlank() ? "(Sem conteúdo)" : bodyField.getValue();

            // === Gerar PDF em memória com OpenPDF ===
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            var bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph(fileName.replace(".pdf", ""), titleFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(body, bodyFont));

            // Opcional: adicionar todas as tarefas no PDF
            document.add(new Paragraph("\nTarefas na base de dados:", titleFont));
            for (ExportPDF task : exportPDFService.list(null)) {
                String line = task.getDescription() + " | Limite: " +
                        (task.getDueDate() != null ? task.getDueDate().toString() : "Never");
                document.add(new Paragraph(line, bodyFont));
            }

            document.close();

            // === Forçar download no browser via Blob ===
            byte[] pdfBytes = baos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(pdfBytes);
            String safeFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

            UI.getCurrent().getPage().executeJs(
                    "const bytes = Uint8Array.from(atob($0), c => c.charCodeAt(0));" +
                            "const blob = new Blob([bytes], {type: 'application/pdf'});" +
                            "const link = document.createElement('a');" +
                            "link.href = URL.createObjectURL(blob);" +
                            "link.download = $1;" +
                            "document.body.appendChild(link);" +
                            "link.click();" +
                            "document.body.removeChild(link);" +
                            "URL.revokeObjectURL(link.href);",
                    base64, safeFileName
            );

            Notification.show("PDF criado com sucesso!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        } catch (Exception ex) {
            Notification.show("Erro ao criar PDF: " + ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
