package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuite.grupscooperatius.model.Agrupament;
import cat.iesmanacor.gestsuite.grupscooperatius.model.GrupCooperatiu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgrupamentRepository extends JpaRepository<Agrupament, Long> {
    List<Agrupament> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
    void deleteAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
}
