package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.AgrupamentDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.GrupCooperatiuDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ItemGrupCooperatiuDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ItemGrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ItemGrupCooperatiuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemGrupCooperatiuService {
    @Autowired
    private ItemGrupCooperatiuRepository itemGrupCooperatiuRepository;

    public ItemGrupCooperatiuDto save(ItemGrupCooperatiuDto itemGrupCooperatiuDto) {
        ModelMapper modelMapper = new ModelMapper();
        ItemGrupCooperatiu itemGrupCooperatiu = modelMapper.map(itemGrupCooperatiuDto,ItemGrupCooperatiu.class);
        ItemGrupCooperatiu itemGrupCooperatiuSaved = itemGrupCooperatiuRepository.save(itemGrupCooperatiu);
        return modelMapper.map(itemGrupCooperatiuSaved,ItemGrupCooperatiuDto.class);
    }

    public ItemGrupCooperatiuDto getItemGrupCooperatiuById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        //Ha de ser findById i no getById perquè getById és Lazy
        ItemGrupCooperatiu itemGrupCooperatiu = itemGrupCooperatiuRepository.findById(id).get();
        //return grupCooperatiuRepository.getById(id);
        return modelMapper.map(itemGrupCooperatiu,ItemGrupCooperatiuDto.class);
    }

    public List<ItemGrupCooperatiuDto> findAllByGrupCooperatiu(GrupCooperatiuDto grupCooperatiuDto){
        ModelMapper modelMapper = new ModelMapper();
        GrupCooperatiu grupCooperatiu = modelMapper.map(grupCooperatiuDto,GrupCooperatiu.class);
        return itemGrupCooperatiuRepository.findAllByGrupCooperatiu(grupCooperatiu).stream().map(itemGrupCooperatiu->modelMapper.map(itemGrupCooperatiu, ItemGrupCooperatiuDto.class)).collect(Collectors.toList());
    }


}

