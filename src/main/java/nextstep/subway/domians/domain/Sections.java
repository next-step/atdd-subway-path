package nextstep.subway.domians.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (invalidUpStation(section) || alreadyExistsStation(section)) {
            throw new IllegalArgumentException("invalid section");
        }
        this.sections.add(section);
    }

    public boolean invalidUpStation(Section section) {
        Optional<Station> optionalStation = getEndStation();
        if (optionalStation.isPresent()) {
            Station endStation = optionalStation.get();
            return !Objects.equals(endStation, section.getUpStation());
        }
        return false;
    }

    public boolean alreadyExistsStation(Section section) {
        return this.getStations().stream()
            .anyMatch(station -> Objects.equals(station,section.getDownStation()));
    }

    public List<Station> getStations() {
        return this.sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
    }


    public void removeSection(Long downStationId) {
        if (isNotLastStation(downStationId) || getSectionSize() < 2) {
            throw new IllegalArgumentException("invalid section");
        }
        sections.remove(sections.size() - 1);
    }

    public Optional<Station> getEndStation() {
        if (sections.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sections.get(sections.size() - 1).getDownStation());
    }

    public int getSectionSize() {
        return this.sections.size();
    }

    private boolean isNotLastStation(Long stationId) {
        Optional<Station> optionalStation = getEndStation();
        if (optionalStation.isPresent()) {
            Station endStation = optionalStation.get();
            return !Objects.equals(endStation.getId(), stationId);
        } else {
            return false;
        }
    }
}
