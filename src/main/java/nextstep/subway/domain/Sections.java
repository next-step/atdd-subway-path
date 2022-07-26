package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Embeddable
@Getter
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public int getSize() {
        return sections.size();
    }

    public List<Station> getStations() {
        List<Station> stations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());
        stations.add(0, getUpTerminal());
        return stations;
    }

    public void add(Section section) {
        validateAdd(section);

        if (isUpTerminal(section)) {
            sections.add(0, section);
        }
    }

    private boolean isUpTerminal(Section section) {
        return getUpTerminal().equals(section.getDownStation());
    }

    private Station getUpTerminal() {
        return sections.get(0).getUpStation();
    }

    private void validateAdd(Section section) {
        Assert.isTrue(section != null, "section은 null일 수 없습니다.");

        // 상행역과 하행역 모두 등록되어있으면 예외 발생
        if (containsAllStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 등록되어있습니다.");
        }

        // 상행역과 하행역 모두 등록되어있지 않으면 예외 발생
        if (notContainsAllStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어있지 않습니다.");
        }
    }

    private boolean notContainsAllStation(Section section) {
        return !getStations().contains(section.getUpStation()) && !getStations().contains(section.getDownStation());
    }

    private boolean containsAllStation(Section section) {
        return getStations().contains(section.getUpStation()) && getStations().contains(section.getDownStation());
    }

    public void remove(Section section) {
        sections.remove(section);
    }
}
