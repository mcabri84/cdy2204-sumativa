package cl.duoc.cdy2204.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CrearGuiaRequest {

    @NotBlank
    private String numeroGuia;

    @NotBlank
    private String transportista;

    @NotNull
    private LocalDate fechaGuia;

    @NotBlank
    private String destinatario;

    @NotBlank
    private String direccionDestino;

    @NotBlank
    private String detallePedido;

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
}
