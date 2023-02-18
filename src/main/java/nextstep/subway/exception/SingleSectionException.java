package nextstep.subway.exception;

/**
 * 지하철 구간을 삭제할 시 등록된 구간이 최초 등록한 구간뿐일 경우 던지는 예외입니다.
 */
public class SingleSectionException extends SubwayException {

    private static final String MESSAGE ="구간은 2개 이상일 때 삭제할 수 있습니다.";

    public SingleSectionException() {
        super(MESSAGE);
    }
}
