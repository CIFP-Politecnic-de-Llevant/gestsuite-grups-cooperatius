package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuite.grupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuite.grupscooperatius.model.Membre;
import cat.iesmanacor.gestsuite.grupscooperatius.repository.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembreService {
    @Autowired
    private MembreRepository membreRepository;


    public Membre save(Membre membre) {

        return membreRepository.save(membre);
    }

    public Membre getMembreById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return membreRepository.findById(id).get();
        //return itemRepository.getById(id);
    }

    public void deleteByGrupCooperatiu(GrupCooperatiu grupCooperatiu){
        membreRepository.deleteAllByGrupCooperatiu(grupCooperatiu);
    }

}

