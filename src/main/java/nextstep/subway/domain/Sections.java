package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        List<Section> ordered = new ArrayList<>();

        if (this.sections.isEmpty()) {
            return ordered;
        }

        Section section = getFirstSection();

        while (section != null) {
            ordered.add(section);
            Station downStation = section.getDownStation();
            section = sections.stream()
                    .filter(findSection -> findSection.getUpStation().equals(downStation))
                    .findAny()
                    .orElse(null);
        }

        return ordered;
    }

    public void addSection(Section newSection) {

        if (!sections.isEmpty()) {
            boolean existUpStation = getSections().stream()
                    .anyMatch(section -> section.getUpStation() == newSection.getUpStation());
            boolean existDownStation = getSections().stream()
                    .anyMatch(section -> section.getDownStation() == newSection.getDownStation());

            checkExistStationInLine(existUpStation, existDownStation, newSection);

            if (existUpStation) {
                updateUpSection(newSection);
            }

            if (existDownStation) {
                updateDownSection(newSection);
            }
        }

        this.sections.add(newSection);
    }

    private void updateUpSection(Section newSection) {
        Station downStation = newSection.getDownStation();

        Section findSection = this.sections.stream()
                .filter(section -> section.isSameUpStation(downStation))
                .findAny()
                .get();

        findSection.updateUpStation(newSection.getDownStation(), newSection.getDistance());
    }

    private void updateDownSection(Section newSection) {
        Station upStation = newSection.getUpStation();

        Section findSection = this.sections.stream()
                .filter(section -> section.isSameDownStation(upStation))
                .findAny()
                .get();

        findSection.updateDownStation(newSection.getUpStation(), newSection.getDistance());
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        List<Section> sections = getSections();
        if (!sections.isEmpty()) {
            stations.add(getFirstStation());
            stations.addAll(findDownStations());
        }

        return stations;
    }

    public void remove(Station station) {
        // order list
        List<Section> sections = getSections();
        Station firstStation = getFirstStation();
        Station lastStation = getLastStation();

        validRemoveStation(station);

        if (station.equalId(firstStation)) {
            sections.remove(0);
        }

        if (station.equalId(lastStation)) {
            sections.remove(sections.size() - 1);
        }

        if (!station.equalId(firstStation) && !station.equalId(lastStation)) {
            // Section upSection = findUpSection(station);
            // Section downSection = findDownSection(station);
        }
    }

    private Section findDownSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny()
                .get();
    }

    private Section findUpSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .get();
    }

    private void validRemoveStation(Station station) {
        if (sections.size() == 1) {
            throw new RuntimeException("더 이상 역을 제거할 수 없습니다.");
        }

        boolean existStation = sections.stream().anyMatch(section -> section.existStation(station));

        if (!existStation) {
            throw new RuntimeException("해당 라인에" + station.getName() + "이 존재하지 않습니다.");
        }
    }

    private List<Station> findDownStations() {
        return getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Section getLastSection() {
        Section findSection = this.sections.get(0);

        while (findSection != null) {
            Station lastStation = findSection.getDownStation();
            Optional<Section> optionalSection = sections.stream()
                    .filter(section -> section.getUpStation().equals(lastStation))
                    .findAny();

            if (!optionalSection.isPresent()) {
                break;
            }

            findSection = optionalSection.get();
        }

        return findSection;
    }

    private Station getLastStation() {
        return getLastSection().getDownStation();
    }

    private Station getFirstStation() {
        return getFirstSection().getUpStation();
    }

    private Section getFirstSection() {
        Section findSection = this.sections.get(0);

        while (findSection != null) {
            Station firstStation = findSection.getUpStation();
            Optional<Section> optionalSection = sections.stream()
                    .filter(section -> section.getDownStation().equals(firstStation))
                    .findAny();

            if (!optionalSection.isPresent()) {
                break;
            }

            findSection = optionalSection.get();
        }

        return findSection;
    }

    private void checkExistStationInLine(boolean existUpStation, boolean existDownStation, Section newSection) {

        boolean existStation = getStations().stream()
                .anyMatch(station -> newSection.getUpStation() == station || newSection.getDownStation() == station);

        if (existUpStation && existDownStation)
            throw new RuntimeException("이미 존재하는 구간");

        if (!existStation)
            throw new RuntimeException("등록할 수 없음");

    }

}