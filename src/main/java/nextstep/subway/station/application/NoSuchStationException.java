package nextstep.subway.station.application;

public class NoSuchStationException extends RuntimeException{
    public NoSuchStationException(String msg){
        super(msg);
    }
}
