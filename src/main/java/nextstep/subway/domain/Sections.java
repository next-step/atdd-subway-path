package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionException;
import nextstep.subway.exception.DeleteSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        boolean findUpStation = allStations().stream()
                .anyMatch(station -> station.equals(section.getUpStation()));

        boolean findDownStation = allStations().stream()
                .anyMatch(station -> station.equals(section.getDownStation()));

        if (findUpStation && findDownStation) {
            throw new AddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        if (!findUpStation && !findDownStation) {
            throw new AddSectionException("상행역과 하행역 둘 중 하나라도 노선에 존재해야 합니다.");
        }
    }

    private void betweenSection(Section newSection) {
        sameNewAndExistingUpStation(newSection);
        sameNewAndExistingDownStation(newSection);
    }

    private void sameNewAndExistingUpStation(Section newSection) {
        Optional<Section> optionalExistingSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst();

        if (optionalExistingSection.isEmpty()) {
            return;
        }

        Section existingSection = optionalExistingSection.get();
        existingSection.changeExistingUpStationToNewDownStation(newSection);
    }

    private void sameNewAndExistingDownStation(Section newSection) {
        Optional<Section> optionalExistingSection = sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst();

        if (optionalExistingSection.isEmpty()) {
            return;
        }

        Section existingSection = optionalExistingSection.get();
        existingSection.changeExistingDownStationToNewUpStation(newSection);
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
        if (sections.size() == 1) {
            throw new DeleteSectionException("구간이 1개인 노선은 구간 삭제를 진행할 수 없습니다.");
        }

        if (!allStations().contains(station)) {
            throw new DeleteSectionException("삭제하려는 역이 노선에 등록되지 않은 역입니다.");
        }

        if (!lastSection().getDownStation().equals(station)) {
            throw new DeleteSectionException("삭제하려는 역이 마지막 구간의 역이 아닙니다.");
        }

        sections.remove(lastSection());
    }

    public Section firstSection() {
        return sections.stream()
                .filter(section -> matchSectionsDownStation(section.getUpStation()))
                .findAny()
                .orElseThrow();
    }

    private boolean matchSectionsDownStation(Station upStation) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(upStation));
    }

    public Section lastSection() {
        return sections.stream()
                .filter(section -> matchSectionsUpStation(section.getDownStation()))
                .findAny()
                .orElseThrow();
    }

    private boolean matchSectionsUpStation(Station downStation) {
        return sections.stream()
                .noneMatch(section -> section.getUpStation().equals(downStation));
    }

    public List<Section> getSections() {
        return unmodifiableList(sections);
    }
}
