package nextstep.subway.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void deleteSection(Section section) {
        this.sections.remove(section);
    }

    public List<Station> getStations() {
        List<Station> stations = this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(sections.get(sections.size() - 1).getDownStation()); //종착역 추가
        return stations;
    }

    public Section getLastSection() {
        int lastIndex = this.sections.size() - 1;
        return this.sections.get(lastIndex);
    }
}
