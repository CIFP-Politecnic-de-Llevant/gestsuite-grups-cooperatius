package cat.politecnicllevant.gestsuitegrupscooperatius.repository;

import cat.politecnicllevant.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.ItemGrupCooperatiu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemGrupCooperatiuRepository extends JpaRepository<ItemGrupCooperatiu, Long> {
    List<ItemGrupCooperatiu> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
}
