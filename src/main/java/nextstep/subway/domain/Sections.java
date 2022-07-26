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
        return this.sections.stream()
                .map(Section::getStationList)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        validateAdd(section);

        sections.add(section);
    }

    private void validateAdd(Section section) {
        Assert.isTrue(section != null, "section은 null일 수 없습니다.");

        // 상행역과 하행역 모두 등록되어있으면 예외 발생
        if (containsAllStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 등록되어있습니다.");
        }
    }

    private boolean containsAllStation(Section section) {
        return getStations().contains(section.getUpStation()) && getStations().contains(section.getDownStation());
    }

    public void remove(Section section) {
        sections.remove(section);
    }
}
