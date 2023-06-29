package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.GrupCooperatiuDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.MembreDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.MembreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembreService {
    @Autowired
    private MembreRepository membreRepository;


    public MembreDto save(MembreDto membreDto) {
        ModelMapper modelMapper = new ModelMapper();
        Membre membre = modelMapper.map(membreDto,Membre.class);
        Membre membreSaved = membreRepository.save(membre);
        return modelMapper.map(membreSaved,MembreDto.class);
    }

    public MembreDto getMembreById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        //Ha de ser findById i no getById perquè getById és Lazy
        Membre membre = membreRepository.findById(id).get();
        //return itemRepository.getById(id);
        return modelMapper.map(membre,MembreDto.class);
    }

    public List<MembreDto> findAllByGrupCooperatiu(GrupCooperatiuDto grupCooperatiuDto){
        ModelMapper modelMapper = new ModelMapper();
        GrupCooperatiu grupCooperatiu = modelMapper.map(grupCooperatiuDto,GrupCooperatiu.class);
        List<Membre> membres = membreRepository.findAllByGrupCooperatiu(grupCooperatiu);
        return membres.stream().map(membre -> modelMapper.map(membre,MembreDto.class)).collect(Collectors.toList());
    }

    public void deleteByGrupCooperatiu(GrupCooperatiuDto grupCooperatiuDto){
        ModelMapper modelMapper = new ModelMapper();
        GrupCooperatiu grupCooperatiu = modelMapper.map(grupCooperatiuDto,GrupCooperatiu.class);
        membreRepository.deleteAllByGrupCooperatiu(grupCooperatiu);
    }

}

