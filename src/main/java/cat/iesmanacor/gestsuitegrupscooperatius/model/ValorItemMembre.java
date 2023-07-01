package cat.iesmanacor.gestsuitegrupscooperatius.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "im_valor_item_membre")
//Alerta amb posar Hashcode de Lombok perquè si posem valorItem i Member el HashSet detecta que són distints si no té id!
public @Data class ValorItemMembre {
    @Id
    @Column(name = "idvalor_item_membre")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvalorItemMembre;

    @ManyToOne(optional = true)
    private ValorItem valorItem;

    @ManyToOne(optional = true)
    private Membre membre;
}
