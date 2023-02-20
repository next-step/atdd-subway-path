package nextstep.subway.domain;

public class SectionException extends RuntimeException {
    private String message;
    public SectionException(String message) {
        this.message = message;
    }
}
