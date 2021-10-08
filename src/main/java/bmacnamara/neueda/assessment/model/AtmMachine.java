package bmacnamara.neueda.assessment.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "machine")
public class AtmMachine {

    @Id
    @GeneratedValue
    private int id;

    private int count50s;
    private int count20s;
    private int count10s;
    private int count5s;

    public AtmMachine(int count50s, int count20s, int count10s, int count5s) {
        this.count50s = count50s;
        this.count20s = count20s;
        this.count10s = count10s;
        this.count5s = count5s;
    }

    public int getTotalValue() {
        int value = 0;
        value += count50s * 50;
        value += count20s * 20;
        value += count10s * 10;
        value += count5s * 5;

        return value;
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AtmMachine machine = (AtmMachine) o;
        return Objects.equals(id, machine.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
