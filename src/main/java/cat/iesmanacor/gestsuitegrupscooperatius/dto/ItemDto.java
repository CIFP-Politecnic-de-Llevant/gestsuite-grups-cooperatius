package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import cat.iesmanacor.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

public @Data class ItemDto {
    private Long iditem;
    private String nom;
    private Set<ItemGrupCooperatiuDto> itemsGrupsCooperatius = new HashSet<>();
    private Set<ValorItemDto> valorItems = new HashSet<>();
    private UsuariDto usuari;

}
