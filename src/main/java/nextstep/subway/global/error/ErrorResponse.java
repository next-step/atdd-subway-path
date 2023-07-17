package nextstep.subway.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private int status;
    private List<String> errorMessages;

    public static ErrorResponse of(HttpStatus status, List<String> errorMessages) {
        return ErrorResponse.builder()
                .status(status.value())
                .errorMessages(errorMessages)
                .build();
    }

}
