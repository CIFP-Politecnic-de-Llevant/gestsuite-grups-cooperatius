package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuite.core.model.gestib.Usuari;
import cat.iesmanacor.gestsuite.grupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuite.grupscooperatius.repository.GrupCooperatiuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupCooperatiuService {
    @Autowired
    private GrupCooperatiuRepository grupCooperatiuRepository;

    public GrupCooperatiu save(GrupCooperatiu grupCooperatiu) {
        return grupCooperatiuRepository.save(grupCooperatiu);
    }

    public GrupCooperatiu getGrupCooperatiuById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return grupCooperatiuRepository.findById(id).get();
        //return grupCooperatiuRepository.getById(id);
    }

    public List<GrupCooperatiu> findAllByUsuari(Usuari usuari){
        return grupCooperatiuRepository.findAllByUsuari(usuari);
    }
}

