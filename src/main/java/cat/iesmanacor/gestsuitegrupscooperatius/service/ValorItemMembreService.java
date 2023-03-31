package cat.iesmanacor.gestsuitegrupscooperatius.service;

import cat.iesmanacor.gestsuite.grupscooperatius.model.ValorItemMembre;
import cat.iesmanacor.gestsuite.grupscooperatius.repository.ValorItemMembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValorItemMembreService {

    @Autowired
    private ValorItemMembreRepository valorItemMembreRepository;

    public ValorItemMembre findById(Long id){
        return valorItemMembreRepository.findById(id).get();
    }

    public ValorItemMembre save(ValorItemMembre valorItemMembre){
        return valorItemMembreRepository.save(valorItemMembre);
    }
}

