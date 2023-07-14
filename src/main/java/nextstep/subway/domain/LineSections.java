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

    public void removeLastStation() {
        lineSections.remove(lineSections.size() - 1);
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

    public Station getDownStation() {
        return downStation;
    }
}
