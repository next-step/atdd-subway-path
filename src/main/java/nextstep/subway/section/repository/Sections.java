package nextstep.subway.section.repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nextstep.subway.section.policy.AddSectionPolicy;
import nextstep.subway.section.policy.DeleteSectionPolicy;
import nextstep.subway.station.repository.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@AllArgsConstructor
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
}
