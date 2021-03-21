package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class ApplicationException extends RuntimeException implements Serializable {

    private ApplicationType applicationType;

    public ApplicationException(ApplicationType applicationType) {
        super(applicationType.getMessage());
        this.applicationType = applicationType;
    }

    public HttpStatus getStatus() {
        return this.applicationType.getHttpStatus();
    }
}


