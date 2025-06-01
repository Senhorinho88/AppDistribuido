package org.vitor.appdistribuido.Alunos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "alunos")
public class Alunos {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alunos_seq")
    @SequenceGenerator(name = "alunos_seq",
            sequenceName = "alunos_sequence",
            allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Número é obrigatório")
    private Integer number;

    public Alunos(String name, Integer number) {
        this.name   = name;
        this.number = number;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Object unwrapped = (o instanceof HibernateProxy proxy) ? proxy.getHibernateLazyInitializer().getImplementation() : o;
        if (!(unwrapped instanceof Alunos other)) return false;
        return id != null && Objects.equals(id, other.id);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
