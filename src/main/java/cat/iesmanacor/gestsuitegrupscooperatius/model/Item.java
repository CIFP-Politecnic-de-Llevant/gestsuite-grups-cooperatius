package cat.iesmanacor.gestsuitegrupscooperatius.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_item")
public @Data class Item {
    @Id
    @Column(name = "iditem")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    @Column(name = "nom", nullable = false, length = 255)
    private String nom;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemGrupCooperatiu> itemsGrupsCooperatius = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ValorItem> valorItems = new HashSet<>();


    //@Column(name = "usuari_idusuari", nullable = true)
    //private Long usuari;

}
