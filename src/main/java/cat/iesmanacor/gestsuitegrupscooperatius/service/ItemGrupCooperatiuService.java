package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuite.grupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuite.grupscooperatius.model.ItemGrupCooperatiu;
import cat.iesmanacor.gestsuite.grupscooperatius.repository.ItemGrupCooperatiuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemGrupCooperatiuService {
    @Autowired
    private ItemGrupCooperatiuRepository itemGrupCooperatiuRepository;

    public ItemGrupCooperatiu save(ItemGrupCooperatiu itemGrupCooperatiu) {
        return itemGrupCooperatiuRepository.save(itemGrupCooperatiu);
    }

    public ItemGrupCooperatiu getItemGrupCooperatiuById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return itemGrupCooperatiuRepository.findById(id).get();
        //return grupCooperatiuRepository.getById(id);
    }

    public List<ItemGrupCooperatiu> findAllByGrupCooperatiu(GrupCooperatiu grupCooperatiu){
        return itemGrupCooperatiuRepository.findAllByGrupCooperatiu(grupCooperatiu);
    }


}

