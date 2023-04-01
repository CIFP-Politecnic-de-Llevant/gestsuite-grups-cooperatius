package cat.iesmanacor.gestsuitegrupscooperatius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_membre")
public @Data class Membre {
    @Id
    @Column(name = "idmembre")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idmembre;

    @Column(name = "nom", nullable = false, length = 255)
    private String nom;

    @Column(name = "agrupament_fixe",  nullable = true)
    private String agrupamentFixe;

    @ManyToOne(optional = true)
    @JsonBackReference
    private Agrupament agrupament;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ValorItemMembre> valorsItemMembre = new HashSet<>();

    @JsonIgnoreProperties({"agrupament","valorsItemMembre","amics","enemics","grupCooperatiu"})
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Membre> amics = new HashSet<>();

    @JsonIgnoreProperties({"agrupament","valorsItemMembre","amics","enemics","grupCooperatiu"})
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Membre> enemics = new HashSet<>();

    @ManyToOne(optional = false)
    @JsonBackReference
    private GrupCooperatiu grupCooperatiu;

}
