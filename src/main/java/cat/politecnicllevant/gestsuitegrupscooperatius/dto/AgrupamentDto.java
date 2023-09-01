package cat.politecnicllevant.gestsuitegrupscooperatius.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@ToString(exclude={"grupCooperatiu","membres"})
@EqualsAndHashCode(exclude={"grupCooperatiu","membres"})
public @Data class AgrupamentDto {
    private Long idagrupament;
    private String numero;
    private Set<MembreDto> membres = new HashSet<>();
    private GrupCooperatiuDto grupCooperatiu;
}
