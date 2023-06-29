package cat.iesmanacor.gestsuitegrupscooperatius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_valor_item_membre")
//Alerta amb posar Hashcode de Lombok perquè si posem valorItem i Member el HashSet detecta que són distints si no té id!
public @Data class ValorItemMembre {
    @Id
    @Column(name = "idvalor_item_membre")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvalorItemMembre;

    @ManyToOne(optional = true)
    @JsonManagedReference
    private ValorItem valorItem;

    @ManyToOne(optional = true)
    @JsonBackReference
    private Membre membre;
}
