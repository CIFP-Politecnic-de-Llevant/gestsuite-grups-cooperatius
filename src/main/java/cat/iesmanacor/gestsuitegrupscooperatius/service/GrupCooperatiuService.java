package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.AgrupamentDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.GrupCooperatiuDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ItemDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Item;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.GrupCooperatiuRepository;
import cat.iesmanacor.gestsuitegrupscooperatius.restclient.CoreRestClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrupCooperatiuService {
    @Autowired
    private GrupCooperatiuRepository grupCooperatiuRepository;

    @Autowired
    private CoreRestClient coreRestClient;

    public GrupCooperatiuDto save(GrupCooperatiuDto grupCooperatiuDto) {
        ModelMapper modelMapper = new ModelMapper();
        PropertyMap<GrupCooperatiuDto, GrupCooperatiu> mapperGrupCooperatiu = new PropertyMap<>() {
            protected void configure() {
                map().setUsuari(source.getUsuari().getIdusuari());
            }
        };
        modelMapper.addMappings(mapperGrupCooperatiu);

        GrupCooperatiu grupCooperatiu = modelMapper.map(grupCooperatiuDto,GrupCooperatiu.class);
        GrupCooperatiu grupCooperatiuSaved = grupCooperatiuRepository.save(grupCooperatiu);
        return modelMapper.map(grupCooperatiuSaved,GrupCooperatiuDto.class);
    }

    public GrupCooperatiuDto getGrupCooperatiuById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        //Ha de ser findById i no getById perquè getById és Lazy
        GrupCooperatiu grupCooperatiu = grupCooperatiuRepository.findById(id).get();
        //return grupCooperatiuRepository.getById(id);
        return modelMapper.map(grupCooperatiu,GrupCooperatiuDto.class);
    }

    public List<GrupCooperatiuDto> findAllByUsuari(UsuariDto usuariDto) {
        ModelMapper modelMapper = new ModelMapper();
        return grupCooperatiuRepository.findAllByUsuari(usuariDto.getIdusuari()).stream().map(grupCooperatiu->modelMapper.map(grupCooperatiu, GrupCooperatiuDto.class)).collect(Collectors.toList());
    }
}

