package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.MembreDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ValorItemDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ValorItemMembreDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItem;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItemMembre;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ValorItemMembreRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ValorItemMembreService {

    @Autowired
    private ValorItemMembreRepository valorItemMembreRepository;

    public ValorItemMembreDto findById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        ValorItemMembre valorItemMembre = valorItemMembreRepository.findById(id).get();
        return modelMapper.map(valorItemMembre,ValorItemMembreDto.class);
    }

    public void deleteById(Long id){
        valorItemMembreRepository.deleteById(id);
    }

    public ValorItemMembreDto save(ValorItemMembreDto valorItemMembreDto){
        ModelMapper modelMapper = new ModelMapper();
        PropertyMap<ValorItemMembreDto, ValorItemMembre> mapper = new PropertyMap<>() {
            protected void configure() {
                map().getMembre().setAmics(new HashSet<>());
                map().getMembre().setEnemics(new HashSet<>());
            }
        };
        modelMapper.addMappings(mapper);
        ValorItemMembre valorItemMembre = modelMapper.map(valorItemMembreDto,ValorItemMembre.class);
        ValorItemMembre valorItemMembreSaved = valorItemMembreRepository.save(valorItemMembre);
        return modelMapper.map(valorItemMembreSaved,ValorItemMembreDto.class);
    }

    public Optional<ValorItemMembreDto> findByMembreAndValorItem(MembreDto membreDto, ValorItemDto valorItemDto){
        ModelMapper modelMapper = new ModelMapper();
        Membre membre = modelMapper.map(membreDto,Membre.class);
        ValorItem valorItem = modelMapper.map(valorItemDto,ValorItem.class);
        ValorItemMembre valorItemMembre = valorItemMembreRepository.findByMembreAndValorItem(membre,valorItem);
        if(valorItemMembre == null){
            return Optional.empty();
        }
        ValorItemMembreDto valorItemMembreDto = modelMapper.map(valorItemMembre,ValorItemMembreDto.class);
        return Optional.ofNullable(valorItemMembreDto);
    }

    public List<ValorItemMembreDto> findAllByMembre(MembreDto membreDto){
        ModelMapper modelMapper = new ModelMapper();
        Membre membre = modelMapper.map(membreDto,Membre.class);
        List<ValorItemMembre> valorItemMembres = valorItemMembreRepository.findAllByMembre(membre);
        return valorItemMembres.stream().map(valorItemMembre -> modelMapper.map(valorItemMembre,ValorItemMembreDto.class)).collect(java.util.stream.Collectors.toList());
    }
}

