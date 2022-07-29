package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {
    @Transient
    private static final int ONE = 1;
    @Transient
    private static final int ZERO = 0;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        final List<Section> list = new LinkedList<>();
        final Station downTerminal = downTerminal();
        Station upStation = upTerminal();
        Section findSection;

        do {
            findSection = findSection(upStation);
            upStation = findSection.getDownStation();
            list.add(findSection);
        } while (!isMatchStation(findSection.getDownStation(), downTerminal));

        return new ArrayList<>(list);
    }

    private Section findSection(Station upStation) {
        return sections.stream().filter(section -> isMatchStation(section.getUpStation(), upStation)).findFirst().orElseThrow(() -> new IllegalStateException("해당 구간을 찾을 수 없습니다."));
    }

    public boolean addSection(Section section) {
        if (isMatchStation(section.getDownStation(), upTerminal())) {
            return addUpSection(section);
        }
        if (isMatchStation(section.getUpStation(), downTerminal())) {
            return addDownSection(section);
        }
        return addMiddleSection(section);
    }

    public boolean addUpSection(Section section) {
        if (isMatchStation(section.getUpStation())) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }

        if (!isMatchStation(section.getDownStation(), upTerminal())) {
            throw new IllegalArgumentException("등록하려는 구간의 하행역이 상행 종점역과 맞지 않습니다.");
        }

        return sections.add(section);
    }

    public boolean addDownSection(Section section) {
        if (isMatchStation(section.getDownStation())) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }

        if (!isMatchStation(section.getUpStation(), downTerminal())) {
            throw new IllegalArgumentException("등록하려는 구간의 상행역이 하행 종점역과 일치하지 않습니다.");
        }

        return sections.add(section);
    }

    public boolean addMiddleSection(Section section) {
        boolean matchDownSection = isMatchStation(section.getDownStation());
        boolean matchUpSection = isMatchStation(section.getUpStation());

        if (matchDownSection == matchUpSection) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }

        Section findSection = matchDownSection ? findMatchDownStation(section.getDownStation()) : findMatchUpStation(section.getUpStation());
        sections.remove(findSection);
        sections.add(new Section(section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance()));

        if (matchDownSection) {
            sections.add(new Section(section.getLine(), findSection.getUpStation(), section.getUpStation(), findSection.getDistance() - section.getDistance()));
            return true;
        }

        sections.add(new Section(section.getLine(), section.getDownStation(), findSection.getDownStation(), findSection.getDistance() - section.getDistance()));
        return true;
    }

    public void deleteSection(Station station) {
        if (!isMatchStation(station)) {
            throw new IllegalArgumentException("역이 존재하지 않습니다.");
        }

        if (sections.size() < 2) {
            throw new IllegalArgumentException("하나 남은 노선은 삭제할 수 없습니다.");
        }

        if (station.equals(upTerminal())) {
            sections.remove(ZERO);
            return;
        }

        if (station.equals(downTerminal())) {
            sections.remove(sections.size() - ONE);
            return;
        }

        Section upSection = findMatchDownStation(station);
        Section downSection = findMatchUpStation(station);

        sections.add(new Section(downSection.getLine(), upSection.getUpStation(), downSection.getDownStation(), upSection.getDistance() + downSection.getDistance()));
        sections.remove(upSection);
        sections.remove(downSection);
    }

    private Section findMatchUpStation(Station station) {
        return sections.stream().filter(section -> isMatchStation(section.getUpStation(), station)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Section findMatchDownStation(Station station) {
        return sections.stream().filter(section -> isMatchStation(section.getDownStation(), station)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Station upTerminal() {
        ArrayList<Station> upStations = new ArrayList<>();
        ArrayList<Station> downStations = new ArrayList<>();

        sections.forEach(section -> {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        });

        upStations.removeAll(downStations);

        if (upStations.size() != ONE) {
            throw new IllegalArgumentException("상행 종점역을 찾을 수 없습니다.");
        }
        return upStations.get(ZERO);
    }

    private Station downTerminal() {
        ArrayList<Station> upStations = new ArrayList<>();
        ArrayList<Station> downStations = new ArrayList<>();

        for (Section section : sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }

        downStations.removeAll(upStations);

        if (downStations.size() != ONE) {
            throw new IllegalArgumentException("하행 종점역을 찾을 수 없습니다.");
        }
        return downStations.get(ZERO);
    }

    private boolean isMatchStation(Station station) {
        return sections.stream()
            .anyMatch(findSection -> isMatchStation(findSection.getUpStation(), station)
                || isMatchStation(findSection.getDownStation(), station));
    }

    private boolean isMatchStation(Station station, Station o) {
        return station.equals(o);
    }
}
