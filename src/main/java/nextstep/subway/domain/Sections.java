package nextstep.subway.domain;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    public void addSection(Section section) {
        if (values.isEmpty()) {
            values.add(section);
            return;
        }
        if (contains(section.getDownStation()) && contains(section.getUpStation())) {
            throw new SubwayException(SubwayExceptionMessage.SECTION_ALREADY_ADDED);
        }
        // 상행 종점 구간 추가
        if (equalFirstStation(section.getDownStation()) && !contains(section.getUpStation())) {
            values.add(section);
            return;
        }
        // 하행 종점 구간 추가
        if (equalLastStation(section.getUpStation()) && !contains(section.getDownStation())) {
            values.add(section);
            return;
        }
        // 중간 구간 추가 상행역 일치
        if (contains(section.getUpStation()) && !contains(section.getDownStation())) {
            Section includedSection = getIncludedSectionWhenEqualUpStation(section);
            includedSection.divideUpStation(section);
            values.add(section);
            return;
        }
        // 중간 구간 추가 하행역 일치
        if (contains(section.getDownStation()) && !contains(section.getUpStation())) {
            Section includedSection = getIncludedSectionWhenEqualDownStation(section);
            includedSection.divideDownStation(section);
            values.add(section);
            return;
        }
        throw new SubwayException(SubwayExceptionMessage.SECTION_CANNOT_ADD);
    }

    private Optional<Section> getAfterSection(Section section) {
        return values.stream()
                .filter(value -> value.equalUpStation(section.getDownStation())
                        && !value.equals(section))
                .findFirst();
    }

    private Optional<Section> getBeforeSection(Section section) {
        return values.stream()
                .filter(value -> value.equalDownStation(section.getUpStation())
                        && !value.equals(section))
                .findFirst();
    }

    private Section getFirstSection() {
        return values.stream()
                .filter(value -> getBeforeSection(value).isEmpty())
                .findFirst().orElseThrow();

    }

    private Section getIncludedSectionWhenEqualUpStation(Section section) {
        return values.stream()
                .filter(value -> value.equalUpStation(section.getUpStation()))
                .findFirst()
                .orElseThrow();
    }

    private Section getIncludedSectionWhenEqualDownStation(Section section) {
        return values.stream()
                .filter(value -> value.equalDownStation(section.getDownStation()))
                .findFirst()
                .orElseThrow();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<Station> getStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Section firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());

        Optional<Section> now = Optional.of(firstSection);

        while (now.isPresent()) {
            stations.add(now.get().getDownStation());
            now = getAfterSection(now.get());
        }

        return Collections.unmodifiableList(stations);

    }

    protected boolean equalLastStation(Station station) {
        return values.get(values.size() - 1).equalDownStation(station);
    }

    protected boolean equalFirstStation(Station station) {
        return values.get(0).equalUpStation(station);
    }

    public void remove(Station station) {
        values.remove(getStations().indexOf(station) - 1);
    }

    public int size() {
        return values.size();
    }

    public boolean contains(Station station) {
        return values.stream().anyMatch(section -> section.contains(station));

    }

    public List<Section> getValuesOrderBy() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<Section> sections = new ArrayList<>();
        Section firstSection = getFirstSection();

        Optional<Section> now = Optional.of(firstSection);

        while (now.isPresent()) {
            sections.add(now.get());
            now = getAfterSection(now.get());
        }

        return Collections.unmodifiableList(sections);
    }
}
