package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        if (sections.size() <= 1) {
            return sections;
        }
        
        List<Section> list = new ArrayList<>();

        list.add(getFirstSection());

        for (int i = 0; i < sections.size(); i++) {
            Station downStation = list.get(list.size() - 1).getDownStation();

            sections.stream().filter(s -> s.getUpStation().equals(downStation)).findFirst()
                    .ifPresent(list::add);
        }

        return list;
    }

    private Section getFirstSection() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        Station upStation = upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst().orElseThrow(() -> new SectionException("상행 종점역이 존재하지 않습니다."));

        return sections.stream().filter(s -> s.getUpStation().equals(upStation)).findFirst()
                .orElseThrow(() -> new SectionException("상행 종점역이 존재하지 않습니다."));
    }

    public List<Station> getStations() {
        List<Station> stations = getSections()
                .stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(getDownFinalStation());

        return stations;
    }

    private Station getUpFinalStation() {
        return getSections().get(0).getUpStation();
    }

    private Station getDownFinalStation() {
        return getSections().get(sections.size() - 1).getDownStation();
    }

    public void addSection(Section section) {
        if (sections.size() > 0) {
            verifyAlreadyStation(section);
        }

        middleSectionAddForPreviousUpStationChange(section);

        sections.add(section);
    }

    private void verifyAlreadyStation(Section section) {
        boolean isAlreadyStation = sections
                .stream()
                .anyMatch(s -> s.getUpStation().equals(section.getUpStation()) &&
                        s.getDownStation().equals(section.getDownStation()));

        if (isAlreadyStation) {
            throw new SectionException("이미 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.");
        }
    }

    private void middleSectionAddForPreviousUpStationChange(Section section) {
        sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(previousStation -> previousStation.changeUpStation(section.getDownStation(), section.getDistance()));
    }

    public void removeSection(Station station, Line line) {
        verifyDeletable(station);

        boolean isUpFinalStation = getUpFinalStation().equals(station);
        boolean isDownFinalStation = getDownFinalStation().equals(station);

        if (isUpFinalStation) {
            removeUpFinalStation();

        } else if (isDownFinalStation) {
            removeDownFinalStation();

        } else {
            sectionRelocation(station, line);
        }
    }

    private void verifyDeletable(Station station) {
        if (sections.size() <= 1) {
            throw new SectionException("구간이 1개인 노선의 구간은 삭제할 수 없습니다.");
        }

        boolean isExistsStation = getStations().stream().anyMatch(s -> s.equals(station));
        if (!isExistsStation) {
            throw new SectionException("노선에 존재하지 않는 역은 삭제할 수 없습니다.");
        }
    }

    private void removeUpFinalStation() {
        Section section = findSectionByStation(s -> s.getUpStation().equals(getUpFinalStation()));
        sections.remove(section);
    }

    private void removeDownFinalStation() {
        Section section = findSectionByStation(s -> s.getDownStation().equals(getDownFinalStation()));
        sections.remove(section);
    }

    private void sectionRelocation(Station station, Line line) {
        Section sectionFindByUpStation = findSectionByStation(s -> s.getUpStation().equals(station));
        Section sectionFindByDownStation = findSectionByStation(s -> s.getDownStation().equals(station));

        sections.remove(sectionFindByUpStation);
        sections.remove(sectionFindByDownStation);

        int distance = sectionFindByUpStation.getDistance() + sectionFindByDownStation.getDistance();
        Section section = new Section(
                distance,
                sectionFindByDownStation.getUpStation(),
                sectionFindByUpStation.getDownStation(),
                line
        );
        sections.add(section);
    }

    private Section findSectionByStation(Predicate<? super Section> condition) {
        return getSections()
                .stream()
                .filter(condition)
                .findFirst()
                .orElseThrow(() -> new SectionException("존재하지 않는 구간입니다."));
    }
}
