package nextstep.subway.domain.exception;

public class IllegalRemoveMinSectionSize extends RuntimeException {

    public IllegalRemoveMinSectionSize() {
        super("하나의 노선에 하나의 구간은 있어야합니다");
    }

}
