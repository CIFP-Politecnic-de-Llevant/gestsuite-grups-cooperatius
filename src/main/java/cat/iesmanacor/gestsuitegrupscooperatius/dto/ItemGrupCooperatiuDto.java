package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import cat.iesmanacor.gestsuitegrupscooperatius.model.ItemGrupCooperatiu;
import lombok.Data;

public @Data class ItemGrupCooperatiuDto implements Comparable<ItemGrupCooperatiuDto> {
    private ItemDto item;
    private GrupCooperatiuDto grupCooperatiu;
    private Integer percentatge;

    @Override
    public int compareTo(ItemGrupCooperatiuDto o) {
        return this.getItem().getIdItem().compareTo(o.getItem().getIdItem());
    }
}
