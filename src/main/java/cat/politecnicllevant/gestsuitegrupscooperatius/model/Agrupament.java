package cat.politecnicllevant.gestsuitegrupscooperatius.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_agrupament")
public @Data class Agrupament {
    @Id
    @Column(name = "idagrupament")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idagrupament;

    @Column(name = "numero", nullable = false, length = 255)
    private String numero;

    @OneToMany(mappedBy = "agrupament", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Membre> membres = new HashSet<>();

    @ManyToOne(optional = true)
    private GrupCooperatiu grupCooperatiu;
}
