package bmacnamara.neueda.assessment.service.impl;

import bmacnamara.neueda.assessment.dao.AtmMachineRepository;
import bmacnamara.neueda.assessment.exception.InvalidWithdrawalRequestException;
import bmacnamara.neueda.assessment.model.AtmMachine;
import bmacnamara.neueda.assessment.model.Note;
import bmacnamara.neueda.assessment.service.api.AtmMachineService;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@CommonsLog
public class AtmMachineServiceImpl implements AtmMachineService {

    private final AtmMachineRepository repository;

    @Autowired
    public AtmMachineServiceImpl(AtmMachineRepository repository) {
        this.repository = repository;
    }

    /**
     * Algorithm to determine the smallest number of notes to distribute for a withdrawal request
     * @param withdrawalAmount
     * @return
     */
    @Override
    public Map<Note, Integer> getNoteDistributionForWithdrawal(int withdrawalAmount) {
        if (withdrawalAmount < 0) throw new InvalidWithdrawalRequestException();

        AtmMachine machine = this.repository.findAtmMachineById(1);
        Map<Note, Integer> output = new HashMap<>();

        int runningTotal = withdrawalAmount;

        int fifties = Math.floorDiv(runningTotal, 50);
        if (fifties > machine.getCount50s()) {
            fifties = machine.getCount50s();
        }
        runningTotal = runningTotal - (fifties * 50);
        output.put(Note.FIFTY, fifties);

        int twenties = Math.floorDiv(runningTotal, 20);
        if (twenties > machine.getCount20s()) {
            twenties = machine.getCount20s();
        }
        runningTotal = runningTotal - (twenties * 20);
        output.put(Note.TWENTY, twenties);


        int tens = Math.floorDiv(runningTotal, 10);
        if (tens > machine.getCount10s()) {
            tens = machine.getCount10s();
        }
        runningTotal = runningTotal - (tens * 10);
        output.put(Note.TEN, tens);

        int fives = Math.floorDiv(runningTotal, 5);
        if (fives > machine.getCount5s()) {
            fives = machine.getCount5s();
        }
        output.put(Note.FIVE, fives);

        return output;
    }

    /**
     * Algorithm to determine the maximum amount that a user can draw from the ATM they are using
     * @param accountBalance
     * @return
     */
    @Override
    public int getMaximumWithdrawal(double accountBalance) {
        AtmMachine machine = this.repository.findAtmMachineById(1);
        
        if (accountBalance > machine.getTotalValue()) {
            return machine.getTotalValue();
        } else if (accountBalance <= 0) {
            return 0;
        }

        Map<Note, Integer> notes = getNoteDistributionForWithdrawal((int) accountBalance);
        return this.getValueOfNotes(notes);
    }

    /**
     * This functions totals the sum value of all notes in a transaction
     * @param notes
     * @return
     */
    @Override
    public int getValueOfNotes(Map<Note, Integer> notes) {
        int value = 0;
        value += notes.get(Note.FIFTY) * 50;
        value += notes.get(Note.TWENTY) * 20;
        value += notes.get(Note.TEN) * 10;
        value += notes.get(Note.FIVE) * 5;

        return value;
    }

    /**
     * Updating database to reflect the changes in the ATM's balance after a withdrawal
     * @param notes
     */
    @Override
    public void executeWithdrawal(Map<Note, Integer> notes) {
        AtmMachine machine = this.repository.findAtmMachineById(1);
        
        machine.setCount50s(machine.getCount50s() - notes.get(Note.FIFTY));
        machine.setCount20s(machine.getCount20s() - notes.get(Note.TWENTY));
        machine.setCount10s(machine.getCount10s() - notes.get(Note.TEN));
        machine.setCount5s(machine.getCount5s() - notes.get(Note.FIVE));

        StringBuilder sb = new StringBuilder("The withdrawal was executed successfully.\nThere are now:");
        sb.append("\n")
                .append(machine.getCount50s()).append(" EUR50 notes").append("\n")
                .append(machine.getCount20s()).append(" EUR20 notes").append("\n")
                .append(machine.getCount10s()).append(" EUR10 notes").append("\n")
                .append(machine.getCount5s()).append(" EUR5 notes").append("\n")
                .append("left in the machine, with a total value of EUR").append(machine.getTotalValue());
        String logMessage = sb.toString();

        log.info(logMessage);
        this.repository.save(machine);
    }

}
