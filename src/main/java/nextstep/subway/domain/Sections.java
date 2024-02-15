package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.IllegalSectionException;

@Embeddable
public class Sections {
    public static final int ONLY_ONE_SECTION = 1;

    @Override
    public String toString() {
        return "Sections{" +
               "sections=" + sections +
               '}';
    }

    @OneToMany(cascade = CascadeType.ALL)
    private List<Section> sections;

    protected Sections() {
        this.sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.isPossibleToAdd(section.getUpStation())) {
            throw new IllegalSectionException("기존 노선의 하행 종점역과 추가하려는 구간의 상행역이 달라 추가할 수 없습니다.");
        }
        sections.add(section);
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void removeLastSection(Station station) {
        if (ONLY_ONE_SECTION == sections.size()) {
            throw new IllegalSectionException("해당 노선에 구간이 1개만 남아 있어 삭제할 수 없습니다.");
        }
        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.isPossibleToDelete(station)) {
            throw new IllegalSectionException("해당 역은 노선에 등록된 하행 종점역이 아닙니다.");
        }
        sections.remove(sections.size() - 1);
    }

    public Stations getStations() {
        List<Station> stations = sections.stream()
                .flatMap(section ->
                             Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
        return Stations.of(stations);
    }
}
