package bmacnamara.neueda.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponse {

    private int accountNumber;
    private Map<Note, Integer> notes;
    private double remainingBalance;
    private String message;

}
