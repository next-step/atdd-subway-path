package nextstep.subway.section;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        if (sections.size() > 0) {
            validateDuplicateStation(section);
        }

        if(isMiddleSection(section)){
            Section nextSection = getNextSection(section);
            nextSection.update(section);
        }

        this.sections.add(section);
    }

    private void validateDuplicateStation(Section section) {
        if (isMatchUpStation(section.getDownStation())) {
            throw new SubwayException("이미 등록되어있는 역입니다.");
        }
    }

    private Section getNextSection(Section section) {
        return this.sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new SubwayException("역을 찾을 수 없습니다."));
    }


    private boolean isMiddleSection(Section section) {
        return isMatchUpStation(section.getUpStation());
    }

    private boolean isMatchUpStation(Station station) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(station));
    }

    public void removeSection(Station station) {
        validateLastSection();
        validateEndSection(station);

        Section deleteSection = this.sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new SubwayException("역을 찾을 수 없습니다."));

        this.sections.remove(deleteSection);
    }

    private void validateLastSection() {
        if (sections.size() < 2) {
            throw new SubwayException("구간이 1개인 경우 역을 삭제할 수 없습니다.");
        }
    }

    private void validateEndSection(Station station) {
        if (!getEndStation().equals(station)) {
            throw new SubwayException("마지막 구간만 제거할 수 있습니다.");
        }
    }

    private Station getEndStation() {
        return getOrderedStations().get(sections.size());
    }

    public List<Station> getOrderedStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
