package bmacnamara.neueda.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceCheckResponse {

    private int accountNumber;
    private double balance;
    private double maximumWithdrawal;

}
