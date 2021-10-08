package bmacnamara.neueda.assessment.service;

import bmacnamara.neueda.assessment.TestUtils;
import bmacnamara.neueda.assessment.dao.AccountRepository;
import bmacnamara.neueda.assessment.exception.AccountNotFoundException;
import bmacnamara.neueda.assessment.exception.IncorrectPinException;
import bmacnamara.neueda.assessment.model.Account;
import bmacnamara.neueda.assessment.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static bmacnamara.neueda.assessment.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountServiceImpl accountService;

    Account mockAccount = TestUtils.getMockAccount();

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(accountRepository.findAccountByAccountNumber(MOCK_ACCT_NUMBER)).thenReturn(mockAccount);
    }

    @Test
    public void testVerifyAccountPin() {
        this.accountService.verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);
    }

    @Test
    public void testVerifyAccountPin_incorrectPin() {
        assertThrows(IncorrectPinException.class, () -> {
            this.accountService.verifyAccountPin(MOCK_ACCT_NUMBER, 4321);
        });
    }

    @Test
    public void testVerifyAccountPin_accountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> {
            this.accountService.verifyAccountPin(45467897, MOCK_ACCT_PIN);
        });
    }

    @Test
    public void testGetAccountBalance() {
        double balance = this.accountService.getAccountBalance(MOCK_ACCT_NUMBER);
        assertEquals(MOCK_ACCT_BALANCE, balance);
    }

    @Test
    public void testGetAccountBalance_accountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> {
            this.accountService.getAccountBalance(45467897);
        });
    }

    @Test
    public void testGetMaximumWithdrawalForAccount() {
        double maximumWithdrawal = this.accountService.getMaximumWithdrawalForAccount(MOCK_ACCT_NUMBER);
        assertEquals(MOCK_ACCT_BALANCE + MOCK_ACCT_OVERDRAFT, maximumWithdrawal);
    }

    @Test
    public void testGetMaximumWithdrawalForAccount_accountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> {
            this.accountService.getMaximumWithdrawalForAccount(45467897);
        });
    }

    @Test
    public void testExecuteWithdrawal() {
        this.accountService.executeWithdrawal(MOCK_ACCT_NUMBER, 500);
        Mockito.verify(accountRepository, Mockito.times(1))
                .setNewBalance(MOCK_ACCT_NUMBER, (MOCK_ACCT_BALANCE - 500));
    }

    @Test
    public void testExecuteWithdrawal_accountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> {
            this.accountService.executeWithdrawal(45467897, 500);
        });
        Mockito.verify(accountRepository, Mockito.times(0))
                .setNewBalance(45467897, (MOCK_ACCT_BALANCE - 500));
    }

}
