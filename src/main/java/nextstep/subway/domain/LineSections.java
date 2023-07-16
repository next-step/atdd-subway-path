package nextstep.subway.domain;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class LineSections {
    public LineSections() {

    }

    public LineSections(Line line) {
        this.line = line;
    }

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Station downStation;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> lineSections = new ArrayList<>();

    public void addSection(Section section) {
        if (lineSections.isEmpty()) {
            initializeStations(section);
        } else {
            updateStations(section);
        }

        lineSections.add(section);
    }

    private void initializeStations(Section section) {
        upStation = section.getUpStation();
        downStation = section.getDownStation();
    }

    private void updateStations(Section section) {
        validateSection(section);

        if (upStationExistUpStation(section)) {
            return;
        }

        if (upStation.equals(section.getDownStation())) {
            upStation = section.getUpStation();
            return;
        }

        if (downStation.equals(section.getUpStation())) {
            downStation = section.getDownStation();
        }
    }

    private void validateSection(Section section) {
        if (section.equalsUpStation(upStation) && section.equalsDownStation(downStation)) {
            throw new DataIntegrityViolationException("상행역과 하행역이 이미 등록된 구간입니다.");
        }

        if (!isValidSection(section)) {
            throw new DataIntegrityViolationException("잘못된 지하철 구간 등록입니다.");
        }
    }

    private boolean isValidSection(Section section) {
        boolean hasSameUpStation = lineSections.stream()
                .anyMatch(s -> s.getUpStation().equals(section.getUpStation()));

        return hasSameUpStation ||
                upStation.equals(section.getDownStation()) ||
                downStation.equals(section.getUpStation());
    }

    private boolean upStationExistUpStation(Section section) {
        for (Section existSection : lineSections) {
            if (existSection.equalsUpStation(section.getUpStation())) {
                validateExistUpStation(existSection, section);

                lineSections.add(new Section(line, section.getDownStation(), existSection.getDownStation()
                        , existSection.getDistance() - section.getDistance()));
                lineSections.remove(existSection);
                return true;
            }
        }
        return false;
    }

    private void validateExistUpStation(Section existSection, Section section) {
        if (section.isGreaterOrEqualDistance(existSection)) {
            throw new DataIntegrityViolationException("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }

        if (section.equalsUpStationAndDownStation(existSection)) {
            throw new DataIntegrityViolationException("상행역과 하행역이 이미 등록된 구간입니다.");
        }
    }

    public List<Station> getStations() {
        Section currentSection = getUpStationSection();

        List<Section> list = new ArrayList<>();
        list.add(currentSection);

        while (list.size() < lineSections.size()) {
            for (Section section : lineSections) {
                if (section.getUpStation().equals(currentSection.getDownStation())) {
                    list.add(section);
                    currentSection = section;
                }
            }
        }

        List<Station> stations = list.stream().map(Section::getDownStation).collect(Collectors.toList());
        stations.add(0, list.get(0).getUpStation());

        return stations;
    }

    private Section getUpStationSection() {
        return lineSections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElse(null);
    }

    public boolean isEmpty() {
        return lineSections.isEmpty();
    }

    public int size() {
        return lineSections.size();
    }

    public void removeStation(Station removeStation) {
        validateRemoveStation(removeStation);

        if (!hasDownStationSection(removeStation)) {
            removeFirstSection(removeStation);
            return;
        }

        if (!hasUpStationSection(removeStation)) {
            removeLastSection(removeStation);
            return;
        }

        if (hasUpStationSection(removeStation) && hasDownStationSection(removeStation)) {
            removeMiddleSection(removeStation);
            return;
        }

        throw new DataIntegrityViolationException("올바르지 않은 구간 삭제 입니다.");
    }

    private void removeMiddleSection(Station removeStation) {
        Section frontSection = getSectionWithSameDownStation(removeStation);
        Section rearSection = getSectionWithSameUpStation(removeStation);

        Section mergeSection = new Section(line
                , frontSection.getUpStation()
                , rearSection.getDownStation()
                , frontSection.getDistance() + rearSection.getDistance());

        lineSections.remove(frontSection);
        lineSections.remove(rearSection);
        lineSections.add(mergeSection);
    }

    private Section getSectionWithSameDownStation(Station removeStation) {
        return lineSections.stream()
                .filter(section -> section.getDownStation().equals(removeStation))
                .findFirst()
                .orElseThrow();
    }

    private void removeFirstSection(Station removeStation) {
        Section firstSection = getSectionWithSameUpStation(removeStation);
        upStation = firstSection.getDownStation();
        lineSections.remove(firstSection);
    }

    private Section getSectionWithSameUpStation(Station station) {
        return lineSections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst().orElseThrow();
    }

    private void removeLastSection(Station removeStation) {
        Section lastSection = getSectionWithSameDownStation(removeStation);
        downStation = lastSection.getUpStation();
        lineSections.remove(lastSection);
    }

    private boolean hasUpStationSection(Station station) {
        return lineSections.stream()
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    private boolean hasDownStationSection(Station station) {
        return lineSections.stream()
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    private void validateRemoveStation(Station removeStation) {
        if (lineSections.size() == 1) {
            throw new DataIntegrityViolationException("지하철 노선에 구간이 한개인 경우 삭제할 수 없습니다.");
        }

        if (!lineSections.stream().anyMatch(section ->
                section.getUpStation().equals(removeStation) || section.getDownStation().equals(removeStation))) {
            throw new DataIntegrityViolationException("지하철 노선에 등록되어 있지 않은 역입니다.");
        }
    }

    public int getLineSectionsTotalDistance() {
        return lineSections.stream().mapToInt(Section::getDistance).sum();
    }
}
