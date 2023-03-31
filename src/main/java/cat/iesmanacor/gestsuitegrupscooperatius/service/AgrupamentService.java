package cat.iesmanacor.gestsuitegrupscooperatius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AgrupamentService {
    @Autowired
    private AgrupamentRepository agrupamentRepository;


    public List<AgrupamentDto> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu){
        return agrupamentRepository.findAllByGrupCooperatiu(grupCooperatiu);
    }

    public AgrupamentDto save(AgrupamentDto agrupament) {
        return agrupamentRepository.save(agrupament);
    }

    public AgrupamentDto getAgrupamentById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return agrupamentRepository.findById(id).get();
        //return agrupamentRepository.getById(id);
    }

    public void deleteByGrupCooperatiu(GrupCooperatiuDto grupCooperatiu){
        agrupamentRepository.deleteAllByGrupCooperatiu(grupCooperatiu);
    }
}

