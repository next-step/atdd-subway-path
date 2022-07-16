package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }


    public void update(final String name, final String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }

    public boolean isLastDownStation(final Station station) {
        return getLastStation(getStations()).equals(station);
    }

    public Station getFirstStation(final List<Station> stations) {
        return stations.get(0);
    }

    public Station getLastStation(final List<Station> stations) {
        return stations.get(stations.size() - 1);
    }

    public void removeLastSection() {
        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        if (hasNoSection()) {
            return Collections.emptyList();
        }

        final Station firstUpStation = getFirstUpStation();
        final List<Station> stations = findAllDownStationsInOrder(firstUpStation);
        stations.add(0, firstUpStation);

        return Collections.unmodifiableList(stations);
    }

    public boolean hasNoSection() {
        return sections.isEmpty();
    }

    private Station getFirstUpStation() {
        final List<Station> allDownStations = getAllDownStations();
        return sections.stream()
                .filter(v -> !allDownStations.contains(v.getUpStation()))
                .findFirst()
                .map(Section::getUpStation)
                .orElseThrow(IllegalStateException::new);
    }

    private List<Station> getAllDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> findAllDownStationsInOrder(final Station firstUpStation) {
        final List<Station> stationList = new ArrayList<>();

        Station target = firstUpStation;
        Queue<Section> sectionQueue = new LinkedList<>(sections);
        while (!sectionQueue.isEmpty()) {
            final Section section = sectionQueue.poll();

            // 마지막 구간은 이어지는 곳이 없으므로, downStation을 더하고 break;
            if (sectionQueue.isEmpty()) {
                stationList.add(section.getDownStation());
                break;
            }

            final Station connectedDownStation = findConnectedDownStation(target, section);
            if (connectedDownStation != null) {
                stationList.add(connectedDownStation);
                target = connectedDownStation;
            } else {
                sectionQueue.offer(section);
            }
        }

        return stationList;
    }

    private Station findConnectedDownStation(Station target, final Section section) {
        // 이어지는 구간인 경우 downStation을 추가하고, target을 변경해줌
        if (section.getUpStation().equals(target)) {
            return section.getDownStation();
        }

        return null;
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        addSection(new Section(this, upStation, downStation, distance));
    }

    public void addSection(final Section section) {
        sections.add(section);
    }

    public void addSection(final int index, final Section section) {
        sections.add(index, section);
    }

}
