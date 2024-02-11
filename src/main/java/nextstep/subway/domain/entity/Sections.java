package nextstep.subway.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import nextstep.subway.exception.ApplicationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ExceptionMessage.*;

@Embeddable
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Sections {
    @JsonValue
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        // 추가하려는 구간의 상행역 하행역이 같으면
        if (newSection.getUpStation().equals(newSection.getDownStation())) {
            throw new ApplicationException(NEW_SECTION_VALIDATION_EXCEPTION.getMessage());
        }

        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }
        // 이미 등록된 구간인지
        if (isRegisteredSection(newSection)) {
            throw new ApplicationException(ALREADY_REGISTERED_SECTION_EXCEPTION.getMessage());
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
        checkValidation(newSection, basedSection);
        int newDistance = basedSection.getDistance() - newSection.getDistance();

        this.sections.remove(basedSection);
        this.sections.add(newSection);

        // 상행역이 같다면
        if (basedSection.isSameAsUpStation(newSection.getUpStation())) {
            this.sections.add(new Section(newSection.getDownStation(), basedSection.getDownStation(), newDistance));
            return;
        }
        // 하행역이 같다면
        if (basedSection.isSameAsDwonStation(newSection.getDownStation())) {
            this.sections.add(new Section(basedSection.getUpStation(), newSection.getUpStation(), newDistance));
        }
    }

    private void checkValidation(Section newSection, Section basedSection) {
        // 기존 구간보다 거리가 길거나 같은 노선인지
        if (newSection.getDistance() >= basedSection.getDistance()) {
            throw new ApplicationException(LONGER_DISTANCE_SECTION_EXCEPTION.getMessage());
        }
    }

    public void deleteSection(Section section) {
        this.sections.remove(section);
    }

    public List<Station> getStations() {
        // TODO flatMap
        List<List<Station>> list = sections.stream().map(section -> section.getStations()).collect(Collectors.toList());

        return list.stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

//        List<Station> stations = new ArrayList<>();
//        for (Section section : sections) {
//            stations.addAll(section.getStations());
//        }
//
//        return stations.stream().distinct().collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Section getLastSection() {
        return this.sections.stream()
                .filter(this::isLastSection)
                .findFirst()
                .orElseThrow(() -> new ApplicationException(NO_EXISTS_LAST_SECTION_EXCEIPTION.getMessage()));
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

    public boolean isRegisteredSection(Section section) {
        boolean isExistUpStation = this.getStations().stream()
                .anyMatch(station -> station.equals(section.getUpStation()));

        boolean isExistDownStation = this.getStations().stream()
                .anyMatch(station -> station.equals(section.getDownStation()));

        return isExistUpStation && isExistDownStation;
    }

    public Section findSectionByUpStationName(String name) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().getName().equals(name))
                .findFirst()
                .get();
    }

}
