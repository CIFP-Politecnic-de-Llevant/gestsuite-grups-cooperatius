package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import lombok.Data;

public @Data class ValorItemMembreDto {
    private Long idvalorItemMembre;
    private ValorItemDto valorItem;
    private MembreDto membre;
}
