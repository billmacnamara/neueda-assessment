package bmacnamara.neueda.assessment.service.api;

import bmacnamara.neueda.assessment.model.Note;

import java.util.Map;

public interface AtmMachineService {

    public Map<Note, Integer> getNoteDistributionForWithdrawal(int withdrawalAmount);
    public int getMaximumWithdrawal(double accountBalance);
    public int getValueOfNotes(Map<Note, Integer> notes);
    public void executeWithdrawal(Map<Note, Integer> notes);

}
