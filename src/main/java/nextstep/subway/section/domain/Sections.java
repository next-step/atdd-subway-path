package nextstep.subway.section.domain;

import nextstep.subway.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {

    private final static int FIRST = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
        }

        stations.add(getLastSection().getDownStation());

        return stations;
    }

    public void addSection(Section newSection) {
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        System.out.println("테스트");
        
        List<Station> stations = getStations();

        if (this.sections.contains(newSection)) {
            throw new AlreadyExistSectionException();
        }
        if (stations.contains(newSection.getUpStation()) || stations.contains(newSection.getDownStation())) {
            // 추가하는 구간의 상행역 혹은 하행역이 노선에 있지 않으면 생성 X
        }

        if (isFirstStation(newSection.getDownStation())) {
            addFirstSection(newSection);
            return;
        }

        // 추가하고자 하는 section의 upStation이
        // List<section> 의 요소의 downStation인 것이 있는지?
        // 그럼 중간/마지막 구간에 추가한다.
        // - 기존 구간 : 논현-신논현[0] - 신논현-강남[1]
        // - 기존 역 : 논현[0] - 신논현[1] - 강남[2]
        // - 추가 : 신논현 - 양재
        // - 예샹 결과 : 논현[0] - 신논현[1] - 양재[2] - 강남[3]
        if (isLastStation(newSection.getUpStation())) {
            addLastSection(newSection);
            return;
        }

        addMiddleSection(newSection);


//        if (isNotLastStation(section.getUpStation())) {
//            throw new IsNotLastStationException();
//        }
//        if (isExistDownStation(section)) {
//            throw new AlreadyExistDownStationException();
//        }
    }

    private void addFirstSection(Section newSection) {
        this.sections.get(FIRST).updateUpStation(newSection.getDownStation());
        this.sections.add(FIRST, newSection);

        System.out.println("this.sections.get(0) : " + this.sections.get(0).getUpStation().getName() + " / " + this.sections.get(0).getDownStation().getName());
        System.out.println("this.sections.get(1) : " + this.sections.get(1).getUpStation().getName() + " / " + this.sections.get(1).getDownStation().getName());
    }

    private void addLastSection(Section newSection) {
        this.sections.add(newSection);
    }

    private void addMiddleSection(Section newSection) {
        int index = getStations().indexOf(newSection.getUpStation());
        this.sections.get(index).updateUpStation(newSection.getDownStation());
        this.sections.add(index, newSection);

        System.out.println("this.sections.get(0) : " + this.sections.get(0).getUpStation().getName() + " / " + this.sections.get(0).getDownStation().getName());
        System.out.println("this.sections.get(1) : " + this.sections.get(1).getUpStation().getName() + " / " + this.sections.get(1).getDownStation().getName());
        System.out.println("this.sections.get(2) : " + this.sections.get(2).getUpStation().getName() + " / " + this.sections.get(2).getDownStation().getName());

    }

    public void deleteSection(Station station) {
        if (!getStations().contains(station)) {
            throw new NotFoundStationException();
        }
        if (isNotLastStation(station)) {
            throw new IsNotLastStationException();
        }
        if (size() == 1) {
            throw new DeleteSectionException();
        }

        Section lastSection = getLastSection();

        lastSection.delete();
        this.sections.remove(lastSection);
    }

    private boolean isFirstStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return sections.get(FIRST).isUpStation(station);
    }

    private boolean isLastStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return getLastSection().isDownStation(station);
    }

    private boolean isNotLastStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return !getLastSection().isDownStation(station);
    }

    private Section getLastSection() {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return this.sections.get(size() - 1);
    }

    private boolean isExistDownStation(Section section) {
        return getStations().stream()
                .anyMatch(comparedStation ->
                        comparedStation.equals(section.getDownStation())
                );
    }

    public boolean hasSection(Section section) {
        return this.sections.contains(section);
    }

    public int size() {
        return this.sections.size();
    }

    public List<Section> getSections() {
        return this.sections;
    }

}
