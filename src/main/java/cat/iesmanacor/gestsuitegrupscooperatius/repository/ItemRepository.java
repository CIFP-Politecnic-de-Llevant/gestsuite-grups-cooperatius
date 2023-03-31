package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuitegrupscooperatius.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUsuari(Long usuari);
}
