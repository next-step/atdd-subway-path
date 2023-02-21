package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        validateEqualSection(section);
        addUpStationEqualUpStation(section);
        addDownStationEqualDownStation(section);
        validateNotEqualStation(section);
        this.sections.add(section);
    }

    private void addUpStationEqualUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    validateSectionDistance(section, oldSection);
                    sections.add(new Section(oldSection.getLine(), section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });
    }

    private void addDownStationEqualDownStation(Section section) {
        sections.stream()
                .filter(oldSection -> section.getDownStation() == oldSection.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    validateSectionDistance(section, oldSection);
                    sections.add(new Section(oldSection.getLine(), oldSection.getUpStation(), section.getUpStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });
    }

    private void validateNotEqualStation(Section section) {
        if (!sections.isEmpty() && sections.stream()
                .noneMatch(oldSection -> section.getDownStation() == oldSection.getDownStation()
                        || section.getUpStation() == oldSection.getUpStation()
                        || section.getUpStation() == oldSection.getDownStation()
                        || section.getDownStation() == oldSection.getUpStation())) {
            throw new AddSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Section nextSection = findFirstSection();
        stations.add(nextSection.getUpStation());
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionByNextUpStation(nextSection.getDownStation());
        }
        return stations;
    }

    private Section findSectionByNextUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation() == station)
                .findFirst()
                .orElse(null);
    }

    private Section findFirstSection() {
        List<Station> downStationList = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(section -> !downStationList.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("첫 번째 역을 찾을 수 없습니다."));
    }

    private void validateSectionDistance(Section section, Section oldSection) {
        if (section.getDistance() >= oldSection.getDistance()) {
            throw new AddSectionException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    private void validateEqualSection(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation() && section.getDownStation() == oldSection.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new AddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
                });
    }

    public void delete(Station station) {
        Section downSection = getDownSection(station);
        Section upSection = getUpSection(station);

        if (downSection != null && upSection != null) {
            int distance = downSection.getDistance() + upSection.getDistance();
            sections.add(new Section(downSection.getLine(), downSection.getUpStation(), upSection.getDownStation(), distance));
            sections.removeAll(List.of(downSection, upSection));
        }

        if (downSection != null && upSection == null) {
            sections.remove(downSection);
        }
    }

    private Section getUpSection(Station station) {
        return sections.stream().filter(section -> section.getUpStation() == station)
                .findFirst()
                .orElse(null);
    }

    private Section getDownSection(Station station) {
        return sections.stream().filter(section -> section.getDownStation() == station)
                .findFirst()
                .orElse(null);
    }
}
