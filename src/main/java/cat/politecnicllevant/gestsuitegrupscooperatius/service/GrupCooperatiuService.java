package cat.politecnicllevant.gestsuitegrupscooperatius.service;

import cat.politecnicllevant.gestsuitegrupscooperatius.dto.GrupCooperatiuDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.GrupCooperatiuRepository;
import cat.politecnicllevant.gestsuitegrupscooperatius.restclient.CoreRestClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrupCooperatiuService {
    @Autowired
    private GrupCooperatiuRepository grupCooperatiuRepository;

    @Autowired
    private CoreRestClient coreRestClient;

    @Transactional
    public GrupCooperatiuDto save(GrupCooperatiuDto grupCooperatiuDto) {
        ModelMapper modelMapper = new ModelMapper();
        /*PropertyMap<GrupCooperatiuDto, GrupCooperatiu> mapperGrupCooperatiu = new PropertyMap<>() {
            protected void configure() {
                map().setUsuari(source.getUsuari().getIdusuari());
            }
        };*/
        //modelMapper.addMappings(mapperGrupCooperatiu);

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

    public List<GrupCooperatiuDto> findAll() {
        ModelMapper modelMapper = new ModelMapper();
        return grupCooperatiuRepository.findAll().stream().map(grupCooperatiu->modelMapper.map(grupCooperatiu, GrupCooperatiuDto.class)).collect(Collectors.toList());
    }

    /*public List<GrupCooperatiuDto> findAllByUsuari(UsuariDto usuariDto) {
        ModelMapper modelMapper = new ModelMapper();
        return grupCooperatiuRepository.findAllByUsuari(usuariDto.getIdusuari()).stream().map(grupCooperatiu->modelMapper.map(grupCooperatiu, GrupCooperatiuDto.class)).collect(Collectors.toList());
    }*/
}

