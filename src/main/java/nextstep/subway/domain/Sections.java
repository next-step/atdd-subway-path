package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionException;
import nextstep.subway.exception.DeleteSectionException;
import nextstep.subway.exception.SectionNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        validateAddSection(section);
        betweenSection(section);

        this.sections.add(section);
    }

    private void validateAddSection(Section section) {
        boolean matchedUpStation = anyMatchStation(section.getUpStation());
        boolean matchedDownStation = anyMatchStation(section.getDownStation());

        if (matchedUpStation && matchedDownStation) {
            throw new AddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        if (!matchedUpStation && !matchedDownStation) {
            throw new AddSectionException("상행역과 하행역 둘 중 하나라도 노선에 존재해야 합니다.");
        }
    }

    private boolean anyMatchStation(Station findStation) {
        return allStations().stream()
                .anyMatch(station -> station.equals(findStation));
    }

    private void betweenSection(Section newSection) {
        findSectionByStation(matchUpStation(newSection.getUpStation()))
                .ifPresent(section -> section.changeExistingUpStationToNewDownStation(newSection));

        findSectionByStation(matchDownStation(newSection.getDownStation()))
                .ifPresent(section -> section.changeExistingDownStationToNewUpStation(newSection));
    }

    public List<Station> allStations() {
        if (sections.isEmpty()) {
            return emptyList();
        }

        List<Station> orderedStations = new ArrayList<>();
        Section startSection = firstSection();
        Section lastSection = lastSection();

        orderedStations.add(startSection.getUpStation());

        while (!startSection.equals(lastSection)) {
            orderedStations.add(startSection.getDownStation());
            startSection = nextSection(startSection);
        }

        orderedStations.add(lastSection.getDownStation());

        return unmodifiableList(orderedStations);
    }

    private Section nextSection(Section firstSection) {
        return sections.stream()
                .filter(section -> firstSection.getDownStation().equals(section.getUpStation()))
                .findAny()
                .orElse(lastSection());
    }

    public void removeSection(Station station) {
        validateRemoveSection(station);
        if (!removeFirstOrLastSection(station)) {
            removeMiddleSection(station);
        }
    }

    private void validateRemoveSection(Station station) {
        if (sections.size() == 1) {
            throw new DeleteSectionException("구간이 1개인 노선은 구간 삭제를 진행할 수 없습니다.");
        }

        if (!allStations().contains(station)) {
            throw new DeleteSectionException("삭제하려는 역이 노선에 등록되지 않은 역입니다.");
        }
    }

    private boolean removeFirstOrLastSection(Station station) {
        if (firstSection().getUpStation().equals(station)) {
            sections.remove(firstSection());
            return true;
        }

        if (lastSection().getDownStation().equals(station)) {
            sections.remove(lastSection());
            return true;
        }
        return false;
    }

    private void removeMiddleSection(Station station) {
        Section sectionWithUpStation = findSectionByStation(matchUpStation(station))
                .orElseThrow(() -> new SectionNotFoundException("제거하려는 역을 상행역으로 갖는 구간이 없습니다."));

        Section sectionWithDownStation = findSectionByStation(matchDownStation(station))
                .orElseThrow(() -> new SectionNotFoundException("제거하려는 역을 하행역으로 갖는 구간이 없습니다."));

        sectionWithDownStation.connectStation(sectionWithUpStation);
        sections.remove(sectionWithUpStation);
    }

    private Optional<Section> findSectionByStation(Predicate<Section> sectionPredicate) {
        return sections.stream()
                .filter(sectionPredicate)
                .findFirst();
    }

    public Section firstSection() {
        return sections.stream()
                .filter(section -> matchSectionsDownStation(section.getUpStation()))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("상행 종점 구간이 존재하지 않습니다."));
    }

    public Section lastSection() {
        return sections.stream()
                .filter(section -> matchSectionsUpStation(section.getDownStation()))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("하행 종점 구간이 존재하지 않습니다."));
    }

    private boolean matchSectionsDownStation(Station upStation) {
        return sections.stream()
                .noneMatch(matchDownStation(upStation));
    }

    private boolean matchSectionsUpStation(Station downStation) {
        return sections.stream()
                .noneMatch(matchUpStation(downStation));
    }

    private Predicate<Section> matchDownStation(Station station) {
        return section -> section.getDownStation().equals(station);
    }

    private Predicate<Section> matchUpStation(Station station) {
        return section -> section.getUpStation().equals(station);
    }

    public List<Section> getSections() {
        return unmodifiableList(sections);
    }
}
