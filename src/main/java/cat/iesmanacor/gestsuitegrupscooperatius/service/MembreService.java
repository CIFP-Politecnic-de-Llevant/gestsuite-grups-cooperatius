package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.AgrupamentDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.GrupCooperatiuDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.MembreDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Agrupament;
import cat.iesmanacor.gestsuitegrupscooperatius.model.GrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.MembreRepository;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ValorItemMembreRepository;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembreService {
    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private ValorItemMembreRepository valorItemMembreRepository;


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

    public List<MembreDto> findAllByAgrupament(AgrupamentDto agrupamentDto){
        ModelMapper modelMapper = new ModelMapper();
        Agrupament agrupament = modelMapper.map(agrupamentDto,Agrupament.class);
        List<Membre> membres = membreRepository.findAllByAgrupament(agrupament);
        return membres.stream().map(membre -> modelMapper.map(membre,MembreDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public void deleteByGrupCooperatiu(Long idGrupCooperatiuDto){
        List<Membre> membresGrupsCooperatiu = membreRepository.findAllByGrupCooperatiu_IdgrupCooperatiu(idGrupCooperatiuDto);
        membresGrupsCooperatiu.forEach(membre -> {
            valorItemMembreRepository.deleteAllByMembre(membre);
        });

        membreRepository.deleteAllByGrupCooperatiu_IdgrupCooperatiu(idGrupCooperatiuDto);
    }

    @Transactional
    public void deleteByGrupCooperatiu(GrupCooperatiuDto grupCooperatiuDto){
        ModelMapper modelMapper = new ModelMapper();
        GrupCooperatiu grupCooperatiu = modelMapper.map(grupCooperatiuDto,GrupCooperatiu.class);
        List<Membre> membresGrupsCooperatiu = membreRepository.findAllByGrupCooperatiu(grupCooperatiu);
        membresGrupsCooperatiu.forEach(membre -> valorItemMembreRepository.deleteAllByMembre(membre));
        membresGrupsCooperatiu.forEach(membre -> {
            membre.setAmics(null);
            membre.setEnemics(null);
        });
        membreRepository.saveAll(membresGrupsCooperatiu);
        membreRepository.deleteAllByGrupCooperatiu(grupCooperatiu);
    }

}

