package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.Getter;
import nextstep.subway.exception.SectionException;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section insertSection) {
        if(!sections.isEmpty()) {
            isExistSection(insertSection);
            isExitsUpStationOrDownStation(insertSection);

            addSectionProcess(insertSection);
        }
        sections.add(insertSection);
    }

    private void addSectionProcess(Section insertSection) {
        for(int i = 0 ; i < sections.size() ; i++) {
            Section section = sections.get(i);

            if(section.isDownStationConnection(insertSection)
                && !(i == 0 && section.getDownStation().equals(insertSection.getUpStation()))) {
                section.changeDownStation(insertSection.getUpStation(), insertSection.getDistance());
            }

            if (section.isUpStationConnection(insertSection)
                && !(i == getLastIndexSections() && section.getUpStation().equals(insertSection.getDownStation()))) {
                section.changeUpStation(insertSection.getDownStation(), insertSection.getDistance());
            }
        }
    }

    private void isExistSection(Section addSection) {
        sections.stream()
            .filter(section -> addSection.isEqualsUpStationAndDownStation(section))
            .findFirst()
            .ifPresent(section -> {
                throw new SectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
            });
    }

    private Section isExitsUpStationOrDownStation(Section insertSection) {
        return sections.stream()
            .filter(section -> section.isConnection(insertSection))
            .findFirst()
            .orElseThrow(() -> new SectionException("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없습니다."));
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findFirstUpStation();
        stations.add(downStation);

        while (sections.size() + 1 != stations.size()) {
            downStation = findNextLineStation(downStation).getDownStation();
            stations.add(downStation);
        }

        return Collections.unmodifiableList(stations);
    }

    private Station findFirstUpStation() {
        return sections.stream()
            .map(Section::getUpStation)
            .filter(this::isFirstStation)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Section findNextLineStation(Station downStation) {
        return sections.stream()
            .filter(it -> downStation.equals(it.getUpStation()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isFirstStation(Station station) {
        return sections.stream()
            .noneMatch(currentStation -> station.equals(currentStation.getDownStation()));
    }

    public void removeStation(Station station) {
        if(!sections.get(getLastIndexSections()).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(getLastIndexSections());
    }

    private int getLastIndexSections() {
        return sections.size() - 1;
    }

}
