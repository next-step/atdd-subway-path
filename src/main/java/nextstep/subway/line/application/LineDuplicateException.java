package nextstep.subway.line.application;

public class LineDuplicateException extends RuntimeException {
    public LineDuplicateException() {
        super("이미 존재하는 지하철 노선 이름입니다.");
    }
}
