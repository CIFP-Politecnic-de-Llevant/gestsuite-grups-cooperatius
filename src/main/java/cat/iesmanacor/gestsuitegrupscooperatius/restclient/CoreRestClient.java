package cat.iesmanacor.gestsuitegrupscooperatius.restclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.gestib.UsuariDto;

@FeignClient(name = "core")
public interface CoreRestClient {

    //USUARIS
    @GetMapping("/usuaris/profile")
    ResponseEntity<UsuariDto> getProfile() throws Exception;


}
