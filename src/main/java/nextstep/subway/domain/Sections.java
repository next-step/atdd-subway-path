package nextstep.subway.domain;

import nextstep.subway.exception.SectionAddFailureException;
import nextstep.subway.exception.SectionDeleteFailureException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(
        mappedBy = "line",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        verifyAddableSection(section);
        sections.add(section);
    }

    public void remove(Section section) {
        verifyDeletableStation(section);
        this.sections.remove(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return List.of();
        }

        List<Station> stations = sections
            .stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        stations.add(getLastSection().getDownStation());

        return stations;
    }

    public Section getLastSection() {
        if (sections.isEmpty()) {
            return null;
        }
        return sections.get(sections.size() - 1);
    }

    private void verifyAddableSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        if(!section.getUpStation().equals(getLastSection().getDownStation())) {
            throw new SectionAddFailureException("새로운 구간의 상행역이 기존 구간의 하행역이 아닙니다.");
        }

        if (isAlreadyExistStation(section.getDownStation())) {
            throw new SectionAddFailureException("새로운 구간의 하행역이 기존 노선에 이미 존재합니다.");
        }
    }

    private void verifyDeletableStation(Section section) {
        if (hasOnlyOneSection()) {
            throw new SectionDeleteFailureException("노선의 구간은 최소 한 개 이상 존재해야 합니다.");
        }

        Section lastSection = getLastSection();
        if (lastSection == null || !lastSection.equals(section)) {
            throw new SectionDeleteFailureException("노선의 하행종점역만 제거할 수 있습니다.");
        }
    }

    private boolean isAlreadyExistStation(Station station) {
        return sections.stream().anyMatch(section ->
            station.equals(section.getUpStation()) || station.equals(section.getDownStation())
        );
    }

    private boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }
}
