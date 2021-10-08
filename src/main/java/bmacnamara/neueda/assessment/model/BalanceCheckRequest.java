package bmacnamara.neueda.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceCheckRequest {

    private int accountNumber;
    private int pin;

}
