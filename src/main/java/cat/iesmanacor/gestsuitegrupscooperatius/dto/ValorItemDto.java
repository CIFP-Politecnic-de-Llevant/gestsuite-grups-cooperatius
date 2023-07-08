package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(exclude="item")
@EqualsAndHashCode(exclude="item")
public @Data class ValorItemDto {
    private Long idvalorItem;
    private String valor;
    private Integer pes;
    private ItemDto item;
}
