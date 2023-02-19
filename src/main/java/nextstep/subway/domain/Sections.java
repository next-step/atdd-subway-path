package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (isEmptySections()) {
            this.sections.add(new Section(line, upStation, downStation, distance));
            return;
        }

        for (int i = 0; i < this.sections.size(); i++) {
            validateSameUpDownStation(upStation, downStation, sections.get(i));

            if (Objects.equals(sections.get(i).getUpStation(), upStation)) {
                Station currentUpStation = sections.get(i).getUpStation();
                Station currentDownStation = sections.get(i).getDownStation();
                int newSectionDistance = getNewSectionDistance(distance, sections.get(i));
                sections.get(i).modify(line, currentUpStation, downStation, distance);
                this.sections.add(i + 1, new Section(line, downStation, currentDownStation, newSectionDistance));
                return;
            }

            if (Objects.equals(sections.get(i).getUpStation(), downStation)) {
                this.sections.add(i, new Section(line, upStation, downStation, distance));
                return;
            }

            if (Objects.equals(sections.get(i).getDownStation(), upStation)) {
                this.sections.add(i + 1, new Section(line, upStation, downStation, distance));
                return;
            }
        }
    }

    private boolean isEmptySections() {
        return getSections() == null || getSections().isEmpty();
    }

    private int getNewSectionDistance(int distance, Section section) {
        validateDistance(distance, section);
        return section.getDistance() - distance;
    }

    private void validateDistance(int distance, Section section) {
        if (section.getDistance() <= distance) {
            throw new AddSectionException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    private void validateSameUpDownStation(Station upStation, Station downStation, Section section) {
        if (Objects.equals(section.getUpStation().getId(), upStation.getId()) && Objects.equals(section.getDownStation().getId(), downStation.getId())) {
            throw new AddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }
}
