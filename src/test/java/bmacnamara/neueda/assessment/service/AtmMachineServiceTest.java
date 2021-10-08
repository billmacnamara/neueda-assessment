package bmacnamara.neueda.assessment.service;

import bmacnamara.neueda.assessment.dao.AtmMachineRepository;
import bmacnamara.neueda.assessment.exception.InvalidWithdrawalRequestException;
import bmacnamara.neueda.assessment.model.AtmMachine;
import bmacnamara.neueda.assessment.model.Note;
import bmacnamara.neueda.assessment.service.impl.AtmMachineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AtmMachineServiceTest {

    @Mock
    AtmMachineRepository atmMachineRepository;

    @InjectMocks
    AtmMachineServiceImpl atmMachineService;

    AtmMachine mockMachine = new AtmMachine();

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMachine = new AtmMachine(1, 10, 30, 30, 20);
        Mockito.when(atmMachineRepository.findAtmMachineById(1)).thenReturn(mockMachine);
    }

    @Test
    public void testGetNoteDistributionForWithdrawal_singleTransactions() {
        AtmMachine mockMachine = new AtmMachine(1, 10, 30, 30, 20);
        Mockito.when(atmMachineRepository.findAtmMachineById(1)).thenReturn(mockMachine);

        Map<Note, Integer> map = this.atmMachineService.getNoteDistributionForWithdrawal(125);
        assertEquals(2, map.get(Note.FIFTY));
        assertEquals(1, map.get(Note.TWENTY));
        assertEquals(0, map.get(Note.TEN));
        assertEquals(1, map.get(Note.FIVE));

        map = this.atmMachineService.getNoteDistributionForWithdrawal(247);
        assertEquals(4, map.get(Note.FIFTY));
        assertEquals(2, map.get(Note.TWENTY));
        assertEquals(0, map.get(Note.TEN));
        assertEquals(1, map.get(Note.FIVE));

        map = this.atmMachineService.getNoteDistributionForWithdrawal(17);
        assertEquals(0, map.get(Note.FIFTY));
        assertEquals(0, map.get(Note.TWENTY));
        assertEquals(1, map.get(Note.TEN));
        assertEquals(1, map.get(Note.FIVE));

        map = this.atmMachineService.getNoteDistributionForWithdrawal(1097);
        assertEquals(10, map.get(Note.FIFTY));      // ATM is initialised with only 10 E50 notes
        assertEquals(29, map.get(Note.TWENTY));
        assertEquals(1, map.get(Note.TEN));
        assertEquals(1, map.get(Note.FIVE));

        map = this.atmMachineService.getNoteDistributionForWithdrawal(2000);
        assertEquals(10, map.get(Note.FIFTY));
        assertEquals(30, map.get(Note.TWENTY));
        assertEquals(30, map.get(Note.TEN));
        assertEquals(20, map.get(Note.FIVE));

        map = this.atmMachineService.getNoteDistributionForWithdrawal(4);
        assertEquals(0, map.get(Note.FIFTY));
        assertEquals(0, map.get(Note.TWENTY));
        assertEquals(0, map.get(Note.TEN));
        assertEquals(0, map.get(Note.FIVE));
    }

    @Test
    public void testGetNoteDistributionForWithdrawal_multipleTransactions() {
        Map<Note, Integer> map = this.atmMachineService.getNoteDistributionForWithdrawal(125);
        assertEquals(2, map.get(Note.FIFTY));
        assertEquals(1, map.get(Note.TWENTY));
        assertEquals(0, map.get(Note.TEN));
        assertEquals(1, map.get(Note.FIVE));

        this.atmMachineService.executeWithdrawal(map);
        Mockito.when(atmMachineRepository.findAtmMachineById(1)).thenReturn(mockMachine);

        map = this.atmMachineService.getNoteDistributionForWithdrawal(247);
        assertEquals(4, map.get(Note.FIFTY));
        assertEquals(2, map.get(Note.TWENTY));
        assertEquals(0, map.get(Note.TEN));
        assertEquals(1, map.get(Note.FIVE));

        this.atmMachineService.executeWithdrawal(map);
        Mockito.when(atmMachineRepository.findAtmMachineById(1)).thenReturn(mockMachine);

        map = this.atmMachineService.getNoteDistributionForWithdrawal(597);
        assertEquals(4, map.get(Note.FIFTY));
        assertEquals(19, map.get(Note.TWENTY));
        assertEquals(1, map.get(Note.TEN));
        assertEquals(1, map.get(Note.FIVE));
        this.atmMachineService.executeWithdrawal(map);
    }

    @Test
    public void testGetNoteDistributionForWithdrawal_negativeWithdrawalValue() {
        AtmMachine mockMachine = new AtmMachine(1, 10, 30, 30, 20);
        Mockito.when(atmMachineRepository.findAtmMachineById(1)).thenReturn(mockMachine);

        Exception exception = assertThrows(InvalidWithdrawalRequestException.class, () -> {
            this.atmMachineService.getNoteDistributionForWithdrawal(-125);
        });
    }



    @Test
    public void testGetMaxWithdrawal() {
        int maximumWithdrawal = this.atmMachineService.getMaximumWithdrawal(1);
        assertEquals(0, maximumWithdrawal);

        maximumWithdrawal = this.atmMachineService.getMaximumWithdrawal(10);
        assertEquals(10, maximumWithdrawal);

        maximumWithdrawal = this.atmMachineService.getMaximumWithdrawal(127);
        assertEquals(125, maximumWithdrawal);

        maximumWithdrawal = this.atmMachineService.getMaximumWithdrawal(15527);
        assertEquals(1500, maximumWithdrawal);

        maximumWithdrawal = this.atmMachineService.getMaximumWithdrawal(-15527);
        assertEquals(0, maximumWithdrawal);
    }

    @Test
    public void testExecuteWithdrawal() {
        Map<Note, Integer> notes = this.atmMachineService.getNoteDistributionForWithdrawal(125);
        this.atmMachineService.executeWithdrawal(notes);
        Mockito.verify(atmMachineRepository, Mockito.times(1)).save(mockMachine);
    }

}
