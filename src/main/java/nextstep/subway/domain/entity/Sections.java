package nextstep.subway.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
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
}
