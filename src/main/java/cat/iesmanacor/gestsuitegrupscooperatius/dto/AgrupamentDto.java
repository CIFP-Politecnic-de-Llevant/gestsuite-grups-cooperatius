package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

public @Data class AgrupamentDto {
    private Long idagrupament;
    private String numero;
    private Set<MembreDto> membres = new HashSet<>();
    private GrupCooperatiuDto grupCooperatiu;
}
