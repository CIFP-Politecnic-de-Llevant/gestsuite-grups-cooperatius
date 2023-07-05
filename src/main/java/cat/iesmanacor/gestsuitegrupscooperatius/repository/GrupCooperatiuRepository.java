package cat.iesmanacor.gestsuitegrupscooperatius.repository;

import cat.iesmanacor.gestsuitegrupscooperatius.model.GrupCooperatiu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupCooperatiuRepository extends JpaRepository<GrupCooperatiu, Long> {
    //List<GrupCooperatiu> findAllByUsuari(Long usuari);
}
