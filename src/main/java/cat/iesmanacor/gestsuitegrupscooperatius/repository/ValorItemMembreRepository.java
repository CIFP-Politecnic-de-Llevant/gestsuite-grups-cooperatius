package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuite.grupscooperatius.model.ValorItemMembre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorItemMembreRepository extends JpaRepository<ValorItemMembre, Long> {
}
