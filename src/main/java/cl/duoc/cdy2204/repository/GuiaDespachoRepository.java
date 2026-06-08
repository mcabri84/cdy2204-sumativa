package cl.duoc.cdy2204.repository;

import cl.duoc.cdy2204.model.GuiaDespacho;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    Optional<GuiaDespacho> findByNumeroGuia(String numeroGuia);

    List<GuiaDespacho> findByTransportistaAndFechaGuia(String transportista, LocalDate fechaGuia);

    List<GuiaDespacho> findByTransportista(String transportista);
}
