package cl.duoc.cdy2204.service;

import cl.duoc.cdy2204.dto.ActualizarGuiaRequest;
import cl.duoc.cdy2204.dto.CrearGuiaRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PdfGuiaService {

    @Value("${app.efs.path}")
    private String efsPath;

    public Path generarPdf(CrearGuiaRequest request) {
        return generarPdf(
                request.getNumeroGuia(),
                request.getTransportista(),
                request.getFechaGuia(),
                request.getDestinatario(),
                request.getDireccionDestino(),
                request.getDetallePedido()
        );
    }

    public Path actualizarPdf(String numeroGuia, String transportista, LocalDate fechaGuia, ActualizarGuiaRequest request) {
        return generarPdf(
                numeroGuia,
                transportista,
                fechaGuia,
                request.getDestinatario(),
                request.getDireccionDestino(),
                request.getDetallePedido()
        );
    }

    private Path generarPdf(String numeroGuia, String transportista, LocalDate fechaGuia, String destinatario, String direccionDestino, String detallePedido) {
        try {
            Files.createDirectories(Path.of(efsPath));

            String archivoNombre = limpiarNombre(numeroGuia) + ".pdf";
            Path archivo = Path.of(efsPath, archivoNombre);

            try (PDDocument documento = new PDDocument()) {
                PDPage pagina = new PDPage();
                documento.addPage(pagina);

                PDType1Font tituloFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font textoFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                    contenido.beginText();
                    contenido.setFont(tituloFont, 18);
                    contenido.newLineAtOffset(50, 730);
                    contenido.showText("Guia de despacho");
                    contenido.endText();

                    escribirLinea(contenido, textoFont, 12, 50, 690, "Numero guia: " + numeroGuia);
                    escribirLinea(contenido, textoFont, 12, 50, 670, "Transportista: " + transportista);
                    escribirLinea(contenido, textoFont, 12, 50, 650, "Fecha guia: " + fechaGuia);
                    escribirLinea(contenido, textoFont, 12, 50, 620, "Destinatario: " + destinatario);
                    escribirLinea(contenido, textoFont, 12, 50, 600, "Direccion destino: " + direccionDestino);
                    escribirLinea(contenido, textoFont, 12, 50, 570, "Detalle pedido:");

                    int y = 550;
                    for (String linea : dividirTexto(detallePedido, 80)) {
                        escribirLinea(contenido, textoFont, 11, 70, y, linea);
                        y -= 18;
                    }
                }

                documento.save(archivo.toFile());
            }

            return archivo;
        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF en EFS", e);
        }
    }

    private void escribirLinea(PDPageContentStream contenido, PDType1Font font, int size, int x, int y, String texto) throws IOException {
        contenido.beginText();
        contenido.setFont(font, size);
        contenido.newLineAtOffset(x, y);
        contenido.showText(limpiarTexto(texto));
        contenido.endText();
    }

    private String limpiarNombre(String valor) {
        return valor.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private String limpiarTexto(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[\\r\\n\\t]", " ");
    }

    private String[] dividirTexto(String texto, int largo) {
        String valor = limpiarTexto(texto);
        return valor.replaceAll("(.{1," + largo + "})(\\s+|$)", "$1\n").split("\n");
    }
}
