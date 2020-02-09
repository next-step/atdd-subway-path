package atdd.vo;

import atdd.domain.Station;

public class CreateStationRequestView {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Station toStation() {
        Station station = new Station();
        station.setName(name);
        return station;
    }
}
