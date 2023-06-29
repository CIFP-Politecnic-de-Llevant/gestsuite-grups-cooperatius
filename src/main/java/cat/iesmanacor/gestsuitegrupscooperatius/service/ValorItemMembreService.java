package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.MembreDto;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.ValorItemMembreDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItem;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItemMembre;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ValorItemMembreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValorItemMembreService {

    @Autowired
    private ValorItemMembreRepository valorItemMembreRepository;

    public ValorItemMembreDto findById(Long id){
        ModelMapper modelMapper = new ModelMapper();
        ValorItemMembre valorItemMembre = valorItemMembreRepository.findById(id).get();
        return modelMapper.map(valorItemMembre,ValorItemMembreDto.class);
    }

    public ValorItemMembreDto save(ValorItemMembreDto valorItemMembreDto){
        ModelMapper modelMapper = new ModelMapper();
        ValorItemMembre valorItemMembre = modelMapper.map(valorItemMembreDto,ValorItemMembre.class);
        ValorItemMembre valorItemMembreSaved = valorItemMembreRepository.save(valorItemMembre);
        return modelMapper.map(valorItemMembreSaved,ValorItemMembreDto.class);
    }

    public List<ValorItemMembreDto> findAllByMembre(MembreDto membre){
        ModelMapper modelMapper = new ModelMapper();
        Membre membre1 = modelMapper.map(membre,Membre.class);
        List<ValorItemMembre> valorItemMembres = valorItemMembreRepository.findAllByMembre(membre1);
        return valorItemMembres.stream().map(valorItemMembre -> modelMapper.map(valorItemMembre,ValorItemMembreDto.class)).collect(java.util.stream.Collectors.toList());
    }
}

