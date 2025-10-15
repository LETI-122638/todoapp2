package iscteiul.ista.exportpdf;

import org.openpdf.text.*;
import org.openpdf.text.pdf.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ExportPDFService {

    private final ExportPDFRepository exportPDFRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ExportPDFService(ExportPDFRepository exportPDFRepository) {
        this.exportPDFRepository = exportPDFRepository;
    }

    @Transactional
    public void createTask(String description, @Nullable LocalDate dueDate) {
        var task = new ExportPDF(description, Instant.now());
        task.setDueDate(dueDate);
        exportPDFRepository.saveAndFlush(task);
    }

    @Transactional(readOnly = true)
    public List<ExportPDF> list(Pageable pageable) {
        return exportPDFRepository.findAllBy(pageable).toList();
    }

    @Transactional(readOnly = true)
    public byte[] exportAllTasksToPDF() {
        List<ExportPDF> tasks = exportPDFRepository.findAll();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph title = new Paragraph("Lista de Tarefas",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 3});
            addHeaderCell(table, "Descrição");
            addHeaderCell(table, "Data Limite");
            addHeaderCell(table, "Data de Criação");

            for (ExportPDF task : tasks) {
                table.addCell(task.getDescription());
                table.addCell(Optional.ofNullable(task.getDueDate())
                        .map(DATE_FORMATTER::format).orElse("—"));
                table.addCell(DATETIME_FORMATTER.format(task.getCreationDate().atZone(ZoneId.systemDefault())));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(220, 220, 220));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
