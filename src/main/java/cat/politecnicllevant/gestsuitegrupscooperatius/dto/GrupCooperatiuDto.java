package cat.politecnicllevant.gestsuitegrupscooperatius.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@ToString(exclude={"itemsGrupsCooperatius","agrupaments","membres"})
@EqualsAndHashCode(exclude={"itemsGrupsCooperatius","agrupaments","membres"})
public @Data class GrupCooperatiuDto {
    private Long idgrupCooperatiu;
    private String nom;
    private Set<ItemGrupCooperatiuDto> itemsGrupsCooperatius = new HashSet<>();
    private Set<MembreDto> membres = new HashSet<>();
    private Set<AgrupamentDto> agrupaments = new HashSet<>();
    //private UsuariDto usuari;

}
