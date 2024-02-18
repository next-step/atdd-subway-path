package nextstep.subway.line.section;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.exception.HttpBadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    public static final int MINIMUM_SIZE = 1;

    @JsonManagedReference
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();


    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public boolean contains(Station station) {
        return sections.stream().anyMatch(section -> section.contains(station));
    }

    public void deleteSection(Section section) {
        if (sections.size() == MINIMUM_SIZE) {
            throw new HttpBadRequestException("노선에는 최소 한 개의 구간이 존재해야 합니다.");
        }

        if(!Objects.equals(sections.get(sections.size()-1), section)){
            throw new HttpBadRequestException("노선의 마지막 구간만 삭제할 수 있습니다.");
        }

        sections.remove(section);
    }

    public int size() {
        return sections.size();
    }

    public void clear() {
        sections.clear();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addStationBefore(Station newStation, Station nextStation, int distance, Line line) {
        mustHaveMoreThanOneSection();
        alreadyHaveStation(newStation);

        //새로운 구간 추가
        new Section(newStation, nextStation, distance, line);

        //기존 구간 수정
        Optional<Section> beforeSection = sections.stream().findFirst().filter(s -> s.getDownStation().equals(nextStation));
        if(beforeSection.isPresent()){
            Section section = beforeSection.get();
            section.setDownStation(newStation);
            section.setDistance(section.getDistance() - distance);
        }else{
           line.setStartStation(newStation);
        }
    }

    private void mustHaveMoreThanOneSection() {
        if (sections.isEmpty()) {
            throw new HttpBadRequestException("노선에는 최소 한 개의 구간이 존재해야 합니다.");
        }
    }

    private void alreadyHaveStation(Station newStation) {
        if (sections.stream().anyMatch(section -> section.contains(newStation))) {
            throw new HttpBadRequestException("이미 등록된 역입니다.");
        }
    }

    private void notExistStation(Station newStation) {
        if (sections.stream().noneMatch(section -> section.contains(newStation))) {
            throw new HttpBadRequestException("등록되지 않은 역입니다.");
        }
    }

    public void removeStation(Station targetStation, Line line) {
        notExistStation(targetStation);

        Optional<Section> beforeSectionOptional = sections.stream().filter(s -> s.getDownStation().equals(targetStation)).findFirst();
        Optional<Section> nextSectionsOptional = sections.stream().filter(s -> s.getUpStation().equals(targetStation)).findFirst();

        if(nextSectionsOptional.isPresent()){
            Section nextSection = nextSectionsOptional.get();
            if(beforeSectionOptional.isPresent() ){
                Section beforeSection = beforeSectionOptional.get();
                beforeSection.setDownStation(nextSection.getDownStation());
                beforeSection.setDistance(beforeSection.getDistance() + nextSection.getDistance());
            }else{
                line.setStartStation(nextSection.getDownStation());
            }
            sections.remove(nextSection);
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        sections.stream().forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return stations.stream().distinct().collect(Collectors.toList());
    }
}
