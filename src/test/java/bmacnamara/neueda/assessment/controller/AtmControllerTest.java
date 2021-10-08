package bmacnamara.neueda.assessment.controller;

import bmacnamara.neueda.assessment.exception.AccountNotFoundException;
import bmacnamara.neueda.assessment.exception.AtmEmptyException;
import bmacnamara.neueda.assessment.exception.IncorrectPinException;
import bmacnamara.neueda.assessment.exception.InsufficientFundsException;
import bmacnamara.neueda.assessment.model.*;
import bmacnamara.neueda.assessment.service.impl.AccountServiceImpl;
import bmacnamara.neueda.assessment.service.impl.AtmMachineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static bmacnamara.neueda.assessment.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class AtmControllerTest {

    @Mock
    AccountServiceImpl accountService;

    @Mock
    AtmMachineServiceImpl atmMachineService;

    @InjectMocks
    AtmController atmController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetBalanceForAccount() {
        BalanceCheckRequest request = new BalanceCheckRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);

        doNothing().when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);
        Mockito.when(accountService.getAccountBalance(MOCK_ACCT_NUMBER)).thenReturn(MOCK_ACCT_BALANCE);
        Mockito.when(accountService.getMaximumWithdrawalForAccount(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE + MOCK_ACCT_OVERDRAFT);

        ResponseEntity<BalanceCheckResponse> response = this.atmController.getBalanceForAccount(request);

        assertEquals(MOCK_ACCT_BALANCE, response.getBody().getBalance());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetBalanceForAccount_incorrectPin() {
        BalanceCheckRequest request = new BalanceCheckRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);

        doThrow(IncorrectPinException.class).when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);

        assertThrows(IncorrectPinException.class, () -> {
            this.atmController.getBalanceForAccount(request);
        });
    }

    @Test
    public void testGetBalanceForAccount_accountNotFound() {
        BalanceCheckRequest request = new BalanceCheckRequest();
        request.setAccountNumber(1231231312);
        request.setPin(MOCK_ACCT_PIN);

        doThrow(AccountNotFoundException.class).when(accountService).verifyAccountPin(1231231312, MOCK_ACCT_PIN);

        assertThrows(AccountNotFoundException.class, () -> {
            this.atmController.getBalanceForAccount(request);
        });
    }

    @Test
    public void testWithdrawFromAccount() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);
        request.setAtmId(1);

        int withdrawalAmount = 500;
        request.setWithdrawalAmount(withdrawalAmount);

        doNothing().when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);

        Mockito.when(accountService.getMaximumWithdrawalForAccount(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE + MOCK_ACCT_OVERDRAFT);
        Mockito.when(accountService.getAccountBalance(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE);

        Map<Note, Integer> notes = new HashMap<>();
        notes.put(Note.FIFTY, 10);
        notes.put(Note.TWENTY, 0);
        notes.put(Note.TEN, 0);
        notes.put(Note.FIVE, 0);
        Mockito.when(atmMachineService.getNoteDistributionForWithdrawal(withdrawalAmount))
                .thenReturn(notes);
        Mockito.when(atmMachineService.getValueOfNotes(notes))
                .thenReturn(withdrawalAmount);
        Mockito.when(atmMachineService.getMaximumWithdrawal(withdrawalAmount))
                .thenReturn(1500);

        ResponseEntity<WithdrawalResponse> response = this.atmController.withdrawFromAccount(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ACCT_NUMBER, response.getBody().getAccountNumber());
        assertEquals(10, response.getBody().getNotes().get(Note.FIFTY));
        assertEquals(0, response.getBody().getNotes().get(Note.TWENTY));
        assertEquals(0, response.getBody().getNotes().get(Note.TEN));
        assertEquals(0, response.getBody().getNotes().get(Note.FIVE));
        assertEquals(MOCK_ACCT_BALANCE - withdrawalAmount, response.getBody().getRemainingBalance());
    }

    @Test
    public void testWithdrawFromAccount_overdraft() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);
        request.setAtmId(1);

        int withdrawalAmount = 900;
        request.setWithdrawalAmount(withdrawalAmount);

        doNothing().when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);

        Mockito.when(accountService.getMaximumWithdrawalForAccount(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE + MOCK_ACCT_OVERDRAFT);
        Mockito.when(accountService.getAccountBalance(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE);
        Mockito.when(atmMachineService.getMaximumWithdrawal(withdrawalAmount))
                .thenReturn(1500);

        Map<Note, Integer> notes = new HashMap<>();
        notes.put(Note.FIFTY, 10);
        notes.put(Note.TWENTY, 0);
        notes.put(Note.TEN, 0);
        notes.put(Note.FIVE, 0);
        Mockito.when(atmMachineService.getNoteDistributionForWithdrawal(withdrawalAmount))
                .thenReturn(notes);
        Mockito.when(atmMachineService.getValueOfNotes(notes))
                .thenReturn(withdrawalAmount);

        ResponseEntity<WithdrawalResponse> response = this.atmController.withdrawFromAccount(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ACCT_NUMBER, response.getBody().getAccountNumber());
        assertEquals(10, response.getBody().getNotes().get(Note.FIFTY));
        assertEquals(0, response.getBody().getNotes().get(Note.TWENTY));
        assertEquals(0, response.getBody().getNotes().get(Note.TEN));
        assertEquals(0, response.getBody().getNotes().get(Note.FIVE));
        assertEquals(MOCK_ACCT_BALANCE - withdrawalAmount, response.getBody().getRemainingBalance());
        assertTrue(MOCK_ACCT_BALANCE - withdrawalAmount < 0);
        assertTrue(MOCK_ACCT_BALANCE - withdrawalAmount > (MOCK_ACCT_OVERDRAFT * -1));
    }

    @Test
    public void testWithdrawFromAccount_insufficientFunds() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);
        request.setAtmId(1);

        int withdrawalAmount = 1500;
        request.setWithdrawalAmount(withdrawalAmount);

        doNothing().when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);

        Mockito.when(accountService.getMaximumWithdrawalForAccount(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE + MOCK_ACCT_OVERDRAFT);
        Mockito.when(accountService.getAccountBalance(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE);
        Mockito.when(atmMachineService.getMaximumWithdrawal(withdrawalAmount))
                .thenReturn(1500);

        Map<Note, Integer> notes = new HashMap<>();
        notes.put(Note.FIFTY, 10);
        notes.put(Note.TWENTY, 0);
        notes.put(Note.TEN, 0);
        notes.put(Note.FIVE, 0);
        Mockito.when(atmMachineService.getNoteDistributionForWithdrawal(withdrawalAmount))
                .thenReturn(notes);
        Mockito.when(atmMachineService.getValueOfNotes(notes))
                .thenReturn(withdrawalAmount);


        assertThrows(InsufficientFundsException.class, () -> {
            this.atmController.withdrawFromAccount(request);
        });
    }

    @Test
    public void testWithdrawFromAccount_incompleteWithdrawal() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);
        request.setAtmId(1);

        int withdrawalAmount = 500;
        request.setWithdrawalAmount(withdrawalAmount);

        doNothing().when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);

        Mockito.when(accountService.getMaximumWithdrawalForAccount(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE + MOCK_ACCT_OVERDRAFT);
        Mockito.when(accountService.getAccountBalance(MOCK_ACCT_NUMBER))
                .thenReturn(MOCK_ACCT_BALANCE);

        int atmTotalVal = 300;
        Mockito.when(atmMachineService.getMaximumWithdrawal(withdrawalAmount))
                .thenReturn(atmTotalVal);

        Map<Note, Integer> notes = new HashMap<>();
        notes.put(Note.FIFTY, 4);
        notes.put(Note.TWENTY, 0);
        notes.put(Note.TEN, 0);
        notes.put(Note.FIVE, 0);
        Mockito.when(atmMachineService.getNoteDistributionForWithdrawal(withdrawalAmount))
                .thenReturn(notes);
        Mockito.when(atmMachineService.getValueOfNotes(notes))
                .thenReturn(atmTotalVal);

        ResponseEntity<WithdrawalResponse> response = this.atmController.withdrawFromAccount(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MOCK_ACCT_NUMBER, response.getBody().getAccountNumber());
        assertEquals(4, response.getBody().getNotes().get(Note.FIFTY));
        assertEquals(0, response.getBody().getNotes().get(Note.TWENTY));
        assertEquals(0, response.getBody().getNotes().get(Note.TEN));
        assertEquals(0, response.getBody().getNotes().get(Note.FIVE));
        assertEquals(MOCK_ACCT_BALANCE - atmTotalVal, response.getBody().getRemainingBalance());
    }

    @Test
    public void testWithdrawFromAccount_atmEmpty() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);
        request.setAtmId(1);

        int withdrawalAmount = 500;
        request.setWithdrawalAmount(withdrawalAmount);

        doNothing().when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);
        Mockito.when(atmMachineService.getMaximumWithdrawal(withdrawalAmount))
                .thenReturn(0);
        assertThrows(AtmEmptyException.class, () -> {
            this.atmController.withdrawFromAccount(request);
        });
    }

    @Test
    public void testWithdrawFromAccount_incorrectPin() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountNumber(MOCK_ACCT_NUMBER);
        request.setPin(MOCK_ACCT_PIN);
        request.setAtmId(1);

        int withdrawalAmount = 1500;
        request.setWithdrawalAmount(withdrawalAmount);

        doThrow(IncorrectPinException.class).when(accountService).verifyAccountPin(MOCK_ACCT_NUMBER, MOCK_ACCT_PIN);

        assertThrows(IncorrectPinException.class, () -> {
            this.atmController.withdrawFromAccount(request);
        });
    }

    @Test
    public void testWithdrawFromAccount_accountNotFound() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountNumber(1231231312);
        request.setPin(MOCK_ACCT_PIN);
        request.setAtmId(1);

        int withdrawalAmount = 1500;
        request.setWithdrawalAmount(withdrawalAmount);

        doThrow(AccountNotFoundException.class).when(accountService).verifyAccountPin(1231231312, MOCK_ACCT_PIN);

        assertThrows(AccountNotFoundException.class, () -> {
            this.atmController.withdrawFromAccount(request);
        });
    }

}
