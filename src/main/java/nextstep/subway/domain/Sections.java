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

        while(section != null){
            ordered.add(section);
            Station downStation = section.getDownStation();
            section = sections.stream()
                    .filter(findSection -> findSection.getUpStation().equals(downStation))
                    .findAny()
                    .orElse(null);
        }

        return ordered;
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        if (!sections.isEmpty()) {
            checkExistStationInLine(section.getDownStation());
            // validSection(section);
            // 기존 하행과 신규 상행이 같거나
            // 기존 상행과 신규 하행이 같으면
            // 그냥 추가.
            // 기존 상행과 신규 상행이 같으면
            // 구간을 업데이트 해야함.
            if(isSameUpStation(upStation)){
                updateSection(section);
            }
        }
        this.sections.add(section);
    }

    private void validSection(Section newSection) {
        boolean isValid = false;
        isValid = isLastDownStation(newSection.getUpStation());
        isValid = isFirstUpStation(newSection.getDownStation());
    }

    private boolean isFirstUpStation(Station downStation) {
        return downStation.equals(getFirstStation());
    }

    private void updateSection(Section newSection) {
        Section findSection = this.sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findAny()
                .get();

        findSection.updateUpStation(newSection.getDownStation(), newSection.getDistance());
    }

    private boolean isSameUpStation(Station upStation) {
        return this.sections.stream()
                .anyMatch(section -> section.getUpStation().equals(upStation));
    }

    public List<Station> get() {
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

        while(findSection != null){
            Station lastStation = findSection.getDownStation();
            Optional<Section> optionalSection = sections.stream()
                    .filter(section -> section.getUpStation().equals(lastStation))
                    .findAny();

            if(!optionalSection.isPresent()){
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

    private Section getFirstSection(){
        Section findSection = this.sections.get(0);

        while(findSection != null){
            Station firstStation = findSection.getUpStation();
            Optional<Section> optionalSection = sections.stream()
                    .filter(section -> section.getDownStation().equals(firstStation))
                    .findAny();

            if(!optionalSection.isPresent()){
                break;
            }

            findSection = optionalSection.get();
        }

        return findSection;
    }

    private void checkExistStationInLine(Station downStation) {
        boolean exist = this.sections.stream()
                .anyMatch(section -> section.getId() == downStation.getId());

        if (exist) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isLastDownStation(Station upStation) {
        return upStation.equals(getLastStation());
    }

}