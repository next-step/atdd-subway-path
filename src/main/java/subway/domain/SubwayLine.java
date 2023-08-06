package subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SubwayLine {
    private final Id id;

    @Getter
    private String name;
    @Getter
    private String color;
    @Getter
    private Station.Id startStationId;
    private SubwaySections sections;

    public static SubwayLine register(String name, String color, Station upStation, Station downStation, Kilometer distance) {
        SubwaySection subwaySection = SubwaySection.register(upStation, downStation, distance);

        SubwayLine subwayLine = new SubwayLine(name, color, subwaySection);
        subwayLine.validate();
        return subwayLine;
    }

    public static SubwayLine of(Id id, String name, String color, Station.Id startStationId, List<SubwaySection> sectionList) {
        return new SubwayLine(id, name, color, startStationId, new SubwaySections(sectionList));
    }

    private SubwayLine(Id id, String name, String color, Station.Id startStationId, SubwaySections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.sections = sections;
    }

    private SubwayLine(String name, String color, SubwaySection section) {
        this.id = new Id();
        this.name = name;
        this.color = color;
        this.startStationId = section.getUpStationId();
        this.sections = new SubwaySections(section);
    }

    private void validate() {
        sections.validate();
    }

    public Id getId() {
        if (isNew()) {
            throw new IllegalArgumentException("아직 저장되지 않은 지하철 노선입니다.");
        }
        return id;
    }

    public Optional<SubwaySection> getSection(Station.Id stationId) {
        return sections.getSectionMatchedUpStation(stationId);
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, Kilometer kilometer, SectionAddManager manager) {
        SubwaySection subwaySection = SubwaySection.register(upStation, downStation, kilometer);
        SectionAdder updater = manager.getUpdater(this.sections, subwaySection);
        this.sections = updater.addSection(this.sections, subwaySection);
        this.startStationId = sections.getStartStationId();
        validate();
    }

    public void closeSection(Station station, SectionCloseManager manager) {
        SectionCloser closer = manager.getOperator(this.sections, station);
        this.sections = closer.closeSection(this.sections, station);
        this.startStationId = sections.getStartStationId();
        validate();
    }

    public boolean existsUpStation(Station.Id stationId) {
        return sections.existsUpStation(stationId);
    }

    public List<SubwaySection> getSections() {
        return sections.getSections();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Id {
        private Long id;

        public Id(Long id) {
            this.id = id;
        }

        public Long getValue() {
            return id;
        }

        public boolean isNew() {
            return id == null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id idObject = (Id) o;
            return Objects.equals(id, idObject.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
