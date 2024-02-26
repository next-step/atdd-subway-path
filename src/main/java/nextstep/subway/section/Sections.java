package nextstep.subway.section;

import nextstep.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {
    private final static int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {}

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for(Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        stations = new ArrayList<>(new HashSet<>(stations));

        return stations;
    }

    public void addSection(Line line, Section requestSection) {
        validatePossibleSection(line, requestSection);

        if(isFirstPositionAddition(requestSection)) {
            requestSection.registerLine(line);
            return;
        }

        if(isMiddlePositionAddition(requestSection)) {
            addNewSection(line, requestSection);
            requestSection.registerLine(line);
            return;
        }

        validateEndPositionSection(requestSection);
        requestSection.registerLine(line);
    }

    private Section lastSection() {
        return sections.get(sections.size()-1);
    }

    public boolean isLastStation(Station deleteStation) {
        if(deleteStation.equals(lastSection().getDownStation())) {
            return true;
        }
        return false;
    }

    private boolean isStationMatched(Section section) {
        if(sections.size() > 0){
            return section.getUpStation().equals(lastSection().getDownStation());
        }
        return true;
    }

    private boolean isExistStation(Station newStation) {
        for(Section section : sections) {
            if(newStation.equals(section.getUpStation())){
                return true;
            }
        }
        return false;
    }

    private void validatePossibleSection(Line line, Section requestSection) {
        for(Section section : sections) {
            if(line.equals(requestSection.getLine()) && section.getDownStation().equals(requestSection.getDownStation())){
                throw new BadRequestException("추가할 수 없는 구간입니다.");
            }
        }
    }

    private void validateFirstSection(Section section, Section newSection) {
        if(section.getDownStation().equals(newSection.getUpStation())){
            throw new BadRequestException("새로운 구간의 상행역이 이미 노선에 등록된 역입니다.");
        }
    }

    public void validateEndPositionSection(Section newSection) {
        if(isExistStation(newSection.getDownStation())){
            throw new BadRequestException("새로운 구간의 하행역이 이미 노선에 등록된 역입니다.");
        }

        if(!isStationMatched(newSection)){
            throw new BadRequestException("새로운 구간의 상행역과 노선의 하행역이 일치하지 않습니다.");
        }
    }

    public boolean isFirstPositionAddition(Section newSection) {
        for(Section section : sections) {
            if(section.getUpStation().equals(newSection.getDownStation())) {
                validateFirstSection(section, newSection);
                return true;
            }
        }
        return false;
    }

    public boolean isMiddlePositionAddition(Section requestSection) {
        for(Section section : sections) {
            if(section.getUpStation().equals(requestSection.getUpStation()))
                return section.validMiddleSection(requestSection);
        }
        return false;
    }

    public void addNewSection(Line line, Section requestSection) {
        Section existingSection = new Section();

        for(Section section : sections) {
            if(section.getUpStation().equals(requestSection.getUpStation())) {
                existingSection = section;
            }
        }

        Section newSection = createNewSection(existingSection, requestSection);
        newSection.registerLine(line);
    }

    public void deleteSection(Station deleteStation) {
        validDeleteSection();
        //마지막 역인 경우
        if(isLastStation(deleteStation)) {
            deleteLastStation(deleteStation);
            return;
        }
        //마지막 역이 아닌 경우
        deleteStation(deleteStation);
    }

    private void validDeleteSection() {
        if(sections.size() == MIN_SECTION_SIZE) {
            throw new BadRequestException("구간이 1개 남은 경우 삭제할 수 없습니다.");
        }
    }

    private void deleteLastStation(Station deleteStation) {
        Section lastSection = lastSection();

        if(!deleteStation.equals(lastSection().getDownStation())){
            throw new BadRequestException("노선의 하행 종점역이 아닙니다.");
        }

        sections.remove(lastSection);
    }

    private void deleteStation(Station deleteStation) {
        Section deleteSection = sections.stream()
                .filter(section -> section.getUpStation().equals(deleteStation))
                .findAny().get();

        for(Section section : sections) {
            if(section.getDownStation().equals(deleteStation)) {
                section.changeDownStationAndDistance(deleteSection.getDownStation(), deleteSection.getDistance());
            }
        }

        sections.removeIf(s -> s.equals(deleteSection));
    }

    private Section createNewSection(Section existingSection, Section requestSection) {
        Section newSection = new Section(requestSection.getDownStation(), existingSection.getDownStation(), existingSection.getDistance() - requestSection.getDistance());
        removeSection(existingSection);
        return newSection;
    }

    private void removeSection(Section section) {
        sections.removeIf(s -> s.equals(section));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(getSections(), sections1.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSections());
    }
}
