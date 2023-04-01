package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude={"valorItem","membre"})
public @Data class ValorItemMembreDto {
    private Long idvalorItemMembre;
    private ValorItemDto valorItem;
    private MembreDto membre;
}
