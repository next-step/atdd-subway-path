package nextstep.subway.error;

public class NameExistsException extends RuntimeException{
    public NameExistsException(Object value) { super(String.format("%s의 이름이 존재합니다.", value)); }
}
