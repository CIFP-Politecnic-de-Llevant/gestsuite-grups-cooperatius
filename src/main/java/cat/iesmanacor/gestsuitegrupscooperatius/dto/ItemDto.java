package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(exclude={"usuari","valorItems","itemsGrupsCooperatius"})
public @Data class ItemDto {
    private Long idItem;
    private String nom;
    private Set<ItemGrupCooperatiuDto> itemsGrupsCooperatius = new HashSet<>();
    private Set<ValorItemDto> valorItems = new HashSet<>();

    //Microservei CORE
    private UsuariDto usuari;

}
