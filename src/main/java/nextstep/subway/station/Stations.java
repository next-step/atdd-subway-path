package nextstep.subway.station;

import java.util.List;
import java.util.function.Consumer;

public class Stations {

    private final List<Station> stationList;

    private Stations(List<Station> stationList) {
        this.stationList = stationList;
    }

    public static Stations from(List<Station> stationList) {
        return new Stations(stationList);
    }

    public void forEach(Consumer<Station> action) {
        stationList.forEach(action);
    }
}
