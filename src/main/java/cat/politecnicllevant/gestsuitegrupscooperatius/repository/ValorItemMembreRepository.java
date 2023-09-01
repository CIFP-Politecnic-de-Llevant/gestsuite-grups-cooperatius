package cat.politecnicllevant.gestsuitegrupscooperatius.repository;

import cat.politecnicllevant.gestsuitegrupscooperatius.model.Membre;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.ValorItem;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.ValorItemMembre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValorItemMembreRepository extends JpaRepository<ValorItemMembre, Long> {
    ValorItemMembre findByMembreAndValorItem(Membre membre, ValorItem valorItem);
    List<ValorItemMembre> findAllByMembre(Membre membre);
    void deleteAllByMembre(Membre membre);
    ValorItemMembre findByMembre_IdmembreAndValorItem_IdvalorItem(Long idMembre, Long idValorItem);
}
