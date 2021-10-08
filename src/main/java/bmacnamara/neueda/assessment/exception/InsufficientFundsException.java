package bmacnamara.neueda.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "You do not have enough funds in your account to complete this withdrawal")
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super();
    }

    public InsufficientFundsException(String msg) {
        super(msg);
    }

    public InsufficientFundsException(String msg, Throwable t) {
        super(msg, t);
    }

}
