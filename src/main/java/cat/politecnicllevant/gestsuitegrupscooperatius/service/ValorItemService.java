package cat.politecnicllevant.gestsuitegrupscooperatius.service;

import cat.politecnicllevant.gestsuitegrupscooperatius.dto.ItemDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.dto.ValorItemDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.Item;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.ValorItem;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.ValorItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValorItemService {

    @Autowired
    private ValorItemRepository valorItemRepository;

    public ValorItemDto findById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        ValorItem valorItem = valorItemRepository.findById(id).orElse(null);
        if(valorItem == null){
            return null;
        }
        return modelMapper.map(valorItem,ValorItemDto.class);
    }

    public List<ValorItemDto> findAllValorsByItem(ItemDto itemDto){
        ModelMapper modelMapper = new ModelMapper();
        /*PropertyMap<ItemDto, Item> mapper = new PropertyMap<>() {
            protected void configure() {
                map().setUsuari(source.getUsuari().getIdusuari());
            }
        };
        modelMapper.addMappings(mapper);*/

        Item item = modelMapper.map(itemDto, Item.class);

        return valorItemRepository.findAllByItem(item).stream().map(valorItem->modelMapper.map(valorItem, ValorItemDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public ValorItemDto save(ValorItemDto valorItemDto){
        ModelMapper modelMapper = new ModelMapper();
        ValorItem valorItem = modelMapper.map(valorItemDto,ValorItem.class);
        ValorItem valorItemSaved = valorItemRepository.save(valorItem);
        return modelMapper.map(valorItemSaved,ValorItemDto.class);
    }
}

