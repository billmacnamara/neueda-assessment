package bmacnamara.neueda.assessment.service.api;

import bmacnamara.neueda.assessment.exception.AccountNotFoundException;
import bmacnamara.neueda.assessment.exception.AtmServiceException;

public interface AccountService {

    void verifyAccountPin(int accountNumber, int pin) throws AtmServiceException;
    double getAccountBalance(int accountNumber);
    void executeWithdrawal(int accountNumber, int withdrawalAmount);
    double getMaximumWithdrawalForAccount(int accountNumber) throws AccountNotFoundException;

}
