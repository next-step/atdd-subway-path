package nextstep.subway.section;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void updateLine(Line line) {
        this.sections.get(0).updateLine(line);
    }

    public void add(final Line line, final Station upStation, final Station downStation,
        final int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        sections.add(section);
    }

    public void validate(final Station upStation, final Station downStation) {
        if (isAlreadyRegisteredDownStation(downStation)) {
            throw new BusinessException(
                "이미 등록된 역을 하행역으로 가지면 구간을 추가할 수 없습니다. 하행역ID: " + downStation.getId());
        }

        if (!getLastSection().getDownStation().equals(upStation)) {
            throw new BusinessException(
                "하행 종점이 아닌 역을 상행역으로 가지면 구간을 추가할 수 없습니다. 상행역ID: " + upStation.getId());
        }
    }

    public void delete(final Station station) {
        if (!getLastSection().getDownStation().equals(station)) {
            throw new BusinessException("하행 종점이 아닌 역을 삭제할 수 없습니다. 역ID: " + station.getId());
        }

        if (sections.size() == 1) {
            throw new BusinessException("상행 종점역과 하행 종점역만 있는 노선의 구간은 삭제할 수 없습니다.");
        }

        sections.remove(getLastSection());
    }

    public List<Station> getStations() {
        return toStations();
    }

    public List<Section> getSections() {
        return this.sections;
    }

    private List<Station> toStations() {
        return Stream.concat(Stream.of(this.sections.get(0).getUpStation()),
                this.sections.stream().map(Section::getDownStation))
            .collect(Collectors.toList());
    }

    private boolean isAlreadyRegisteredDownStation(final Station downStation) {
        if (isAlreadyRegisteredInFirstUpStation(downStation)) {
            return true;
        }

        return this.sections.stream()
            .anyMatch(localSection -> localSection.getDownStation().equals(downStation));
    }

    private boolean isAlreadyRegisteredInFirstUpStation(final Station downStation) {
        return this.sections.stream().findFirst().orElseThrow()
            .getUpStation().equals(downStation);
    }

    public Section getLastSection() {
        return this.sections.get(this.sections.size() - 1);
    }
}
