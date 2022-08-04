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
import java.util.stream.IntStream;

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
            return;
        }

        if (isDownTerminal(section)) {
            sections.add(section);
            return;
        }

        int index = IntStream.range(0, sections.size())
                .filter(i-> sections.get(i).getDownStation().equals(section.getUpStation()))
                .findFirst()
                .orElse(0);

        validDistance(section, index);

        this.sections.add(index, section);
    }

    private void validDistance(Section section, int index) {
        if(this.sections.get(index).getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("등록하려는 구간 길이가 기존 구간 길이보다 클 수 없습니다.");
        }
    }

    private boolean isDownTerminal(Section section) {
        return getDownTerminal().equals(section.getUpStation());
    }

    private boolean isUpTerminal(Section section) {
        return getUpTerminal().equals(section.getDownStation());
    }

    private Station getUpTerminal() {
        return sections.get(0).getUpStation();
    }

    private Station getDownTerminal() {
        return sections.get(sections.size()-1).getDownStation();
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

    public void remove(Station station) {
        validateRemove(station);
        removeSection(station);
    }

    public void removeSection(Station station) {
        if (getUpTerminal().equals(station)) {
            sections.remove(getDownSection(station));
        } else if (getDownTerminal().equals(station)) {
            sections.remove(getUpSection(station));
        } else {
            Section upSection = getUpSection(station);
            Section downSection = getDownSection(station);
            int newDistance = upSection.getDistance() + downSection.getDistance();

            sections.add(new Section(upSection.getLine(), upSection.getUpStation(), downSection.getDownStation(), newDistance));

            sections.remove(upSection);
            sections.remove(downSection);
        }
    }

    private Section getDownSection(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section getUpSection(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validateRemove(Station station) {
        if (isLastSection()) {
            throw new IllegalStateException("마지막 구간은 삭제할 수 없습니다.");
        }

        if (notContainsStation(station)) {
            throw new IllegalArgumentException("등록되어있지 않은 구간입니다.");
        }
    }

    private boolean isLastSection() {
        return getSections().size() == 1;
    }

    private boolean notContainsStation(Station station) {
        return !getStations().contains(station);
    }
}
