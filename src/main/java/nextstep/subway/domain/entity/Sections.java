package nextstep.subway.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import nextstep.subway.exception.ApplicationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ExceptionMessage.NO_EXISTS_SAME_DOWNSTATION_SECTION;
import static nextstep.subway.exception.ExceptionMessage.NO_EXISTS_SAME_UPSTATION_SECTION;

@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        // 새로운 구간과 동일한 상행역이 있는지
        Station upStation = newSection.getUpStation();
        if (this.sections.stream().anyMatch(section -> section.getUpStation().equals(upStation))) {
            insertSection(newSection, getSameUpStationSection(upStation));
            return;
        }
        // 새로운 구간과 동일한 하행역이 있는지
        Station downStation = newSection.getDownStation();
        if (sections.stream().anyMatch(section -> section.getDownStation().equals(downStation))) {
            insertSection(newSection, getSameDownStationSection(downStation));
            return;
        }
        sections.add(newSection);
    }

    private void insertSection(Section newSection, Section basedSection) {
        int newDistance = basedSection.getDistance() - newSection.getDistance();

        this.sections.remove(basedSection);
        this.sections.add(newSection);

        // 상행역이 같다면
        if (basedSection.isSameAsUpStation(newSection.getUpStation())) {
            this.sections.add(new Section(newSection.getDownStation(), basedSection.getDownStation(), newDistance));
            return;
        }
        // 하행역이 같다면
        if (basedSection.isSameAsDwonStation(newSection.getDownStation())){
            this.sections.add(new Section(basedSection.getUpStation(), newSection.getUpStation(), newDistance));
        }
    }

    public void deleteSection(Section section) {
        this.sections.remove(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.addAll(section.getStations());
        }

        return stations.stream().distinct().collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    public Section getLastSection() {
        if (this.sections.isEmpty()) {
            return null;
        }
        return this.sections.get(this.sections.size() - 1);
    }

    public int getSize() {
        return this.sections.size();
    }

    private Section getSameUpStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameAsUpStation(station))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(NO_EXISTS_SAME_UPSTATION_SECTION.getMessage()));
    }

    private Section getSameDownStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameAsDwonStation(station))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(NO_EXISTS_SAME_DOWNSTATION_SECTION.getMessage()));
    }

    private boolean isFirstSection(Section basedSection) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(basedSection.getUpStation()));
    }

    private boolean isLastSection(Section basedSection) {
        return sections.stream()
                .noneMatch(section -> section.getUpStation().equals(basedSection.getDownStation()));
    }

    public int getDistance() {
        int sumOfDistance = 0;
        for (Section section : this.sections) {
            sumOfDistance += section.getDistance();
        }
        return sumOfDistance;
    }
}
