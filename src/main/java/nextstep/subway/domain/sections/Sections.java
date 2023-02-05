package nextstep.subway.domain.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.sections.strategy.ChangeableSections;
import nextstep.subway.domain.sections.strategy.SectionAddStrategies;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    @Transient
    Line line;

    @Transient
    private static final SectionAddStrategies sectionAddStrategies = new SectionAddStrategies();

    protected Sections() {

    }

    public Sections(Line line) {
        this.line = line;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateAddSection(section);
        ChangeableSections changeableSections = sectionAddStrategies.findChangeableSections(this, section);
        sections.add(section);

        changeableSections.getDeprecatedSections()
            .forEach(sections::remove);
        sections.addAll(changeableSections.getAdditionalSections());
    }

    public void deleteSection(Long stationId) {
        if (!isDownMostStation(stationId)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public boolean isDownMostStation(Long stationId) {
        Section lastSection = sections.get(sections.size() - 1);
        return lastSection.isSameDownStation(stationId);
    }

    private void validateAddSection(Section section) {
        if (hasStation(section.getUpStation()) && hasStation(section.getDownStation())) {
            throw new CannotAddSectionException("상/하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        if (!hasStation(section.getUpStation()) && !hasStation(section.getDownStation())) {
            throw new CannotAddSectionException("상/하행역 모두 노선에 존재하지 않습니다.");
        }
    }

    private boolean hasStation(Station station) {
        return getStations().stream()
            .anyMatch(st -> st.equals(station));
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        if (!stations.isEmpty()) {
            stations.add(0, sections.get(0).getUpStation());
        }

        return stations;
    }

    public List<Section> getValue() {
        return Collections.unmodifiableList(sections);
    }

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sections sections1 = (Sections)o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
