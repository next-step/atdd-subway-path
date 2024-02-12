package nextstep.subway.domain;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void removeSection(final Station deleteStation) {
        checkSectionSizeTwoUnder();

        final List<Section> sortedSections = getSections();
        Section lineUpSection = sortedSections.get(0);
        Section lineDownSection = sortedSections.get(sortedSections.size() - 1);

        if (lineUpSection.isSameUpStation(deleteStation)) {
            removeSection(lineUpSection);
        } else if (lineDownSection.isSameDownStation(deleteStation)) {
            removeSection(lineDownSection);
        } else {
            removeMiddle(sortedSections, deleteStation);
        }
    }

    private void removeMiddle(final List<Section> sortedSections, final Station deleteStation) {
        final Section upSection = sortedSections
                .stream()
                .filter(s -> s.isSameDownStation(deleteStation))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "지하철역이 존재 하지 않습니다."));
        final Section downSection = sortedSections
                .stream()
                .filter(s -> s.isSameUpStation(deleteStation))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "지하철역이 존재 하지 않습니다."));

        downSection.plusDistance(upSection.getDistance());
        downSection.changeUpStation(upSection.getUpStation());
        removeSection(upSection);
    }

    private void removeSection(final Section section) {
        this.sections.remove(section);
    }

    private void checkSectionSizeTwoUnder() {
        if (this.sections.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 수 없는 지하철 역 입니다.");
        }
    }

    public List<Section> getSections() {
        return this.sections.stream()
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .sorted()
                .flatMap(s -> Arrays.stream(s.getStations()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public int totalDistance() {
        return this.sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public void addSection(final Station upStation, final Station downStation, final int distance, final Line line) {
        final List<Station> sortedStations = getStations();
        Station lineDownStation = sortedStations.get(sortedStations.size() - 1);
        Station lineUpStation = sortedStations.get(0);

        if ((lineDownStation.isSame(upStation) && containsLineStations(downStation)) ||
                (lineUpStation.isSame(downStation) && containsLineStations(upStation))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록되어 있는 지하철역 입니다.");
        }

        if (lineDownStation.isSame(upStation) || lineUpStation.isSame(downStation)) {
            this.sections.add(new Section(upStation, downStation, distance, line));
            return;
        }

        addMiddle(upStation, downStation, distance, line);
    }

    private void addMiddle(Station upStation, Station downStation, int distance, Line line) {
        if (containsLineStations(downStation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록되어 있는 지하철역 입니다.");
        }

        Section upSection = this.sections.stream()
                .filter(s -> s.isSameUpStation(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지하철역이 존재하지 않습니다."));

        upSection.changeUpStation(downStation);
        upSection.reduceDistance(distance);

        this.sections.add(new Section(upStation, downStation, distance, line));
    }

    private boolean containsLineStations(final Station station) {
        return this.sections.stream()
                .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                .distinct()
                .anyMatch(s -> s.isSame(station));
    }
}
