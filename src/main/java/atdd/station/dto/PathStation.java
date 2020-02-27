package atdd.station.dto;

import atdd.station.domain.Station;

import java.util.Objects;

public class PathStation {
    private Long id;
    private String name;

    private PathStation() { }

    public static PathStation of(Long id, String name) {
        PathStation station = new PathStation();
        station.id = id;
        station.name = name;
        return station;
    }

    public static PathStation from(Station station) {
        PathStation pathStation = new PathStation();
        pathStation.id = station.getId();
        pathStation.name = station.getName();
        return pathStation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathStation)) return false;
        PathStation station = (PathStation) o;
        return Objects.equals(id, station.id) &&
                Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "PathStation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
