package cat.politecnicllevant.gestsuitegrupscooperatius.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(exclude={"valorItem","membre"})
@EqualsAndHashCode(exclude={"valorItem","membre"})
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
