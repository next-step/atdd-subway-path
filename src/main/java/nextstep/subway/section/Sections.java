package nextstep.subway.section;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    private static boolean isMiddleSection(Optional<Section> downSection, Optional<Section> upSection) {
        return downSection.isPresent() && upSection.isPresent();
    }

    public void addSection(Section section) {
        if (sections.size() > 0) {
            validateDuplicateStation(section);
        }

        if (isMiddleSection(section)) {
            Section nextSection = getNextSection(section);
            nextSection.separate(section);
        }

        this.sections.add(section);
    }

    private void validateDuplicateStation(Section section) {
        if (isMatchUpStation(section.getDownStation()) && isMatchDownStation(section.getUpStation())) {
            throw new SubwayException("이미 등록되어있는 역입니다.");
        }
    }

    private boolean isMatchUpStation(Station station) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(station));
    }

    private boolean isMatchDownStation(Station station) {
        return this.sections.stream().anyMatch(section -> section.getDownStation().equals(station));
    }

    private Section getNextSection(Section section) {
        return this.sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new SubwayException("역을 찾을 수 없습니다."));
    }

    private boolean isMiddleSection(Section section) {
        return isMatchUpStation(section.getUpStation());
    }

    public void deleteSection(Station station) {
        validateLastSection();

        Optional<Section> downSection = findSectionByUpStation(station);
        Optional<Section> upSection = findSectionByDownStation(station);
        validatePresent(downSection, upSection);

        if (isMiddleSection(downSection, upSection)) {
            deleteMiddleSection(downSection.get(), upSection.get());
            return;
        }

        deleteSection(downSection);
        deleteSection(upSection);
    }

    private void validatePresent(Optional<Section> downSection, Optional<Section> upSection) {
        if (!downSection.isPresent() && !upSection.isPresent()) {
            throw new SubwayException("존재하지 않는 역입니다");
        }
    }

    private void deleteMiddleSection(Section downSection, Section upSection) {
        upSection.join(downSection);
        this.sections.remove(downSection);
    }

    private void deleteSection(Optional<Section> section) {
        if (section.isPresent()) {
            this.sections.remove(section.get());
        }
    }

    private void validateLastSection() {
        if (sections.size() < 2) {
            throw new SubwayException("구간이 1개인 경우 역을 삭제할 수 없습니다.");
        }
    }

    private Optional<Section> findSectionByDownStation(Station station) {
        return this.sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst();
    }

    private Optional<Section> findSectionByUpStation(Station station) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst();
    }

    public List<Station> getOrderedStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
