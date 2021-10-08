package bmacnamara.neueda.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Incorrect PIN code provided")
public class IncorrectPinException extends AtmServiceException {

    public IncorrectPinException() {
        super();
    }

    public IncorrectPinException(String msg) {
        super(msg);
    }

    public IncorrectPinException(String msg, Throwable t) {
        super(msg, t);
    }

}