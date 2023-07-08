package cat.iesmanacor.gestsuitegrupscooperatius.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@ToString(exclude={"amics","enemics"})
@EqualsAndHashCode(exclude={"amics","enemics"})
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
    @JoinColumn(name = "agrupament_id")
    private Agrupament agrupament;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ValorItemMembre> valorsItemMembre = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Membre> amics = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Membre> enemics = new HashSet<>();

    @ManyToOne(optional = false)
    private GrupCooperatiu grupCooperatiu;

}
