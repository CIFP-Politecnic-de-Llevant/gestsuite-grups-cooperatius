package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuitegrupscooperatius.model.Agrupament;
import cat.iesmanacor.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembreRepository extends JpaRepository<Membre, Long> {
    void deleteAllByGrupCooperatiu_IdgrupCooperatiu(Long idGrupCooperatiu);
    void deleteAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
    List<Membre> findAllByGrupCooperatiu_IdgrupCooperatiu(Long idGrupCooperatiu);
    List<Membre> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
    List<Membre> findAllByAgrupament(Agrupament agrupament);
    void deleteByAmicsContains(Membre membre);
    void deleteByAmicsIsContaining(Membre membre);
    void deleteByEnemicsContains(Membre membre);
    void deleteByEnemicsIsContaining(Membre membre);
}
