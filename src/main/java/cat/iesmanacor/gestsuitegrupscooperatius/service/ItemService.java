package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuite.core.model.gestib.Usuari;
import cat.iesmanacor.gestsuite.grupscooperatius.model.Item;
import cat.iesmanacor.gestsuite.grupscooperatius.repository.ItemRepository;
import cat.iesmanacor.gestsuite.grupscooperatius.repository.ValorItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ValorItemRepository valorItemRepository;

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Item getItemById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return itemRepository.findById(id).get();
        //return itemRepository.getById(id);
    }

    public List<Item> findAllByUsuari(Usuari usuari){
        return itemRepository.findAllByUsuari(usuari);
    }

    public void deleteAllValorsByItem(Item item){
        valorItemRepository.deleteAllByItem(item);
    }
}

