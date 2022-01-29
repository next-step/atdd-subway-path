package nextstep.subway.exceptions;

public class LineNotFoundException extends BadRequestException{
    public static final String MESSAGE = "존재하지 않는 노선입니다. lineId = ";

    public LineNotFoundException(Long id) {
        super(MESSAGE + id);
    }
}
