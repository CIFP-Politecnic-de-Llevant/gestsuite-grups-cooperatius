package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(exclude={"itemsGrupsCooperatius","agrupaments","membres"})
public @Data class GrupCooperatiuDto {
    private Long idgrupCooperatiu;
    private String nom;
    private Set<ItemGrupCooperatiuDto> itemsGrupsCooperatius = new HashSet<>();
    private Set<MembreDto> membres = new HashSet<>();
    private Set<AgrupamentDto> agrupaments = new HashSet<>();
    private UsuariDto usuari;

}
