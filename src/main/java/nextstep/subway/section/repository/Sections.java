package nextstep.subway.section.repository;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.policy.add.AddSectionPolicy;
import nextstep.subway.section.policy.delete.DeleteSectionPolicy;
import nextstep.subway.station.repository.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    @Getter
    private Long firstStationId;

    @Getter
    private Long lastStationId;

    public void addSection(Section section, AddSectionPolicy policy) {
        policy.validate(this, section);
        Optional<Station> upStation = getAllStation().stream()
                .filter(station -> Objects.equals(station, section.getUpStation()))
                .findFirst();
        Optional<Station> downStation = getAllStation().stream()
                .filter(station -> Objects.equals(station, section.getDownStation()))
                .findFirst();

        if (upStation.isPresent() && upStation.get().getId().equals(lastStationId)) {
            lastStationId = section.getDownStation().getId();
        }
        if (downStation.isPresent() && downStation.get().getId().equals(firstStationId)) {
            firstStationId = section.getUpStation().getId();
        }

        this.sections.add(section);
    }

    public void deleteSectionByLastStation(Station station) {
        DeleteSectionPolicy.validate(this, station);
        this.lastStationId = getLastSection().getUpStation().getId();
        this.sections.remove(getLastSection());
    }

    public int size() {
        return this.sections.size();
    }

    public Section getLastSection() {
        return this.sections.get(this.sections.size() - 1);
    }

    public Optional<Section> getSectionByUpStation(Station upStation) {
        return this.sections.stream().filter(section -> Objects.equals(section.getUpStation(), upStation)).findFirst();
    }

    public Optional<Section> getSectionByDownStation(Station downStation) {
        return this.sections.stream().filter(section -> Objects.equals(section.getDownStation(), downStation)).findFirst();
    }

    public Station getDownEndStation() {
        return this.sections.get(this.sections.size() - 1).getDownStation();
    }

    public Long getTotalDistance() {
        return this.sections.stream().mapToLong(Section::getDistance).sum();
    }

    public List<Station> getAllStation() {
        List<Station> result = new ArrayList<>();
        Long targetStationId = firstStationId;

        while (targetStationId != null && !targetStationId.equals(lastStationId)) {
            for (Section section : sections) {
                if (Objects.equals(section.getUpStation().getId(), targetStationId)) {
                    result.add(section.getUpStation());
                    targetStationId = section.getDownStation().getId();
                }
                if (targetStationId.equals(lastStationId)) {
                    result.add(section.getDownStation());
                    break;
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    public Sections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new RuntimeException("sections: at least one section is required");
        }
        this.sections = sections;
        this.firstStationId = sections.get(0).getUpStation().getId();
        this.lastStationId = sections.get(sections.size() - 1).getDownStation().getId();
    }


}
