package atdd.station.domain.query;

public interface StationRepositoryQuery {

    StationQueryView findStationWithLine(Long stationId);

}
