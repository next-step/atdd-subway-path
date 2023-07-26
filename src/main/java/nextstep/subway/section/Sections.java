package nextstep.subway.section;

import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    private static final long MINIMUM_SECTION_COUNT = 1L;

    @OneToMany(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> lineStations) {
        this.sections = lineStations;
    }

    public List<Section> getSections() {
        return sections;
    }

    protected void addOnlyList(Section section) {
        this.sections.add(section);
    }

    private SectionAddStrategy findAddStrategy(Section section) {
        if (sections.isEmpty()) {
            return new EmptyStrategy(this);
        }

        // 상행역끼리 일치시 (구간 자르기)
        if (findByUpStation(section.getUpStation()).isPresent()) {
            return new DivideEqUpStationStrategy(this);
        }

        // 하행역이 동일한 상태로 상행이 다를 시 (구간 자르기)
        if (findByDownStation(section.getDownStation()).isPresent()) {
            return new DivideEqDownStationStrategy(this);
        }

        // 추가구간의 상행과 기존 하행역이 일치시
        if (findByDownStation(section.getUpStation()).isPresent()) {
            return new AddLastStrategy(this);
        }

        // 추가 구간의 하행과 기존 상행이 일치시 (새로운 상행 추가)
        if (findByUpStation(section.getDownStation()).isPresent()) {
            return new AddFirstStrategy(this);
        }

        throw new IllegalArgumentException("추가할 수 있는 조건이 아닙니다");
    }

    public List<Section> addSection(Section section) {
        this.findAddStrategy(section).add(section);
        return sections;
    }

    public List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        if (sections.isEmpty()) {
            return stations;
        }

        Section cursor = getFirstSection();

        stations.add(cursor.getUpStation());
        stations.add(cursor.getDownStation());

        while(hasNextSection(cursor)) {
            cursor = findByUpStation(cursor.getDownStation()).orElseThrow();
            stations.add(cursor.getDownStation());
        }
        return stations;
    }

    public long countOfStations() {
        return sections.size();
    }

    public Optional<Section> findByUpStation(final Station station) {
        return this.sections
            .stream().filter(e -> e.isUpStation(station))
            .findFirst();
    }

    public Optional<Section> findByDownStation(final Station station) {
        return this.sections
            .stream().filter(e -> e.isDownStation(station))
            .findFirst();
    }

    public Section getFirstSection() {
        return this.sections.stream()
            .filter(e -> this.findByDownStation(e.getUpStation()).isEmpty())
            .findFirst()
            .orElse(null);
    }

    public boolean hasNextSection(Section section) {
        return findByUpStation(section.getDownStation()).isPresent();
    }

    public void deleteSection(final Station station) {
        if (!isDeletable()) {
            throw new IllegalStateException("최소 구간 개수라서 삭제할 수 없습니다.");
        }

        final Optional<Section> byUpStation = findByUpStation(station);
        final Optional<Section> byDownStation = findByDownStation(station);

        if (byUpStation.isEmpty() && byDownStation.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 역입니다");
        }

        if (byUpStation.isEmpty()) {
            sections.remove(byDownStation.get());
            return;
        }

        if (byDownStation.isEmpty()) {
            sections.remove(byUpStation.get());
            return;
        }

        final Section mergedSection = byDownStation.get();
        final Section sectionForMerge = byUpStation.get();
        mergedSection.changeDownStation(sectionForMerge.getDownStation(), mergedSection.getDistance() + sectionForMerge.getDistance());
        sections.remove(sectionForMerge);
    }

    private boolean isDeletable() {
        return MINIMUM_SECTION_COUNT < countOfStations();
    }

    @Override
    public String toString() {
        return "Sections{" +
            "sections=" + sections +
            '}';
    }
}
