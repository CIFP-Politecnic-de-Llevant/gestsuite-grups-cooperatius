package cat.politecnicllevant.gestsuitegrupscooperatius.repository;

import cat.politecnicllevant.gestsuitegrupscooperatius.model.Agrupament;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.GrupCooperatiu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgrupamentRepository extends JpaRepository<Agrupament, Long> {
    List<Agrupament> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
    void deleteAllByGrupCooperatiu_IdgrupCooperatiu(Long idGrupCooperatiu);
    void deleteAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
}
