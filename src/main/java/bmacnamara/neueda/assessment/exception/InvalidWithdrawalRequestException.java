package bmacnamara.neueda.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "An invalid amount was requested for this withdrawal")
public class InvalidWithdrawalRequestException extends AtmServiceException {

    public InvalidWithdrawalRequestException() {
        super();
    }

    public InvalidWithdrawalRequestException(String msg) {
        super(msg);
    }

    public InvalidWithdrawalRequestException(String msg, Throwable t) {
        super(msg, t);
    }

}
