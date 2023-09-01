package cat.politecnicllevant.gestsuitegrupscooperatius.repository;

import cat.politecnicllevant.gestsuitegrupscooperatius.model.GrupCooperatiu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupCooperatiuRepository extends JpaRepository<GrupCooperatiu, Long> {
    //List<GrupCooperatiu> findAllByUsuari(Long usuari);
}
