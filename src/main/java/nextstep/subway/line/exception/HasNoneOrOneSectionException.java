package nextstep.subway.line.exception;

public class HasNoneOrOneSectionException extends RuntimeException {

    public HasNoneOrOneSectionException() {
        super("구간이 2개 이상인 경우에만 삭제가 가능합니다.");
    }
}
