package cat.politecnicllevant.gestsuitegrupscooperatius.service;

import cat.politecnicllevant.gestsuitegrupscooperatius.dto.AgrupamentDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.dto.GrupCooperatiuDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.Agrupament;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.AgrupamentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AgrupamentService {
    @Autowired
    private AgrupamentRepository agrupamentRepository;


    public List<AgrupamentDto> findAllByGrupCooperatiu(GrupCooperatiuDto grupCooperatiuDto){
        ModelMapper modelMapper = new ModelMapper();
        GrupCooperatiu grupCooperatiu = modelMapper.map(grupCooperatiuDto, GrupCooperatiu.class);
        return agrupamentRepository.findAllByGrupCooperatiu(grupCooperatiu).stream().map(agrupament->modelMapper.map(agrupament,AgrupamentDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public AgrupamentDto save(AgrupamentDto agrupamentDto) {
        ModelMapper modelMapper = new ModelMapper();
        Agrupament agrupament = modelMapper.map(agrupamentDto,Agrupament.class);
        Agrupament agrupamentSaved = agrupamentRepository.save(agrupament);
        return modelMapper.map(agrupamentSaved,AgrupamentDto.class);
    }

    public AgrupamentDto getAgrupamentById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        //Ha de ser findById i no getById perquè getById és Lazy
        Agrupament agrupament = agrupamentRepository.findById(id).get();
        return modelMapper.map(agrupament,AgrupamentDto.class);
    }

    @Transactional
    public void deleteByGrupCooperatiu(Long idGrupCooperatiuDto){
        agrupamentRepository.deleteAllByGrupCooperatiu_IdgrupCooperatiu(idGrupCooperatiuDto);
    }

    @Transactional
    public void deleteByGrupCooperatiu(GrupCooperatiuDto grupCooperatiuDto){
        ModelMapper modelMapper = new ModelMapper();
        GrupCooperatiu grupCooperatiu = modelMapper.map(grupCooperatiuDto, GrupCooperatiu.class);
        agrupamentRepository.deleteAllByGrupCooperatiu(grupCooperatiu);
    }
}

