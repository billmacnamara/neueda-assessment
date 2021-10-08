package bmacnamara.neueda.assessment.service.impl;

import bmacnamara.neueda.assessment.dao.AccountRepository;
import bmacnamara.neueda.assessment.exception.AccountNotFoundException;
import bmacnamara.neueda.assessment.exception.AtmServiceException;
import bmacnamara.neueda.assessment.exception.IncorrectPinException;
import bmacnamara.neueda.assessment.model.Account;
import bmacnamara.neueda.assessment.service.api.AccountService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
public class AccountServiceImpl implements AccountService {

    private AccountRepository repository;

    @Autowired
    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    private final static String INCORRECT_PIN_ERROR_MSG = "Incorrect PIN code provided";
    private final static String PIN_VERIFIED_MSG = "PIN code verified for account %s";
    private final static String INVALID_ACCT_NUMBER_MSG = "No account with account number %s exists";

    /**
     * Verify that the PIN code provided matches the stored code for a user's account
     * @param accountNumber
     * @param pin
     * @throws AtmServiceException if codes do not match or if account does not exist
     */
    @Override
    public void verifyAccountPin(int accountNumber, int pin) throws AtmServiceException {
        log.info("Verifying PIN code for request");

        Account account = this.repository.findAccountByAccountNumber(accountNumber);
        if (account == null) throw new AccountNotFoundException();

        if (account.getPin() != pin) {
            throw new IncorrectPinException(INCORRECT_PIN_ERROR_MSG);
        }
    }

    @Override
    public double getAccountBalance(int accountNumber) throws AccountNotFoundException {
        Account account = this.repository.findAccountByAccountNumber(accountNumber);
        if (account == null) throw new AccountNotFoundException();

        return account.getBalance();
    }

    @Override
    public double getMaximumWithdrawalForAccount(int accountNumber) throws AccountNotFoundException {
        Account account = this.repository.findAccountByAccountNumber(accountNumber);
        if (account == null) throw new AccountNotFoundException();

        return account.getBalance() + account.getOverdraft();
    }

    @Override
    public void executeWithdrawal(int accountNumber, int withdrawalAmount) {
        Account account = this.repository.findAccountByAccountNumber(accountNumber);
        if (account == null) throw new AccountNotFoundException();

        double newBalance = account.getBalance() - withdrawalAmount;
        this.repository.setNewBalance(accountNumber, newBalance);
    }

}
