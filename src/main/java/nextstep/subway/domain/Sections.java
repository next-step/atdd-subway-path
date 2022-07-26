package nextstep.subway.domain;

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

    public void add(Section section) {

        for (int i = 0; i < sections.size(); i++) {
            Section s = sections.get(i);
            if (section.getUpStation().equals(s.getUpStation())) {

                final int newDistance = s.getDistance() - section.getDistance();

                if (newDistance < 0) {
                    throw new IllegalArgumentException("구간의 거리가 같아서 추가할 수 없습니다.");
                }

                sections.add(i + 1, new Section(s.getLine(), section.getDownStation(), s.getDownStation(), newDistance));
                sections.set(i, section);
                return;
            }
        }

        sections.add(section);

    }

    public List<Station> allStations() {

        Section firstSection = firstSection();
        if (firstSection == null) {
            return List.of();
        }

        List<Station> allStations = new ArrayList<>();
        allStations.add(firstSection.getUpStation());

        Station downStation = firstSection.getDownStation();
        while (true) {
            allStations.add(downStation);
            Station finalDownStation = downStation;
            Optional<Station> stationOptional = sections.stream()
                    .filter(section -> finalDownStation.equals(section.getUpStation()))
                    .map(Section::getDownStation)
                    .findAny();
            if (stationOptional.isEmpty()) {
                break;
            }
            downStation = stationOptional.get();
        }

        return allStations;
    }

    private Section firstSection() {

        if (sections.isEmpty()) {
            return null;
        }

        Section section = sections.get(0);
        Station upStation = section.getUpStation();

        while (true) {
            Station finalUpStation = upStation;
            Optional<Section> sectionOptional = sections.stream()
                    .filter(s -> finalUpStation.equals(s.getDownStation()))
                    .findAny();
            if (sectionOptional.isEmpty()) {
                break;
            }
            section = sectionOptional.get();
            upStation = section.getUpStation();
        }

        return section;
    }

    public boolean hasStation(Station station) {
        return sections.stream().anyMatch(section -> section.hasStation(station));
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