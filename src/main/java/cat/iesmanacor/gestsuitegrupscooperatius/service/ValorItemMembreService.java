package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.ValorItemMembreDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItemMembre;
import cat.iesmanacor.gestsuitegrupscooperatius.repository.ValorItemMembreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

