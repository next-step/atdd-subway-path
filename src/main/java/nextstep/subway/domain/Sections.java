package nextstep.subway.domain;


import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    private static final String SECTION_ALREADY_EXISTS = "상행역과 하행역 모두 일치힙니다. 이미 등록된 노선입니다.";
    private static final String MATCHED_SECTION_NOT_EXISTS = "상행역과 하행역 둘중 한개라도 일치하는 구간이 없습니다.";
    private static final String CAN_UPDATE_IF_DISTANCE_SHORTER = "역과 역사이에 추가될 경우 새로운 역의 길이가 기존보다 짧아야 합니다.";


    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public void add(Section section) {

        validateForAdding(section);

        if (canAddToFirstOrEndPosition(section)) {
            sections.add(section);
            return;
        }

        sections.stream()
                .filter(origin -> origin.hasOneMatchedStation(section))
                .findFirst()
                .ifPresent(origin -> {
                    if (!section.hasShorterDistanceThan(origin)) {
                        throw new IllegalArgumentException(CAN_UPDATE_IF_DISTANCE_SHORTER);
                    }
                    sections.add(origin.divideBy(section));
                    sections.add(section);
                    sections.remove(origin);
                });
    }

    private boolean canAddToFirstOrEndPosition(Section section) {
        return sections.isEmpty()
                || section.getDownStation().equals(findFirstStation())
                || section.getUpStation().equals(findEndStation());
    }

    private void validateForAdding(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        if (findAnyBothMatchedStation(section).isPresent()) {
            throw new IllegalArgumentException(SECTION_ALREADY_EXISTS);
        }

        if (findAnyOneMatchedStation(section).isEmpty()) {
            throw new IllegalArgumentException(MATCHED_SECTION_NOT_EXISTS);
        }
    }

    public void delete(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        List<Station> orderedStations = new ArrayList<>();

        Station firstStation = findFirstStation();
        orderedStations.add(firstStation);

        while (hasSectionContainingStation(firstStation)) {
            Section section = findSectionContainingStation(firstStation);
            orderedStations.add(section.getDownStation());
            firstStation = section.getDownStation();
        }

        return Collections.unmodifiableList(orderedStations);
    }

    private Section findSectionContainingStation(Station station) {
        return sections.stream()
                .filter(i -> i.getUpStation().equals(station))
                .findFirst()
                .orElse(new Section());
    }

    private boolean hasSectionContainingStation(Station firstStation) {
        return sections.stream().anyMatch(i -> i.getUpStation().equals(firstStation));
    }

    private Station findFirstStation() {
        List<Station> upStations = findUpStations();
        List<Station> downStations = findDownStations();
        return upStations.stream()
                .filter(upStation -> !downStations.contains(upStation))
                .findAny()
                .orElse(new Station());
    }

    private Station findEndStation() {
        List<Station> upStations = findUpStations();
        List<Station> downStations = findDownStations();
        return downStations.stream()
                .filter(downStation -> !upStations.contains(downStation))
                .findAny()
                .orElse(new Station());
    }

    private List<Station> findUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> findDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Optional<Section> findAnyOneMatchedStation(Section section) {
        return sections.stream()
                .filter(origin -> origin.hasOneMatchedStation(section))
                .findAny();
    }

    private Optional<Section> findAnyBothMatchedStation(Section section) {
        return sections.stream()
                .filter(origin -> origin.hasBothMatchedStation(section))
                .findAny();
    }

    public List<Section> getValues() {
        return Collections.unmodifiableList(this.sections);
    }
}
