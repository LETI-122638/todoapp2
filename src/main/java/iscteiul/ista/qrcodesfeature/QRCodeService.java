package iscteiul.ista.qrcodesfeature;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import iscteiul.ista.examplefeature.Task;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class QRCodeService {

    private static final int SIZE = 250;

    /**
     * Gera um QR code para a task recebida e devolve-o como byte[] PNG.
     */
    public byte[] generate(Task task) {
        String payload = task.getDescription()
                + System.lineSeparator()
                + (task.getDueDate() != null ? "Due: " + task.getDueDate() : "");

        try {
            BitMatrix matrix = new QRCodeWriter()
                    .encode(payload, BarcodeFormat.QR_CODE, SIZE, SIZE);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(matrix, "PNG", out);
                return out.toByteArray();
            }
        } catch (WriterException | java.io.IOException e) {
            throw new IllegalStateException("Could not generate QR code", e);
        }
    }

    /* Conveniência para gravar em disco durante desenvolvimento */
    public Path generateToFile(Task task, Path target) {
        byte[] png = generate(task);
        try {
            return Files.write(target, png);
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
    }
}