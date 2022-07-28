package nextstep.subway.domain;

import nextstep.subway.domain.vo.SectionLocation;
import nextstep.subway.domain.vo.ToBeAddedSection;
import nextstep.subway.exception.DeleteSectionException;
import nextstep.subway.exception.NewlySectionUpStationAndDownStationNotExist;
import nextstep.subway.exception.SectionAllStationsAlreadyExistException;
import nextstep.subway.exception.SectionNotExistException;

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

    private boolean hasStation(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        return sections.stream().anyMatch(s -> s.hasStation(upStation))
                || sections.stream().anyMatch(s -> s.hasStation(downStation));
    }

    private boolean alreadyExistUpStationAndDownStation(Section newlySection) {
        return sections.stream()
                .anyMatch(section -> section.sameUpStationAndDownStation(newlySection));
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
        if (SectionLocation.NOT_BETWEEN != toBeAddedSection.getLocation()) {
            return;
        }
        sections.add(newlySection);
    }

    private void addNewlySectionIfBetween(Section newlySection, ToBeAddedSection toBeAddedSection) {

        SectionLocation location = toBeAddedSection.getLocation();
        if (!isBetweenLocation(location)) {
            return;
        }

        Section section = toBeAddedSection.getSection();
        int newDistance = section.betweenDistance(newlySection);
        Section betweenSection = betweenSection(newlySection, location, section, newDistance);

        sections.add(betweenSection);
        updateSectionToNew(newlySection, toBeAddedSection.getIndex());
    }

    private boolean isBetweenLocation(SectionLocation location) {
        return SectionLocation.BETWEEN_FRONT == location || SectionLocation.BETWEEN_BACK == location;
    }

    private Section betweenSection(Section newlySection, SectionLocation location, Section section, int newDistance) {
        if (SectionLocation.BETWEEN_FRONT == location) {
            return betweenUpSection(newlySection, section, newDistance);
        }
        if (SectionLocation.BETWEEN_BACK == location) {
            return betweenDownSection(newlySection, section, newDistance);
        }
        return null;
    }

    private Section betweenUpSection(Section newlySection, Section section, int newDistance) {
        return new Section(section.getLine(), section.getUpStation(), newlySection.getUpStation(), newDistance);
    }

    private Section betweenDownSection(Section newlySection, Section section, int newDistance) {
        return new Section(section.getLine(), newlySection.getDownStation(), section.getDownStation(), newDistance);
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

    private Optional<Station> next(Station downStation) {
        return sections.stream()
                .filter(section -> downStation.equals(section.getUpStation()))
                .map(Section::getDownStation)
                .findAny();
    }

    private Optional<Section> next(Section section) {
        Station downStation = section.getDownStation();
        return sections.stream()
                .filter(s -> downStation.equals(s.getUpStation()))
                .findAny();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void delete(Station station) {
        validateDelete();

        Section firstSection = firstSection();
        Section lastSection = lastSection();

        if (deletableBetweenSection(station, firstSection, lastSection)) {
            deleteBetweenSection(station);
            return;
        }

        deleteIfFirstSectionDeletable(firstSection, station);
        deleteIfLastSectionDeletable(lastSection, station);
    }

    private void validateDelete() {
        if (onlyOneSectionExist()) {
            throw new DeleteSectionException("상행역과 하행역만 존재하기 때문에 삭제할 수 없습니다.");
        }

    }

    private boolean onlyOneSectionExist() {
        return sections.size() == 1;
    }

    private boolean deletableBetweenSection(Station station, Section firstSection, Section lastSection) {
        return !firstSection.hasSameUpStation(station) && !lastSection.hasSameDownStation(station);
    }

    private void deleteIfFirstSectionDeletable(Section firstSection, Station station) {
        if (!firstSection.hasSameUpStation(station)) {
            return;
        }
        sections.remove(firstSection);
    }

    private void deleteIfLastSectionDeletable(Section lastSection, Station station) {
        if (!lastSection.hasSameDownStation(station)) {
            return;
        }
        sections.remove(lastSection);
    }

    private Section lastSection() {
        Section section = sections.get(0);
        while (true) {
            Optional<Section> nextSection = next(section);
            if (nextSection.isEmpty()) {
                break;
            }
            section = nextSection.get();
        }
        return section;
    }

    private void deleteBetweenSection(Station station) {
        Section frontSection = frontSection(station);
        Section behindSection = behindSection(station);
        sections.add(mergedSection(frontSection, behindSection));
        sections.remove(frontSection);
        sections.remove(behindSection);
    }

    private Section frontSection(Station station) {
        return sections.stream()
                .filter(section -> section.hasSameDownStation(station))
                .findAny()
                .orElseThrow(() -> new SectionNotExistException("구간을 찾을 수 없습니다."));
    }

    private Section behindSection(Station station) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(station))
                .findAny()
                .orElseThrow(() -> new SectionNotExistException("구간을 찾을 수 없습니다."));
    }

    private Section mergedSection(Section frontSection, Section behindSection) {
        return new Section(frontSection.getLine(), frontSection.getUpStation(), behindSection.getDownStation(),
                frontSection.getDistance() + behindSection.getDistance());
    }


}