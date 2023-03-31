package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import lombok.Data;

public @Data class ValorItemDto {
    private Long idvalorItem;
    private String valor;
    private Integer pes;
    private ItemDto item;
}
