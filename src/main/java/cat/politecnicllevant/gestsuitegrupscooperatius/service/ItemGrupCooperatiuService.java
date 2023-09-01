package cat.politecnicllevant.gestsuitegrupscooperatius.service;

import cat.politecnicllevant.gestsuitegrupscooperatius.dto.GrupCooperatiuDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.dto.ItemGrupCooperatiuDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.Item;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.ItemGrupCooperatiu;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.GrupCooperatiuRepository;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.ItemGrupCooperatiuRepository;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.ItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemGrupCooperatiuService {
    @Autowired
    private ItemGrupCooperatiuRepository itemGrupCooperatiuRepository;

    @Autowired
    private GrupCooperatiuRepository grupCooperatiuRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public ItemGrupCooperatiuDto save(ItemGrupCooperatiuDto itemGrupCooperatiuDto) {
        ModelMapper modelMapper = new ModelMapper();
        /*PropertyMap<ItemGrupCooperatiuDto, ItemGrupCooperatiu> mapperItemGrupCoopeatiu = new PropertyMap<>() {
            protected void configure() {
                map().getGrupCooperatiu().setUsuari(source.getGrupCooperatiu().getUsuari().getIdusuari());
                map().getItem().setUsuari(source.getItem().getUsuari().getIdusuari());
            }
        };
        modelMapper.addMappings(mapperItemGrupCoopeatiu);*/

        //ItemGrupCooperatiu itemGrupCooperatiu = modelMapper.map(itemGrupCooperatiuDto,ItemGrupCooperatiu.class);
        //Mapeig manual perquè al DAO l'usuari és un Long i al DTO és un UsuariDto
        GrupCooperatiu grupCooperatiu = grupCooperatiuRepository.findById(itemGrupCooperatiuDto.getGrupCooperatiu().getIdgrupCooperatiu()).get();
        Item item = itemRepository.findById(itemGrupCooperatiuDto.getItem().getIdItem()).get();

        ItemGrupCooperatiu itemGrupCooperatiu = new ItemGrupCooperatiu();
        itemGrupCooperatiu.setGrupCooperatiu(grupCooperatiu);
        itemGrupCooperatiu.setItem(item);
        itemGrupCooperatiu.setPercentatge(itemGrupCooperatiuDto.getPercentatge());

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

