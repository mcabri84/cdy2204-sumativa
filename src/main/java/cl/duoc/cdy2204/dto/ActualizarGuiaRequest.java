package cl.duoc.cdy2204.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarGuiaRequest {

    @NotBlank
    private String destinatario;

    @NotBlank
    private String direccionDestino;

    @NotBlank
    private String detallePedido;

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
