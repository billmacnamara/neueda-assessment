package bmacnamara.neueda.assessment.exception;

public class AtmServiceException extends RuntimeException {

    public AtmServiceException() {
        super();
    }

    public AtmServiceException(String msg) {
        super(msg);
    }

    public AtmServiceException(String msg, Throwable t) {
        super(msg, t);
    }

}
