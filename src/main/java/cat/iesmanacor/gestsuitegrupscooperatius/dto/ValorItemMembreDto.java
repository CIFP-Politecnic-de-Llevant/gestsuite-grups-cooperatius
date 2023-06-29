package cat.iesmanacor.gestsuitegrupscooperatius.dto;

import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItemMembre;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude={"valorItem"})
public @Data class ValorItemMembreDto implements Comparable<ValorItemMembreDto> {
    private Long idvalorItemMembre;
    private ValorItemDto valorItem;
    private MembreDto membre;

    @Override
    public int compareTo(ValorItemMembreDto o) {
        if(this.getValorItem()==null || this.getValorItem().getItem()==null){
            return -1;
        }
        if(o.getValorItem()==null || o.getValorItem().getItem()==null){
            return 1;
        }
        return this.getValorItem().getItem().getIdItem().compareTo(o.getValorItem().getItem().getIdItem());
    }
}
