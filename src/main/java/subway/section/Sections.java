package subway.section;

import subway.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    @OrderColumn
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> sections() {
        return sections;
    }

    public List<Station> toStations() {
        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());
        return stations;
    }

    public boolean hasUnderOneSection() {
        return sections.size() <= 1;
    }

    public boolean isLastSection(Station station) {
        return lastSection().equalDownStation(station);
    }

    public Section lastSection() {
        return sections.get(sections.size() - 1);
    }

    public void add(Section newSection) {
        // sections가 비어있다면 조건없이 추가한다.
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        // 같은 구간은 추가할 수 없다.
        if (isSameSection(newSection)) {
            throw new IllegalArgumentException("동일한 구간을 추가할 수 없습니다.");
        }

        int num = sections.size();
        for (int position = 0; position < num; position++) {
            // newSection의 upstation과 originSection의 upstation이 같을 때 구간을 중간에 추가한다.
            Section originSection = sections.get(position);
            if (newSection.equalUpStation(originSection.getUpStation())) {
                validDistanceRule(originSection, newSection);
                sections.remove(originSection);
                sections.add(
                        position,
                        new Section(
                                newSection.getDownStation(),
                                originSection.getDownStation(),
                                originSection.subtractDistance(newSection)
                        )
                );
                sections.add(position, newSection);
                return;
            }

            // 추가하려는 section의 upStation이 기존 section의 downStation일 때 맨 뒤이다.
            if (newSection.equalUpStation(originSection.getDownStation())) {
                sections.add(newSection);
                return;
            }

            // 추가하려는 section의 downStation이 기존 section의 upStation일 때 맨 앞이다.
            if (newSection.equalDownStation(originSection.getUpStation())) {
                sections.add(0, newSection);
                return;
            }

            // newSection의 downStation과 originSection의 downStation이 같을 때 구간을 중간에 추가한다.
            if (newSection.equalDownStation(originSection.getDownStation())) {
                validDistanceRule(originSection, newSection);
                sections.remove(originSection);
                sections.add(position, newSection);
                sections.add(
                        position,
                        new Section(
                                originSection.getUpStation(),
                                newSection.getUpStation(),
                                originSection.subtractDistance(newSection)
                        )
                );
                return;
            }
        }

        throw new IllegalArgumentException("해당 구간을 추가할 수 없습니다.");
    }

    private boolean isSameSection(Section newSection) {
        return sections.stream().anyMatch(section -> section.equals(newSection));
    }

    private void validDistanceRule(Section originSection, Section newSection) {
        if (originSection.isDistanceLowerOrEqual(newSection)) {
            throw new IllegalArgumentException("신규 구간은 기존 구간보다 짧아야 합니다.");
        }
    }

    public void remove(Section section) {
        sections.remove(section);
    }
}
