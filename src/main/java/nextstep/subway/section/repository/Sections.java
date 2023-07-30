package nextstep.subway.section.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.section.policy.AddSectionPolicy;
import nextstep.subway.section.policy.DeleteSectionPolicy;
import nextstep.subway.station.repository.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        AddSectionPolicy.validate(this, section);
        this.sections.add(section);
    }

    public void deleteSectionByLastStation(Station station) {
        DeleteSectionPolicy.validate(this, station);
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
        List<Station> totalStation = this.sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        totalStation.add(this.sections.get(this.sections.size() - 1).getDownStation());
        return Collections.unmodifiableList(totalStation);
    }

    public Sections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new RuntimeException("sections: at least one section is required");
        }
        this.sections = sections;
    }
}
