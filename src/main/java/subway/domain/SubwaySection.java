package subway.domain;

import java.util.Objects;

public class SubwaySection {

    private final Id id;
    private final SubwaySectionStation upStation;
    private final SubwaySectionStation downStation;
    private final Kilometer distance;

    public static SubwaySection register(Station startStation, Station endStation, Kilometer kilometer) {
        SubwaySectionStation startSectionStation = SubwaySectionStation.from(startStation);
        SubwaySectionStation endSectionStation = SubwaySectionStation.from(endStation);
        return new SubwaySection(startSectionStation, endSectionStation, kilometer);
    }

    public static SubwaySection of(Id id, SubwaySectionStation startStation, SubwaySectionStation endStation, Kilometer kilometer) {
        return new SubwaySection(id, startStation, endStation, kilometer);
    }

    private SubwaySection(SubwaySectionStation upStation, SubwaySectionStation downStation, Kilometer distance) {
        this.id = new Id();
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private SubwaySection(Id id, SubwaySectionStation upStation, SubwaySectionStation downStation, Kilometer distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station.Id getUpStationId() {
        return upStation.getId();
    }

    public String getUpStationName() {
        return upStation.getName();
    }

    public Station.Id getDownStationId() {
        return downStation.getId();
    }


    public String getDownStationName() {
        return downStation.getName();
    }

    public Id getId() {
        if (id.isNew()) {
            throw new IllegalArgumentException("아직 저장되지 않은 지하철 역입니다.");
        }
        return id;
    }

    public Kilometer getDistance() {
        return distance;
    }

    public boolean isNew() {
        return id.isNew();
    }

    boolean matchesDownStation(Station station) {
        return downStation.getId().equals(station.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwaySection that = (SubwaySection) o;
        return Objects.equals(upStation.getId(), that.upStation.getId()) && Objects.equals(downStation.getId(), that.downStation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation.getId(), downStation.getId());
    }

    public static class Id {
        private Long id;

        public Id(Long id) {
            this.id = id;
        }

        public Id() {
        }

        public Long getValue() {
            return id;
        }

        public boolean isNew() {
            return id == null;
        }
    }
}
