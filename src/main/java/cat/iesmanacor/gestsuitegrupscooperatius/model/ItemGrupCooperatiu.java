package cat.iesmanacor.gestsuitegrupscooperatius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_item_grup_cooperatiu")
//Alerta, si fem un new HashSet i posem que exclogui item i grup cooperatiu, a un grup cooperatiu amb 3 ítems només n'agafarà un!
//@EqualsAndHashCode(exclude={"item","grupCooperatiu"})
@IdClass(ItemGrupCooperatiuId.class)
public @Data class ItemGrupCooperatiu implements Comparable<ItemGrupCooperatiu> {

    @Id
    @ManyToOne
    @JoinColumn(name = "item", insertable = false, updatable = false)
    @JsonManagedReference
    private Item item;

    @Id
    @ManyToOne
    @JoinColumn(name = "grup_cooperatiu", insertable = false, updatable = false)
    @JsonBackReference
    private GrupCooperatiu grupCooperatiu;


    @Column(name = "percentatge", nullable = false)
    private Integer percentatge;

    @Override
    public int compareTo(ItemGrupCooperatiu o) {
        return this.getItem().getIdItem().compareTo(o.getItem().getIdItem());
    }
}
