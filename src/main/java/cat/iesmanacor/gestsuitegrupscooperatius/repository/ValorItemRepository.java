package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuite.grupscooperatius.model.Item;
import cat.iesmanacor.gestsuite.grupscooperatius.model.ValorItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValorItemRepository extends JpaRepository<ValorItem, Long> {
    void deleteAllByItem(Item item);

    List<ValorItem> findAllByItem(Item item);
}
