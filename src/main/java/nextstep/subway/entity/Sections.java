package nextstep.subway.entity;

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
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return this.sections.stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        return this.sections.stream()
            .sorted()
            .flatMap(it -> Stream.of(it.getUpStation(), it.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if(!this.sections.isEmpty()) {
            verifyAddSection(section);

            updateMiddleSection(section);
        }

        this.sections.add(section);
    }

    private void verifyAddSection(Section section) {
        boolean existsUpStation = getStations().stream()
            .anyMatch(it -> it.equals(section.getUpStation()));
        boolean existsDownStation = getStations().stream()
            .anyMatch(it -> it.equals(section.getDownStation()));

        verifyConnectedSection(existsUpStation, existsDownStation);

        verifyDuplicationStation(existsUpStation, existsDownStation);
    }

    private void verifyConnectedSection(boolean existsUpStation, boolean existsDownStation) {
        if(!existsUpStation && !existsDownStation) {
            throw new IllegalArgumentException("연결할 수 없는 구간입니다.");
        }
    }

    private void verifyDuplicationStation(boolean existsUpStation, boolean existsDownStation) {
        if(existsUpStation && existsDownStation) {
            throw new IllegalArgumentException("중복된 역은 등록 불가합니다.");
        }
    }

    private void updateMiddleSection(Section section) {
        // 이후 구간
        Optional<Section> nextSection = getSections().stream()
            .filter(it -> it.getUpStation().equals(section.getUpStation()))
            .findAny();
        nextSection.ifPresent(
            it -> it.updateNextSection(section.getDownStation(), section.getDistance())
        );

        // 이전 구간
        Optional<Section> prevSection = getSections().stream()
            .filter(it -> it.getDownStation().equals(section.getDownStation()))
            .findAny();
        prevSection.ifPresent(
            it -> it.updatePrevSection(section.getUpStation(), section.getDistance())
        );
    }

    public void removeSection(Station station) {
        verifyRemoveSection(station);

        Optional<Section> prevSection = getSections().stream()
            .filter(it -> it.getDownStation().equals(station))
            .findAny();
        Optional<Section> nextSection = getSections().stream()
            .filter(it -> it.getUpStation().equals(station))
            .findAny();

        Section removeSection = null;
        if (prevSection.isEmpty() && nextSection.isPresent()) {
            removeSection = nextSection.get();
        }
        if (prevSection.isPresent() && nextSection.isEmpty()) {
            removeSection = prevSection.get();
        }
        if (prevSection.isPresent() && nextSection.isPresent()) {
            prevSection.get().updatePrevSection(
                nextSection.get().getDownStation(),
                prevSection.get().getDistance() + nextSection.get().getDistance()
            );
            removeSection = nextSection.get();
        }

        this.sections.remove(removeSection);
    }

    private void verifyRemoveSection(Station station) {
        verifyExistsSection(station);

        verifyIsOnlySection();
    }

    private void verifyExistsSection(Station station) {
        boolean existsSection = getStations().stream()
            .anyMatch(it -> it.equals(station));

        if(!existsSection) {
            throw new IllegalArgumentException("존재하는 구간만 삭제 가능하다.");
        }
    }

    private void verifyIsOnlySection() {
        boolean isOnlySection = this.sections.size() == 1;

        if(isOnlySection) {
            throw new IllegalArgumentException("유일한 구간은 삭제가 불가하다.");
        }
    }
}
