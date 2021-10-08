package bmacnamara.neueda.assessment.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    private int accountNumber;
    private int pin;

    private double balance;
    private double overdraft;

}
