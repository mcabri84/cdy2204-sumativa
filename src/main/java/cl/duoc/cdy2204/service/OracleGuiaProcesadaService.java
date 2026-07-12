package cl.duoc.cdy2204.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.sql.Date;
import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OracleGuiaProcesadaService {

    private final JdbcTemplate oracleJdbcTemplate;
    private final ObjectMapper objectMapper;

    public OracleGuiaProcesadaService(
            @Qualifier("oracleJdbcTemplate")
            JdbcTemplate oracleJdbcTemplate,
            ObjectMapper objectMapper) {

        this.oracleJdbcTemplate = oracleJdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public String guardarMensaje(String payloadJson) throws Exception {
        JsonNode json = objectMapper.readTree(payloadJson);

        String mensajeId = textoRequerido(json, "mensajeId");
        String numeroGuia = textoRequerido(json, "numeroGuia");
        String tipoEvento = textoRequerido(json, "tipoEvento");

        String sql = """
                INSERT INTO ADMIN.GUIAS_PROCESADAS (
                    MENSAJE_ID,
                    GUIA_ID,
                    NUMERO_GUIA,
                    TRANSPORTISTA,
                    FECHA_GUIA,
                    ARCHIVO_NOMBRE,
                    RUTA_EFS,
                    RUTA_S3,
                    ESTADO,
                    DESTINATARIO,
                    DIRECCION_DESTINO,
                    DETALLE_PEDIDO,
                    TIPO_EVENTO,
                    FECHA_EVENTO,
                    PAYLOAD_JSON
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                """;

        try {
            oracleJdbcTemplate.update(
                    sql,
                    mensajeId,
                    numero(json, "guiaId"),
                    numeroGuia,
                    texto(json, "transportista"),
                    fecha(json, "fechaGuia"),
                    texto(json, "archivoNombre"),
                    texto(json, "rutaEfs"),
                    texto(json, "rutaS3"),
                    texto(json, "estado"),
                    texto(json, "destinatario"),
                    texto(json, "direccionDestino"),
                    texto(json, "detallePedido"),
                    tipoEvento,
                    fechaHora(json, "fechaEvento"),
                    payloadJson
            );

            return "CREADA";

        } catch (DataIntegrityViolationException e) {
            String mensajeError = e.getMostSpecificCause().getMessage();

            if (mensajeError != null
                    && mensajeError.contains("ORA-00001")) {
                return "DUPLICADA";
            }

            throw e;
        }
    }

    private String textoRequerido(
            JsonNode json,
            String campo) {

        String valor = texto(json, campo);

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio"
            );
        }

        return valor;
    }

    private String texto(
            JsonNode json,
            String campo) {

        JsonNode valor = json.get(campo);

        if (valor == null || valor.isNull()) {
            return null;
        }

        return valor.asText();
    }

    private Long numero(
            JsonNode json,
            String campo) {

        JsonNode valor = json.get(campo);

        if (valor == null || valor.isNull()) {
            return null;
        }

        return valor.asLong();
    }

    private Date fecha(
            JsonNode json,
            String campo) {

        String valor = texto(json, campo);

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return Date.valueOf(valor);
    }

    private Timestamp fechaHora(
            JsonNode json,
            String campo) {

        String valor = texto(json, campo);

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return Timestamp.valueOf(
                valor.replace('T', ' ')
        );
    }
}
