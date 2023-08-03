package subway.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public class SubwaySectionStation {

    private final Station.Id id;
    private final String name;

    public static SubwaySectionStation from(Station station) {
        return new SubwaySectionStation(station);
    }

    private SubwaySectionStation(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public SubwaySectionStation(Station.Id id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwaySectionStation that = (SubwaySectionStation) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
