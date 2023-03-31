package cat.iesmanacor.gestsuitegrupscooperatius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_grups_cooperatius_item")
@EqualsAndHashCode(exclude={"usuari","valorItems","itemsGrupsCooperatius"})
public @Data class Item {
    @Id
    @Column(name = "iditem")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iditem;

    @Column(name = "nom", nullable = false, length = 255)
    private String nom;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<ItemGrupCooperatiu> itemsGrupsCooperatius = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<ValorItem> valorItems = new HashSet<>();


    @Column(name = "usuari_idusuari", nullable = false)
    private Long usuari;

}
