package cat.iesmanacor.gestsuitegrupscooperatius.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "im_item_grup_cooperatiu")
//Alerta, si fem un new HashSet i posem que exclogui item i grup cooperatiu, a un grup cooperatiu amb 3 ítems només n'agafarà un!
//@EqualsAndHashCode(exclude={"item","grupCooperatiu"})
@IdClass(ItemGrupCooperatiuId.class)
public @Data class ItemGrupCooperatiu {

    @Id
    @ManyToOne
    @JoinColumn(name = "item", insertable = false, updatable = false)
    private Item item;

    @Id
    @ManyToOne
    @JoinColumn(name = "grup_cooperatiu", insertable = false, updatable = false)
    private GrupCooperatiu grupCooperatiu;


    @Column(name = "percentatge", nullable = false)
    private Integer percentatge;
}
