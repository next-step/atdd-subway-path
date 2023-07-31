package subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

public class SubwayLine {
    private final Id id;

    @Getter
    private String name;
    @Getter
    private String color;
    @Getter
    private Station.Id startStationId;
    private final SubwaySections sections;

    public static SubwayLine register(String name, String color, SubwaySection subwaySection) {
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
        sections.validate(startStationId);
    }

    public Id getId() {
        if (isNew()) {
            throw new IllegalArgumentException("아직 저장되지 않은 지하철 노선입니다.");
        }
        return id;
    }

    public SubwaySection getSection(Station.Id stationId) {
        return sections.getSection(stationId);
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(SubwaySection subwaySection, SectionUpdateManager manager) {
        SectionAdder updater = manager.getUpdater(this);
        updater.apply(this, subwaySection);
        validate();
    }

    public void closeSection(Station station, SectionCloseManager manager) {
        SectionCloser closer = manager.getOperator(this);
        closer.apply(this, station);
        validate();
    }

    void registerSection(SubwaySection subwaySection) {
        sections.add(subwaySection);
    }

    public boolean existsUpStation(Station.Id stationId) {
        return sections.existsUpStation(stationId);
    }

    public List<SubwaySection> getSections() {
        return sections.getSections();
    }

    public int getSectionSize() {
        return sections.size();
    }

    void closeSection(Station station) {
        sections.close(station);
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
