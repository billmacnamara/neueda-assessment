package bmacnamara.neueda.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Withdrawals from this ATM are not possible at this time")
public class AtmEmptyException extends AtmServiceException {

    public AtmEmptyException() {
        super();
    }

    public AtmEmptyException(String msg) {
        super(msg);
    }

    public AtmEmptyException(String msg, Throwable t) {
        super(msg, t);
    }

}
