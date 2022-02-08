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
        Section findSection = this.sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findAny()
                .get();

        findSection.updateUpStation(newSection.getDownStation(), newSection.getDistance());
    }

    private void updateDownSection(Section newSection) {
        Section findSection = this.sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
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
        if (!getLastStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
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