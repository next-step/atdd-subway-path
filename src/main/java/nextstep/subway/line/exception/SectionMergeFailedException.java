package nextstep.subway.line.exception;

public class SectionMergeFailedException extends LineDomainException {
    private static final String MESSAGE = "구간의 변합에 실패했습니다.";

    public SectionMergeFailedException() {
        super(MESSAGE);
    }
}
