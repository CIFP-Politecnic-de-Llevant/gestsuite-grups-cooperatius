package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.AgrupamentDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ItemDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ValorItemDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Item;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItem;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ValorItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValorItemService {

    @Autowired
    private ValorItemRepository valorItemRepository;

    public ValorItemDto findById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        ValorItem valorItem = valorItemRepository.findById(id).get();
        return modelMapper.map(valorItem,ValorItemDto.class);
    }

    public List<ValorItemDto> findAllValorsByItem(ItemDto itemDto){
        ModelMapper modelMapper = new ModelMapper();
        Item item = modelMapper.map(itemDto, Item.class);

        return valorItemRepository.findAllByItem(item).stream().map(valorItem->modelMapper.map(valorItem, ValorItemDto.class)).collect(Collectors.toList());
    }

    public ValorItemDto save(ValorItemDto valorItemDto){
        ModelMapper modelMapper = new ModelMapper();
        ValorItem valorItem = modelMapper.map(valorItemDto,ValorItem.class);
        ValorItem valorItemSaved = valorItemRepository.save(valorItem);
        return modelMapper.map(valorItemSaved,ValorItemDto.class);
    }
}

