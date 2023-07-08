package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuitegrupscooperatius.model.Agrupament;
import cat.iesmanacor.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembreRepository extends JpaRepository<Membre, Long> {
    void deleteAllByGrupCooperatiu_IdgrupCooperatiu(Long idGrupCooperatiu);
    void deleteAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
    List<Membre> findAllByGrupCooperatiu_IdgrupCooperatiu(Long idGrupCooperatiu);
    List<Membre> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
    List<Membre> findAllByAgrupament(Agrupament agrupament);
    List<Membre> findAllByNom(String nom);
    void deleteByAmicsContains(Membre membre);
    void deleteByAmicsIsContaining(Membre membre);
    void deleteByEnemicsContains(Membre membre);
    void deleteByEnemicsIsContaining(Membre membre);

    @Query(value="DELETE FROM im_membre_amics WHERE membre_id=?1", nativeQuery = true)
    void deleteAmics(Long idMembre);

    @Query(value="DELETE FROM im_membre_enemics WHERE membre_id=?1", nativeQuery = true)
    void deleteEnemics(Long idMembre);
}
