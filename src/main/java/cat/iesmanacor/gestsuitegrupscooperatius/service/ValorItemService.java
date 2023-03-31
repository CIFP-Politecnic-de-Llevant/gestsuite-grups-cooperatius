package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuite.grupscooperatius.model.Item;
import cat.iesmanacor.gestsuite.grupscooperatius.model.ValorItem;
import cat.iesmanacor.gestsuite.grupscooperatius.repository.ValorItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValorItemService {

    @Autowired
    private ValorItemRepository valorItemRepository;

    public ValorItem findById(Long id){
        return valorItemRepository.findById(id).get();
    }

    public List<ValorItem> findAllValorsByItem(Item item){
        return valorItemRepository.findAllByItem(item);
    }

    public ValorItem save(ValorItem valorItem){
        return valorItemRepository.save(valorItem);
    }
}

