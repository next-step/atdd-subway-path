package nextstep.subway.domain;

import nextstep.subway.domain.vo.SectionLocation;
import nextstep.subway.domain.vo.ToBeAddedSection;
import nextstep.subway.exception.NewlySectionUpStationAndDownStationNotExist;
import nextstep.subway.exception.SectionAllStationsAlreadyExistException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section newlySection) {
        validateAddNewlySection(newlySection);
        ToBeAddedSection toBeAddedSection = toBeAddedSection(newlySection);
        addNewlySectionIfNotBetween(newlySection, toBeAddedSection);
        addNewlySectionIfBetween(newlySection, toBeAddedSection);
    }

    private void validateAddNewlySection(Section newlySection) {
        if (!sections.isEmpty() && !hasStation(newlySection)) {
            throw new NewlySectionUpStationAndDownStationNotExist("추가하고자 하는 상행역, 하행역이 존재하지 않아 추가할 수 없습니다.");
        }

        if (alreadyExistUpStationAndDownStation(newlySection)) {
            throw new SectionAllStationsAlreadyExistException("이미 구간 내 상행역, 하행역이 모두 존재하여 추가할 수 없습니다.");
        }
    }

    boolean hasStation(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        return sections.stream().anyMatch(s -> s.hasStation(upStation))
                || sections.stream().anyMatch(s -> s.hasStation(downStation));
    }

    boolean hasStation(Station station) {
        return sections.stream().anyMatch(section -> section.hasStation(station));
    }

    private ToBeAddedSection toBeAddedSection(Section newlySection) {
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            if (section.hasSameUpStation(newlySection)) {
                return new ToBeAddedSection(section, SectionLocation.BETWEEN_BACK, i);
            }
            if (section.hasSameDownStation(newlySection)) {
                return new ToBeAddedSection(section, SectionLocation.BETWEEN_FRONT, i);
            }
        }
        return new ToBeAddedSection(SectionLocation.NOT_BETWEEN);
    }

    private void addNewlySectionIfNotBetween(Section newlySection, ToBeAddedSection toBeAddedSection) {
        if (SectionLocation.NOT_BETWEEN == toBeAddedSection.getLocation()) {
            sections.add(newlySection);
        }
    }

    private void addNewlySectionIfBetween(Section newlySection, ToBeAddedSection toBeAddedSection) {

        if (SectionLocation.BETWEEN_FRONT == toBeAddedSection.getLocation()) {
            Section section = toBeAddedSection.getSection();
            int newDistance = section.betweenDistance(newlySection);
            sections.add(betweenUpSection(newlySection, section, newDistance));
            updateSectionToNew(newlySection, toBeAddedSection.getIndex());
        }

        if (SectionLocation.BETWEEN_BACK == toBeAddedSection.getLocation()) {
            Section section = toBeAddedSection.getSection();
            int newDistance = section.betweenDistance(newlySection);
            sections.add(betweenDownSection(newlySection, section, newDistance));
            updateSectionToNew(newlySection, toBeAddedSection.getIndex());
        }

    }

    private boolean alreadyExistUpStationAndDownStation(Section newlySection) {
        return sections.stream()
                .anyMatch(section -> section.sameUpStationAndDownStation(newlySection));
    }


    private Section betweenDownSection(Section newlySection, Section section, int newDistance) {
        return new Section(section.getLine(), newlySection.getDownStation(), section.getDownStation(), newDistance);
    }

    private Section betweenUpSection(Section newlySection, Section section, int newDistance) {
        return new Section(section.getLine(), section.getUpStation(), newlySection.getUpStation(), newDistance);
    }


    private void updateSectionToNew(Section newlySection, int i) {
        Section section;
        section = newlySection;
        sections.set(i, section);
    }

    public List<Station> allStations() {
        if (sections.isEmpty()) {
            return List.of();
        }
        List<Station> allStations = new ArrayList<>();
        Section firstSection = firstSection();
        addUpStation(allStations, firstSection);
        addAllDownStationRepeatedly(allStations, firstSection);
        return allStations;
    }

    private Section firstSection() {
        Section section = sections.get(0);
        while (true) {
            Optional<Section> preSection = pre(section);
            if (preSection.isEmpty()) {
                break;
            }
            section = preSection.get();
        }
        return section;
    }

    private Optional<Section> pre(Section section) {
        Station upStation = section.getUpStation();
        return sections.stream()
                .filter(s -> upStation.equals(s.getDownStation()))
                .findAny();
    }

    private void addUpStation(List<Station> allStations, Section firstSection) {
        Station upStation = firstSection.getUpStation();
        allStations.add(upStation);
    }

    private void addAllDownStationRepeatedly(List<Station> allStations, Section firstSection) {
        Station downStation = firstSection.getDownStation();
        while (true) {
            allStations.add(downStation);
            Optional<Station> nextDownStation = next(downStation);
            if (nextDownStation.isEmpty()) {
                break;
            }
            downStation = nextDownStation.get();
        }
    }

    private Optional<Station> next(Station downStation) {
        return sections.stream()
                .filter(section -> downStation.equals(section.getUpStation()))
                .map(Section::getDownStation)
                .findAny();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public Station lastStation() {
        if (sections.isEmpty()) {
            throw new IllegalStateException("노선에 등록된 역이 없습니다.");
        }
        return sections.get(sections.size() - 1).getDownStation();
    }

    public void delete(Station station) {
        validateDelete(station);
        sections.remove(sections.get(sections.size() - 1));
    }

    private void validateDelete(Station station) {
        if (notLastStation(station)) {
            throw new IllegalArgumentException("하행역만 삭제할 수 있습니다.");
        }

        if (onlyOneSectionExist()) {
            throw new IllegalArgumentException("상행역과 하행역만 존재하기 때문에 삭제할 수 없습니다.");
        }
    }

    private boolean onlyOneSectionExist() {
        return sections.size() == 1;
    }

    private boolean notLastStation(Station station) {
        return !sections.get(sections.size() - 1).getDownStation().equals(station);
    }
}