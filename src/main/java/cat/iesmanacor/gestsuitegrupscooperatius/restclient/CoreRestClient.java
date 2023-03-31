package cat.iesmanacor.gestsuitegrupscooperatius.restclient;

import cat.iesmanacor.webiesmanacor.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "core")
public interface CoreRestClient {

    //USUARIS
    @GetMapping("/usuaris/profile/{id}")
    ResponseEntity<CoreUsuariDto> getProfile(@PathVariable("id") String idUsuari) throws Exception;

    @GetMapping("/public/usuaris/profile/{id}")
    ResponseEntity<CoreUsuariDto> getPublicProfile(@PathVariable("id") String idUsuari) throws Exception;

    @GetMapping("/usuaris/llistat/actius")
    ResponseEntity<List<CoreUsuariDto>> getUsuarisActius();

    @GetMapping("/usuaris/findByDepartament/{id}")
    ResponseEntity<List<CoreUsuariDto>> getUsuarisByDepartament(@PathVariable("id") Long idDepartament);

    //Departaments
    @GetMapping("/departament/getById/{id}")
    ResponseEntity<DepartamentDto> getDepartamentById(@PathVariable("id") Long identificador);

    @GetMapping("/departament/getByCodiGestib/{id}")
    ResponseEntity<DepartamentDto> getDepartamentByCodiGestib(@PathVariable("id") String identificador);


    //Sessió
    @GetMapping("/sessio/pares")
    ResponseEntity<List<SessioDto>> getSessionsAtencioPares();

    //Grup
    @GetMapping("/grup/getGrupsByTutor/{idusuari}")
    ResponseEntity<List<GrupDto>> getGrupsByTutor(@PathVariable("idusuari") Long idusuari);

    //Curs
    @GetMapping("/curs/getByCodiGestib/{id}")
    ResponseEntity<CursDto> getCursByCodiGestib(@PathVariable("id") String identificador);
}
