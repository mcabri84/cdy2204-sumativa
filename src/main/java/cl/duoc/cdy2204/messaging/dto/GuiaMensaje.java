package cl.duoc.cdy2204.messaging.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class GuiaMensaje {

    private String mensajeId;
    private Long guiaId;
    private String numeroGuia;
    private String transportista;
    private LocalDate fechaGuia;
    private String archivoNombre;
    private String rutaEfs;
    private String rutaS3;
    private String estado;
    private String destinatario;
    private String direccionDestino;
    private String detallePedido;
    private String tipoEvento;
    private LocalDateTime fechaEvento;

    public GuiaMensaje() {
        this.mensajeId = UUID.randomUUID().toString();
        this.fechaEvento = LocalDateTime.now();
    }

    public String getMensajeId() {
        return mensajeId;
    }

    public void setMensajeId(String mensajeId) {
        this.mensajeId = mensajeId;
    }

    public Long getGuiaId() {
        return guiaId;
    }

    public void setGuiaId(Long guiaId) {
        this.guiaId = guiaId;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public void setNumeroGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public LocalDate getFechaGuia() {
        return fechaGuia;
    }

    public void setFechaGuia(LocalDate fechaGuia) {
        this.fechaGuia = fechaGuia;
    }

    public String getArchivoNombre() {
        return archivoNombre;
    }

    public void setArchivoNombre(String archivoNombre) {
        this.archivoNombre = archivoNombre;
    }

    public String getRutaEfs() {
        return rutaEfs;
    }

    public void setRutaEfs(String rutaEfs) {
        this.rutaEfs = rutaEfs;
    }

    public String getRutaS3() {
        return rutaS3;
    }

    public void setRutaS3(String rutaS3) {
        this.rutaS3 = rutaS3;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getDetallePedido() {
        return detallePedido;
    }

    public void setDetallePedido(String detallePedido) {
        this.detallePedido = detallePedido;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String toJson() {
        return "{"
                + "\"mensajeId\":" + texto(mensajeId) + ","
                + "\"guiaId\":" + numero(guiaId) + ","
                + "\"numeroGuia\":" + texto(numeroGuia) + ","
                + "\"transportista\":" + texto(transportista) + ","
                + "\"fechaGuia\":" + texto(fechaGuia == null ? null : fechaGuia.toString()) + ","
                + "\"archivoNombre\":" + texto(archivoNombre) + ","
                + "\"rutaEfs\":" + texto(rutaEfs) + ","
                + "\"rutaS3\":" + texto(rutaS3) + ","
                + "\"estado\":" + texto(estado) + ","
                + "\"destinatario\":" + texto(destinatario) + ","
                + "\"direccionDestino\":" + texto(direccionDestino) + ","
                + "\"detallePedido\":" + texto(detallePedido) + ","
                + "\"tipoEvento\":" + texto(tipoEvento) + ","
                + "\"fechaEvento\":" + texto(fechaEvento == null ? null : fechaEvento.toString())
                + "}";
    }

    private String numero(Long valor) {
        return valor == null ? "null" : valor.toString();
    }

    private String texto(String valor) {
        if (valor == null) {
            return "null";
        }

        return "\"" + escapar(valor) + "\"";
    }

    private String escapar(String valor) {
        return valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
