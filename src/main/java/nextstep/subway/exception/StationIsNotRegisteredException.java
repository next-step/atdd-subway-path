package nextstep.subway.exception;

public class StationIsNotRegisteredException extends BadRequestException{
    public StationIsNotRegisteredException() {
        super("Station is not registered.");
    }
}
