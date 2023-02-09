package nextstep.subway.domain;

import nextstep.subway.domain.exceptions.CanNotAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        Station newUpStation = newSection.getUpStation();
        Station newDownStation = newSection.getDownStation();

        if (hasStation(newUpStation) && hasStation(newDownStation)) {
            throw new CanNotAddSectionException("상행역과 하행역 모두 이미 존재하여 구간 추가 불가능");
        }

        if (!hasStation(newUpStation) && !hasStation(newDownStation)) {
            throw new CanNotAddSectionException("상행역과 하행역 모두 존재하지 않아 구간 추가 불가능");
        }

        if (isFirstStation(newDownStation)) {
            sections.add(newSection);
            return;
        }

        if (isLastStation(newUpStation)) {
            sections.add(newSection);
            return;
        }

        Section ordinarySection = sections.stream()
                .filter(it -> it.getUpStation().equals(newUpStation) || it.getDownStation().equals(newDownStation))
                .findFirst().get();

        Section splittedSection = ordinarySection.split(newSection);

        sections.remove(ordinarySection);
        sections.add(newSection);
        sections.add(splittedSection);
    }

    private boolean hasStation(Station station) {
        return getStations().contains(station);
    }

    private boolean isFirstStation(Station station) {
        return sections.stream().map(Section::getUpStation).anyMatch(it -> it.equals(station))
                && sections.stream().map(Section::getDownStation).noneMatch(it -> it.equals(station));
    }

    private boolean isLastStation(Station station) {
        return sections.stream().map(Section::getDownStation).anyMatch(it -> it.equals(station))
                && sections.stream().map(Section::getUpStation).noneMatch(it -> it.equals(station));
    }

    public List<Section> getSections() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Section> result = new ArrayList<>();
        result.add(sections.stream()
                .filter(it -> isFirstStation(it.getUpStation()))
                .findFirst().get());

        while (result.size() != sections.size()) {
            Section section = sections.stream()
                    .filter(it -> it.getUpStation().equals(result.get(result.size() - 1).getDownStation()))
                    .findFirst().get();
            result.add(section);
        }

        return result;
    }

    public List<Station> getStations() {
        return getSections().stream()
                .flatMap(it -> Stream.of(it.getUpStation(), it.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void remove(Station station) {
        Section section = sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        sections.remove(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
