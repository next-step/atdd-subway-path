package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DeleteSectionException extends IllegalArgumentException {

    public DeleteSectionException() {
        super("남은 구간이 1개인 역은 삭제할 수 없습니다.");
    }

}
