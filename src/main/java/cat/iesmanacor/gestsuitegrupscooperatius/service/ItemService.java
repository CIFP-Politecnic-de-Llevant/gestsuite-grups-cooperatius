package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.AgrupamentDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ItemDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Item;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ItemRepository;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ValorItemRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ValorItemRepository valorItemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, ValorItemRepository valorItemRepository) {
        this.itemRepository = itemRepository;
        this.valorItemRepository = valorItemRepository;
    }

    public ItemDto save(ItemDto itemDto) {
        ModelMapper modelMapper = new ModelMapper();

        PropertyMap<ItemDto, Item> mapper = new PropertyMap<>() {
            protected void configure() {
                map().setUsuari(source.getUsuari().getIdusuari());
                map().getValorItems().stream().map(valorItem -> {
                    valorItem.setItem(null);
                    return valorItem;
                });
            }
        };
        modelMapper.addMappings(mapper);
        Item item = modelMapper.map(itemDto,Item.class);

        //Afegeim l'usuari manualment perquè el mapeig no és igual Usuari i UsuariDTO
        item.setUsuari(itemDto.getUsuari().getIdusuari());

        Item itemSaved = itemRepository.save(item);
        return modelMapper.map(itemSaved,ItemDto.class);
    }

    public ItemDto getItemById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        //Ha de ser findById i no getById perquè getById és Lazy
        Item item = itemRepository.findById(id).get();
        //return itemRepository.getById(id);
        return modelMapper.map(item,ItemDto.class);
    }

    public List<ItemDto> findAllByUsuari(UsuariDto usuari){
        ModelMapper modelMapper = new ModelMapper();
        return itemRepository.findAllByUsuari(usuari.getIdusuari()).stream().map(item->modelMapper.map(item, ItemDto.class)).collect(Collectors.toList());
    }

    public void deleteAllValorsByItem(ItemDto itemDto){
        ModelMapper modelMapper = new ModelMapper();
        Item item = modelMapper.map(itemDto,Item.class);
        valorItemRepository.deleteAllByItem(item);
    }
}

