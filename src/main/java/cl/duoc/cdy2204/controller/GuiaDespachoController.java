package cl.duoc.cdy2204.controller;

import cl.duoc.cdy2204.dto.ActualizarGuiaRequest;
import cl.duoc.cdy2204.dto.CrearGuiaRequest;
import cl.duoc.cdy2204.model.GuiaDespacho;
import cl.duoc.cdy2204.service.GuiaDespachoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    private final GuiaDespachoService service;

    public GuiaDespachoController(GuiaDespachoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GuiaDespacho> crearGuia(@Valid @RequestBody CrearGuiaRequest request) {
        return ResponseEntity.ok(service.crearGuia(request));
    }

    @PutMapping("/{numeroGuia}")
    public ResponseEntity<GuiaDespacho> actualizarGuia(
            @PathVariable String numeroGuia,
            @Valid @RequestBody ActualizarGuiaRequest request
    ) {
        return ResponseEntity.ok(service.actualizarGuia(numeroGuia, request));
    }

    @GetMapping
    public ResponseEntity<List<GuiaDespacho>> listarGuias() {
        return ResponseEntity.ok(service.listarGuias());
    }

    @GetMapping("/{numeroGuia}")
    public ResponseEntity<GuiaDespacho> buscarPorNumero(@PathVariable String numeroGuia) {
        return ResponseEntity.ok(service.buscarPorNumero(numeroGuia));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<GuiaDespacho>> buscarPorTransportistaYFecha(
            @RequestParam String transportista,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        if (fecha == null) {
            return ResponseEntity.ok(service.buscarPorTransportista(transportista));
        }

        return ResponseEntity.ok(service.buscarPorTransportistaYFecha(transportista, fecha));
    }

    @GetMapping("/{numeroGuia}/descargar")
    public ResponseEntity<byte[]> descargarGuia(
            @PathVariable String numeroGuia,
            @RequestParam(required = false) String transportista
    ) {
        byte[] archivo = service.descargarGuia(numeroGuia, transportista);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(numeroGuia + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }

    @DeleteMapping("/{numeroGuia}")
    public ResponseEntity<GuiaDespacho> eliminarGuia(@PathVariable String numeroGuia) {
        return ResponseEntity.ok(service.eliminarGuia(numeroGuia));
    }
}
