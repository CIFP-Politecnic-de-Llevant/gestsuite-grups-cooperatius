package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import cat.iesmanacor.gestsuitegrupscooperatius.model.ItemGrupCooperatiu;
import lombok.Data;

public @Data class ItemGrupCooperatiuDto implements Comparable<ItemGrupCooperatiu> {
    private ItemDto item;
    private GrupCooperatiuDto grupCooperatiu;
    private Integer percentatge;

    @Override
    public int compareTo(ItemGrupCooperatiu o) {
        return this.getItem().getIdItem().compareTo(o.getItem().getIdItem());
    }
}
