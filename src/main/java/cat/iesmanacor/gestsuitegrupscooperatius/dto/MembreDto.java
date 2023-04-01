package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(exclude={"agrupament","valorsItemMembre","amics","enemics","grupCooperatiu"})
public @Data class MembreDto {
    private Long idmembre;
    private String nom;
    private String agrupamentFixe;
    private AgrupamentDto agrupament;
    private Set<ValorItemMembreDto> valorsItemMembre = new HashSet<>();
    private Set<MembreDto> amics = new HashSet<>();
    private Set<MembreDto> enemics = new HashSet<>();
    private GrupCooperatiuDto grupCooperatiu;
}
