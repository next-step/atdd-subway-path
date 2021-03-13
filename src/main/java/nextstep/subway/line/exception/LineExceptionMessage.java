package nextstep.subway.line.exception;

public class LineExceptionMessage {

    private LineExceptionMessage() {
    }

    public static final String EXCEPTION_MESSAGE_EXIST_LINE = "존재하는 지하철 노선 입니다.";
    public static final String EXCEPTION_MESSAGE_NON_EXIST_LINE = "존재하지 않는 지하철 노선입니다.";

    public static final String EXCEPTION_MESSAGE_NOT_DELETABLE_SECTION = "구간을 더이상 삭제할 수 없습니다.";
}
