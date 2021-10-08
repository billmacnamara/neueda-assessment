package bmacnamara.neueda.assessment.model;

public enum Note {
    FIVE(5),
    TEN(10),
    TWENTY(20),
    FIFTY(50);

    private int value;
    Note(int value) {
        this.value = value;
    }

    public int getNoteValue() {
        return this.value;
    }
}
