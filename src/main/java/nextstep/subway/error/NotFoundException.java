package nextstep.subway.error;

public class NotFoundException extends RuntimeException{
    public NotFoundException(Object value) { super(String.format("%s에 해당되는 리소스를 찾지 못했습니다.", value));}
}
