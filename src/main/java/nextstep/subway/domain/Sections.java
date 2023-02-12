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

    public void add(Section section) {
        values.add(section);
    }

    public void addBetweenUp(Section section) {
        Section includedSection = getIncludedSectionWhenEqualUpStation(section);
        includedSection.divideUpStation(section);
        values.add(section);
    }

    public void addBetweenDown(Section section) {
        Section includedSection = getIncludedSectionWhenEqualDownStation(section);
        includedSection.divideDownStation(section);
        values.add(section);
    }

    public boolean isBetweenDown(Section section) {
        return contains(section.getDownStation()) && !contains(section.getUpStation());
    }

    public boolean isBetweenUp(Section section) {
        return contains(section.getUpStation()) && !contains(section.getDownStation());
    }

    public boolean canAddLastSection(Section section) {
        return equalLastStation(section.getUpStation()) && !contains(section.getDownStation());
    }

    public boolean canAddFirstSection(Section section) {
        return equalFirstStation(section.getDownStation()) && !contains(section.getUpStation());
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

    private Section getLastSection() {
        return getValuesOrderBy().get(size() - 1);

    }

    public Section getIncludedSectionWhenEqualUpStation(Section section) {
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

        if (!contains(station)) {
            throw new SubwayException(SubwayExceptionMessage.STATION_NOT_CONTAINED);
        }
        if (size() <= 1) {
            throw new SubwayException(SubwayExceptionMessage.STATION_CANNOT_REMOVE);
        }
        if (equalFirstStation(station)) {
            values.remove(getFirstSection());
            return;
        }
        if (equalLastStation(station)) {
            values.remove(getLastSection());
            return;
        }
        Section removeSection = values.stream()
                .filter(section -> section.equalDownStation(station))
                .findFirst()
                .orElseThrow(() -> new SubwayException(SubwayExceptionMessage.STATION_NOT_CONTAINED));

        Section updateSection = getAfterSection(removeSection)
                .orElseThrow(() -> new SubwayException(SubwayExceptionMessage.STATION_NOT_CONTAINED));

        updateSection.combineUpSection(removeSection);
        values.remove(removeSection);

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
