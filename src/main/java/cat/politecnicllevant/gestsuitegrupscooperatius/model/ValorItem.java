package cat.politecnicllevant.gestsuitegrupscooperatius.model;

import jakarta.persistence.*;
import lombok.Data;

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
    private Item item;
}
