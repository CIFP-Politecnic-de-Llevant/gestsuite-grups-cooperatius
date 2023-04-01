package cat.iesmanacor.gestsuitegrupscooperatius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_valor_item")
public @Data class ValorItem {
    @Id
    @Column(name = "idvalor_item")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvalorItem;

    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Column(name = "pes", nullable = false, length = 255)
    private Integer pes;

    @ManyToOne(optional = true)
    @JsonManagedReference
    private Item item;
}
