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

    public List<Section> addSection(Section section) {
        if (sections.isEmpty()) {
            new EmptyStrategy(this).add(section);
            return sections;
        }

        // 상행역끼리 일치시 (구간 자르기)
        if (findByUpSection(section.getUpStation()).isPresent()) {
            new DivideEqUpStationStrategy(this).add(section);
            return sections;
        }

        // 하행역이 동일한 상태로 상행이 다를 시 (구간 자르기)
        if (findByDownSection(section.getDownStation()).isPresent()) {
            new DivideEqDownStationStrategy(this).add(section);
            return sections;
        }

        // 추가구간의 상행과 기존 하행역이 일치시
        if (findByDownSection(section.getUpStation()).isPresent()) {
            new AddLastStrategy(this).add(section);
            return sections;
        }

        // 추가 구간의 하행과 기존 상행이 일치시 (새로운 상행 추가)
        if (findByUpSection(section.getDownStation()).isPresent()) {
            new AddFirstStrategy(this).add(section);
            return sections;
        }

        throw new IllegalArgumentException();
    }

    public List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        Section cursor = getFirstSection();
        if (cursor == null) {
            return stations;
        }

        stations.add(cursor.getUpStation());
        stations.add(cursor.getDownStation());

        while(hasNextSection(cursor)) {
            cursor = findByUpSection(cursor.getDownStation()).orElseThrow();
            stations.add(cursor.getDownStation());
        }
        return stations;
    }

    public long countOfStations() {
        return sections.size();
    }

    public Optional<Section> findByUpSection(final Station station) {
        return this.sections
            .stream().filter(e -> e.isUpStation(station))
            .findFirst();
    }

    public Optional<Section> findByDownSection(final Station station) {
        return this.sections
            .stream().filter(e -> e.isDownStation(station))
            .findFirst();
    }

    public Section getFirstSection() {
        return this.sections.stream()
            .filter(e -> this.findByDownSection(e.getUpStation()).isEmpty())
            .findFirst()
            .orElse(null);
    }

    public boolean hasNextSection(Section section) {
        return findByUpSection(section.getDownStation()).isPresent();
    }

    @Override
    public String toString() {
        return "Sections{" +
            "sections=" + sections +
            '}';
    }
}
