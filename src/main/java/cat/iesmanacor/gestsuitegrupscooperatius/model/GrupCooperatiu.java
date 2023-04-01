package cat.iesmanacor.gestsuitegrupscooperatius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_grup_cooperatiu")
public @Data class GrupCooperatiu {
    @Id
    @Column(name = "idgrup_cooperatiu")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idgrupCooperatiu;

    @Column(name = "nom", nullable = false, length = 2048)
    private String nom;

    @OneToMany(mappedBy = "grupCooperatiu", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ItemGrupCooperatiu> itemsGrupsCooperatius = new HashSet<>();

    @OneToMany(mappedBy = "grupCooperatiu", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Membre> membres = new HashSet<>();

    @OneToMany(mappedBy = "grupCooperatiu", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Agrupament> agrupaments = new HashSet<>();

    @Column(name = "usuari_idusuari", nullable = false)
    private Long usuari;

}
