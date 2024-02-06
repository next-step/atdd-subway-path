package nextstep.subway.domain;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
//    @OrderColumn(name = "line_order")
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return this.sections;
    }
    private static Stream<? extends Station> apply(Section s) {
        return Arrays.stream(new Station[]{s.getUpStation(), s.getDownStation()});
    }

    public void checkLineStationsDuplicate(final Station station) {
        boolean isDuplicate = this.sections.stream()
//                .flatMap((Section s1) -> apply(s1))
                .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                .distinct()
                .anyMatch(s -> s.isSame(station));

        if (isDuplicate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록되어 있는 지하철역 입니다.");
        }
    }

    public int count() {
        return this.sections.size();
    }

    public void removeSection(final Long stationId) {
        final Section deleteSection = this.sections.stream()
                .filter(s -> s.getDownStation().isSameId(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("역을 찾을 수 없습니다."));

        this.sections.remove(deleteSection);
    }

    public List<Station> getStations(Long upStationId) {
        Map<Long, Section> sectionMap = this.sections.stream()
                .collect(Collectors.toMap(s -> s.getUpStation().getId(), Function.identity()));

        List<Section> results = new ArrayList<>();
        for (int i = 0; i < this.sections.size(); i++) {
            Section section = sectionMap.get(upStationId);
            results.add(section);
            upStationId = section.getDownStation().getId();
        }

        return results.stream()
                .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void addEnd(Section section) {
        this.sections.add(section);
    }

    public void addFirst(Section section) {
        this.sections.add(0, section);
    }

    public void addMiddle(Station upStation, Station downStation, int distance, Line line) {
        Section upSection = this.sections.stream()
                .filter(s -> s.isSameUpStation(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지하철역이 존재하지 않습니다."));

        upSection.changeUpStation(downStation);
        upSection.changeDistance(upSection.getDistance() - distance);

        this.sections.add(new Section(upStation, downStation, distance, line));
    }

    public int totalDistance() {
        return this.sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }
}
