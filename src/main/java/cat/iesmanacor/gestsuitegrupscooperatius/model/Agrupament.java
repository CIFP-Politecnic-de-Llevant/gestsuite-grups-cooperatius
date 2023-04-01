package cat.iesmanacor.gestsuitegrupscooperatius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @JsonManagedReference
    private Set<Membre> membres = new HashSet<>();

    @ManyToOne(optional = true)
    @JsonBackReference
    private GrupCooperatiu grupCooperatiu;
}
