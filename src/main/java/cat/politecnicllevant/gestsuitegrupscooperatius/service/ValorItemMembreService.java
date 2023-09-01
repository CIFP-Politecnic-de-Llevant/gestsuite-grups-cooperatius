package cat.politecnicllevant.gestsuitegrupscooperatius.service;

import cat.politecnicllevant.gestsuitegrupscooperatius.dto.MembreDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.dto.ValorItemDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.dto.ValorItemMembreDto;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.Membre;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.ValorItem;
import cat.politecnicllevant.gestsuitegrupscooperatius.model.ValorItemMembre;
import cat.politecnicllevant.gestsuitegrupscooperatius.repository.ValorItemMembreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void deleteById(Long id){
        valorItemMembreRepository.deleteById(id);
    }

    @Transactional
    public ValorItemMembreDto save(ValorItemMembreDto valorItemMembreDto){
        ModelMapper modelMapper = new ModelMapper();
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

    public ValorItemMembreDto findByMembreAndValorItem(Long idMembre, Long idValorItem){
        ModelMapper modelMapper = new ModelMapper();
        ValorItemMembre valorItemMembre = valorItemMembreRepository.findByMembre_IdmembreAndValorItem_IdvalorItem(idMembre,idValorItem);
        if(valorItemMembre == null){
            return null;
        }
        return modelMapper.map(valorItemMembre,ValorItemMembreDto.class);
    }

    public List<ValorItemMembreDto> findAllByMembre(MembreDto membreDto){
        ModelMapper modelMapper = new ModelMapper();
        Membre membre = modelMapper.map(membreDto,Membre.class);
        List<ValorItemMembre> valorItemMembres = valorItemMembreRepository.findAllByMembre(membre);
        return valorItemMembres.stream().map(valorItemMembre -> modelMapper.map(valorItemMembre,ValorItemMembreDto.class)).collect(java.util.stream.Collectors.toList());
    }
}

