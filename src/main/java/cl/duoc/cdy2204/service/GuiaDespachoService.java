package cl.duoc.cdy2204.service;

import cl.duoc.cdy2204.dto.ActualizarGuiaRequest;
import cl.duoc.cdy2204.dto.CrearGuiaRequest;
import cl.duoc.cdy2204.messaging.producer.GuiaMensajeProducer;
import cl.duoc.cdy2204.model.GuiaDespacho;
import cl.duoc.cdy2204.repository.GuiaDespachoRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuiaDespachoService {

    private final GuiaDespachoRepository repository;
    private final PdfGuiaService pdfGuiaService;
    private final S3GuiaService s3GuiaService;
    private final GuiaMensajeProducer guiaMensajeProducer;

    public GuiaDespachoService(
            GuiaDespachoRepository repository,
            PdfGuiaService pdfGuiaService,
            S3GuiaService s3GuiaService,
            GuiaMensajeProducer guiaMensajeProducer
    ) {
        this.repository = repository;
        this.pdfGuiaService = pdfGuiaService;
        this.s3GuiaService = s3GuiaService;
        this.guiaMensajeProducer = guiaMensajeProducer;
    }

    @Transactional
    public GuiaDespacho crearGuia(CrearGuiaRequest request) {
        repository.findByNumeroGuia(request.getNumeroGuia()).ifPresent(guia -> {
            throw new RuntimeException("La guia ya existe");
        });

        Path archivoPdf = pdfGuiaService.generarPdf(request);
        String archivoNombre = archivoPdf.getFileName().toString();
        String rutaS3 = s3GuiaService.generarRutaS3(
                request.getFechaGuia().toString(),
                request.getTransportista(),
                archivoNombre
        );

        s3GuiaService.subirArchivo(archivoPdf, rutaS3);

        GuiaDespacho guia = new GuiaDespacho();
        guia.setNumeroGuia(request.getNumeroGuia());
        guia.setTransportista(request.getTransportista());
        guia.setFechaGuia(request.getFechaGuia());
        guia.setArchivoNombre(archivoNombre);
        guia.setRutaEfs(archivoPdf.toString());
        guia.setRutaS3(rutaS3);
        guia.setEstado("SUBIDA_S3");

        GuiaDespacho guardada = repository.save(guia);

        guiaMensajeProducer.enviar(
                guardada,
                "GUIA_CREADA",
                request.getDestinatario(),
                request.getDireccionDestino(),
                request.getDetallePedido()
        );

        return guardada;
    }

    @Transactional
    public GuiaDespacho actualizarGuia(
            String numeroGuia,
            ActualizarGuiaRequest request
    ) {
        GuiaDespacho guia = buscarPorNumero(numeroGuia);

        Path archivoPdf = pdfGuiaService.actualizarPdf(
                guia.getNumeroGuia(),
                guia.getTransportista(),
                guia.getFechaGuia(),
                request
        );

        s3GuiaService.subirArchivo(archivoPdf, guia.getRutaS3());

        guia.setRutaEfs(archivoPdf.toString());
        guia.setEstado("ACTUALIZADA_S3");

        GuiaDespacho guardada = repository.save(guia);

        guiaMensajeProducer.enviar(
                guardada,
                "GUIA_ACTUALIZADA",
                request.getDestinatario(),
                request.getDireccionDestino(),
                request.getDetallePedido()
        );

        return guardada;
    }

    @Transactional(readOnly = true)
    public byte[] descargarGuia(
            String numeroGuia,
            String transportista
    ) {
        GuiaDespacho guia = buscarPorNumero(numeroGuia);

        if (
                transportista != null
                        && !transportista.isBlank()
                        && !guia.getTransportista().equalsIgnoreCase(transportista)
        ) {
            throw new RuntimeException(
                    "No tiene permisos para descargar esta guia"
            );
        }

        return s3GuiaService.descargarArchivo(guia.getRutaS3());
    }

    @Transactional
    public GuiaDespacho eliminarGuia(String numeroGuia) {
        GuiaDespacho guia = buscarPorNumero(numeroGuia);

        try {
            if (guia.getRutaS3() != null && !guia.getRutaS3().isBlank()) {
                s3GuiaService.eliminarArchivo(guia.getRutaS3());
            }
        } catch (Exception e) {
            System.out.println(
                    "No se pudo eliminar archivo en S3 por restriccion IAM del laboratorio: "
                            + e.getMessage()
            );
        }

        if (guia.getRutaEfs() != null && !guia.getRutaEfs().isBlank()) {
            try {
                Files.deleteIfExists(Path.of(guia.getRutaEfs()));
            } catch (Exception e) {
                throw new RuntimeException(
                        "Error al eliminar archivo temporal en EFS",
                        e
                );
            }
        }

        guia.setEstado("ELIMINADA");

        GuiaDespacho guardada = repository.save(guia);

        guiaMensajeProducer.enviar(
                guardada,
                "GUIA_ELIMINADA",
                null,
                null,
                null
        );

        return guardada;
    }


    @Transactional(readOnly = true)
    public GuiaDespacho enviarGuiaACola(String numeroGuia) {
        GuiaDespacho guia = buscarPorNumero(numeroGuia);

        guiaMensajeProducer.enviar(
                guia,
                "GUIA_REENVIADA",
                null,
                null,
                null
        );

        return guia;
    }

    @Transactional(readOnly = true)
    public List<GuiaDespacho> listarGuias() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GuiaDespacho> buscarPorTransportista(
            String transportista
    ) {
        return repository.findByTransportista(transportista);
    }

    @Transactional(readOnly = true)
    public List<GuiaDespacho> buscarPorTransportistaYFecha(
            String transportista,
            LocalDate fecha
    ) {
        return repository.findByTransportistaAndFechaGuia(
                transportista,
                fecha
        );
    }

    @Transactional(readOnly = true)
    public GuiaDespacho buscarPorNumero(String numeroGuia) {
        return repository.findByNumeroGuia(numeroGuia)
                .orElseThrow(
                        () -> new RuntimeException("Guia no encontrada")
                );
    }
}
