package cat.politecnicllevant.gestsuitegrupscooperatius.service;

import cat.politecnicllevant.gestsuitegrupscooperatius.dto.ItemDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.Item;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.ItemRepository;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.ValorItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ItemDto save(ItemDto itemDto) {
        ModelMapper modelMapper = new ModelMapper();

        /*PropertyMap<ItemDto, Item> mapper = new PropertyMap<>() {
            protected void configure() {
                map().setUsuari(source.getUsuari().getIdusuari());
            }
        };
        modelMapper.addMappings(mapper);*/
        Item item = modelMapper.map(itemDto,Item.class);

        //Afegeim l'usuari manualment perquè el mapeig no és igual Usuari i UsuariDTO
        //item.setUsuari(itemDto.getUsuari().getIdusuari());

        Item itemSaved = itemRepository.save(item);
        return modelMapper.map(itemSaved,ItemDto.class);
    }

    public ItemDto getItemById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        //Ha de ser findById i no getById perquè getById és Lazy
        Item item = itemRepository.findById(id).get();
        //return itemRepository.getById(id);
        ItemDto itemDto = modelMapper.map(item,ItemDto.class);
        //UsuariDto usuariDto = new UsuariDto();
        //usuariDto.setIdusuari(item.getUsuari());
        //itemDto.setUsuari(usuariDto);
        return itemDto;
    }

    public List<ItemDto> findAllByUsuari(UsuariDto usuari){
        ModelMapper modelMapper = new ModelMapper();
        //return itemRepository.findAllByUsuari(usuari.getIdusuari()).stream().map(item->modelMapper.map(item, ItemDto.class)).collect(Collectors.toList());
        return itemRepository.findAll().stream().map(item->modelMapper.map(item, ItemDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public void deleteAllValorsByItem(ItemDto itemDto){
        ModelMapper modelMapper = new ModelMapper();
        Item item = modelMapper.map(itemDto,Item.class);
        valorItemRepository.deleteAllByItem(item);
    }
}

