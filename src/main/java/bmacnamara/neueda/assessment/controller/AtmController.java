package bmacnamara.neueda.assessment.controller;

import bmacnamara.neueda.assessment.exception.AtmEmptyException;
import bmacnamara.neueda.assessment.exception.AtmServiceException;
import bmacnamara.neueda.assessment.exception.InsufficientFundsException;
import bmacnamara.neueda.assessment.model.*;
import bmacnamara.neueda.assessment.service.api.AccountService;
import bmacnamara.neueda.assessment.service.api.AtmMachineService;
import bmacnamara.neueda.assessment.service.impl.AccountServiceImpl;
import bmacnamara.neueda.assessment.service.impl.AtmMachineServiceImpl;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping(value ="/api/v1/atm")
@CommonsLog
public class AtmController {

    private final AccountService accountService;
    private final AtmMachineService atmMachineService;

    @Autowired
    public AtmController(AccountServiceImpl accountService, AtmMachineServiceImpl atmMachineService) {
        this.accountService = accountService;
        this.atmMachineService = atmMachineService;
    }

    /**
     * Endpoint which allows users to check the balance of their account, as well as the max they can withdraw from the ATM
     * @param request
     * @return
     * @throws AtmServiceException
     */
    @PostMapping("/balance")
    public ResponseEntity<BalanceCheckResponse> getBalanceForAccount(@RequestBody BalanceCheckRequest request)
            throws AtmServiceException {
        log.info("Balance requested for account " + request.getAccountNumber());

        this.accountService.verifyAccountPin(request.getAccountNumber(), request.getPin());
        double accountBalance = this.accountService.getAccountBalance(request.getAccountNumber());

        double maxWithdrawalAllowed = this.accountService.getMaximumWithdrawalForAccount(request.getAccountNumber());
        int maxWithdrawalPossible = this.atmMachineService.getMaximumWithdrawal(maxWithdrawalAllowed);

        BalanceCheckResponse response = new BalanceCheckResponse(request.getAccountNumber(), accountBalance, maxWithdrawalPossible);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint which allows users to withdraw from the ATM
     * @param request
     * @return
     * @throws AtmServiceException
     */
    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawalResponse> withdrawFromAccount(@RequestBody WithdrawalRequest request)
            throws AtmServiceException {
        log.info("Withdrawal of EUR" + request.getWithdrawalAmount() + " requested for account " + request.getAccountNumber());

        this.accountService.verifyAccountPin(request.getAccountNumber(), request.getPin());

        WithdrawalResponse response = new WithdrawalResponse();
        response.setAccountNumber(request.getAccountNumber());

        if (this.atmMachineService.getMaximumWithdrawal(request.getWithdrawalAmount()) == 0) {
            throw new AtmEmptyException();
        }

        if (this.accountService.getMaximumWithdrawalForAccount(request.getAccountNumber()) < request.getWithdrawalAmount()) {
            throw new InsufficientFundsException();
        } else if (this.atmMachineService.getMaximumWithdrawal(request.getWithdrawalAmount()) < request.getWithdrawalAmount()) {
            response.setMessage("The ATM does not have enough notes to fulfill your request. We will give you what we can!");
        }

        Map<Note, Integer> notes = this.atmMachineService.getNoteDistributionForWithdrawal(request.getWithdrawalAmount());
        response.setNotes(notes);

        this.atmMachineService.executeWithdrawal(notes);
        this.accountService.executeWithdrawal(request.getAccountNumber(), this.atmMachineService.getValueOfNotes(notes));

        double newBalance = this.accountService.getAccountBalance(request.getAccountNumber()) - this.atmMachineService.getValueOfNotes(notes);
        response.setRemainingBalance(newBalance);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
