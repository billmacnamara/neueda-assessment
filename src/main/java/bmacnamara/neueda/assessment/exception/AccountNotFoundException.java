package bmacnamara.neueda.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find account with account number provided")
public class AccountNotFoundException extends AtmServiceException {

    public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }

    public AccountNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
