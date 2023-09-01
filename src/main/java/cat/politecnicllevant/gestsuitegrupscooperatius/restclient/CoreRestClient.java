package cat.politecnicllevant.gestsuitegrupscooperatius.restclient;

import cat.politecnicllevant.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "core")
public interface CoreRestClient {

    //USUARIS
    @GetMapping("/usuaris/profile")
    ResponseEntity<UsuariDto> getProfile() throws Exception;


}
