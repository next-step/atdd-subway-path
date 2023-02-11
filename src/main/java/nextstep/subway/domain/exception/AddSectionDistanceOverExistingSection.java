package nextstep.subway.domain.exception;

public class AddSectionDistanceOverExistingSection extends IllegalArgumentException {
    private static final String MESSAGE = "기존 구간 사이에 새로운 구간 추가시 새로운 구간의 길이는 기존 구간의 길이보다 작아야 합니다.";

    public AddSectionDistanceOverExistingSection() {
        super(MESSAGE);
    }
}
