package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@ToString(exclude={"agrupament","valorsItemMembre","amics","enemics","grupCooperatiu"})
@EqualsAndHashCode(exclude={"agrupament","valorsItemMembre","amics","enemics","grupCooperatiu"})
public @Data class MembreDto implements Comparable<MembreDto>{
    private Long idmembre;
    private String nom;
    private String agrupamentFixe;
    private AgrupamentDto agrupament;
    private Set<ValorItemMembreDto> valorsItemMembre = new HashSet<>();
    private Set<MembreDto> amics = new HashSet<>();
    private Set<MembreDto> enemics = new HashSet<>();
    private GrupCooperatiuDto grupCooperatiu;

    @Override
    public int compareTo(MembreDto o) {
        return this.getNom().compareTo(o.getNom());
    }
}
