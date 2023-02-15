package nextstep.subway.exception;

public class EndStationsAreNotLinkedException extends BadRequestException {
    public EndStationsAreNotLinkedException() {
        super("End Stations are not linked.");
    }
}
