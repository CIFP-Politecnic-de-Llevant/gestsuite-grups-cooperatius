package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import lombok.Data;

public @Data class ItemGrupCooperatiuDto {
    private ItemDto item;
    private GrupCooperatiuDto grupCooperatiu;
    private Integer percentatge;
}
