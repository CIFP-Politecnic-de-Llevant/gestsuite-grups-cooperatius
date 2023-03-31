package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuite.grupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuite.grupscooperatius.model.ItemGrupCooperatiu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemGrupCooperatiuRepository extends JpaRepository<ItemGrupCooperatiu, Long> {
    List<ItemGrupCooperatiu> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
}
