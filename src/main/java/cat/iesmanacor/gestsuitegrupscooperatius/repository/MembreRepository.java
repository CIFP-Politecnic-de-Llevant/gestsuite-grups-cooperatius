package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuite.grupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuite.grupscooperatius.model.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembreRepository extends JpaRepository<Membre, Long> {
    void deleteAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu);
}
