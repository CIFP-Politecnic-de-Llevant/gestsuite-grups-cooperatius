package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItem;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItemMembre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValorItemMembreRepository extends JpaRepository<ValorItemMembre, Long> {
    ValorItemMembre findByMembreAndValorItem(Membre membre, ValorItem valorItem);
    List<ValorItemMembre> findAllByMembre(Membre membre);
}
