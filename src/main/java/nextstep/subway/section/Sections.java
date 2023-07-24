package nextstep.subway.section;

import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST})
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

    public List<Section> addSection(Section section) {
        // 비어있을경우
        if (sections.isEmpty()) {
            sections.add(section);
            return sections;
        }

        // 상행역과 매치되는 역을 찾는다.
        final Optional<Section> matchedUpStation = findByUpSection(section.getUpStation());

        // 상행역끼리 일치시
        if (matchedUpStation.isPresent()) {
            final Section matchedSection = matchedUpStation.get();

            // 하행역이 같을 수 없다.
            if (section.getDownStation().equals(matchedSection.getDownStation())) {
                throw new IllegalArgumentException("상행역, 하행역이 동시에 일치하는 구간을 추가할 수 없습니다.");
            }

            // 거리가 기존 구간보다 클 수 없다
            if (section.getDistance() >= matchedSection.getDistance()) {
                throw new IllegalArgumentException("기존 구간보다 큰 거리로 나눌 수 없습니다.");
            }

            matchedSection.changeUpStation(section.getDownStation(), matchedSection.getDistance() - section.getDistance());
            this.sections.add(section);
            return sections;
        }

        // 추가구간의 상행과 기존 하행역이 일치시
        final Optional<Section> matchedDownStation = findByDownSection(section.getUpStation());
        if (matchedDownStation.isPresent()) {
            final Section matchedSection = matchedDownStation.get();

            // 다음 구간이 없어야 한다
            if (findByUpSection(matchedSection.getDownStation()).isPresent()) {
                throw new IllegalArgumentException();
            }

            this.sections.add(section);
            return sections;
        }

        // 추가 구간의 하행과 기존 상행이 일치시 (새로운 상행 추가)
        final Optional<Section> matched = findByUpSection(section.getDownStation());
        if (matched.isPresent()) {
            // 첫 구간이어야 한다.
            if (hasNextSection(matched.get())) {
                throw new IllegalArgumentException();
            }

            sections.add(section);
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

        System.out.println("sections>>>" + this);

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
            .stream().filter(e -> e.getUpStation().getId().equals(station.getId()))
            .findFirst();
    }

    public Optional<Section> findByDownSection(final Station station) {
        return this.sections
            .stream().filter(e -> e.getDownStation().getId().equals(station.getId()))
            .findFirst();
    }

    public Section getFirstSection() {
        return this.sections.stream()
            .filter(e -> this.findByDownSection(e.getUpStation()).isEmpty())
            .findFirst()
            .orElse(null);
    }

    private boolean hasNextSection(Section section) {
        return findByUpSection(section.getDownStation()).isPresent();
    }

    @Override
    public String toString() {
        return "Sections{" +
            "sections=" + sections +
            '}';
    }
}
