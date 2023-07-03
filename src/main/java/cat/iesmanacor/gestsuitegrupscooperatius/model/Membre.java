package cat.iesmanacor.gestsuitegrupscooperatius.model;

import jakarta.persistence.*;
import lombok.Data;

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
    @JoinColumn(name = "agrupament_id")
    private Agrupament agrupament;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ValorItemMembre> valorsItemMembre = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "im_membre_amics",
            joinColumns = @JoinColumn(name = "membre_id"),
            inverseJoinColumns = @JoinColumn(name = "amic_id"))
    private Set<Membre> amics = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "im_membre_enemics",
            joinColumns = @JoinColumn(name = "membre_id"),
            inverseJoinColumns = @JoinColumn(name = "enemic_id"))
    private Set<Membre> enemics = new HashSet<>();

    @ManyToOne(optional = false)
    private GrupCooperatiu grupCooperatiu;

}
