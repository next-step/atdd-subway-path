package atdd.station.domain;

import atdd.station.utils.SubwaySectionQueueUtils;

import javax.persistence.*;
import java.util.*;

@Entity
public class SubwayLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SUBWAY_LINE_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String firstSubwayTime;
    private String lastSubwayTime;

    private Integer dispatchInterval;

    @OneToMany(mappedBy = "subwayLine")
    private List<SubwaySection> subwaySections;

    public SubwayLine() {
    }

    private SubwayLine(String name) {
        this.name = name;
    }

    public static SubwayLine of(String name) {
        return new SubwayLine(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SubwaySection> getSubwaySections() {
        return subwaySections;
    }

    public List<Station> getStations() {
        return connectStations(subwaySections);
    }

    public void setSubwaySections(List<SubwaySection> subwaySections) {
        this.subwaySections = subwaySections;
    }

    private List<Station> connectStations(List<SubwaySection> subwaySections) {
        Deque<SubwaySection> orderedSectionsQueue = new ArrayDeque<>();
        Deque<SubwaySection> waitingQueue = new ArrayDeque<>(subwaySections);

        if (waitingQueue.isEmpty()) {
            return Collections.emptyList();
        }
        orderedSectionsQueue.push(waitingQueue.pollFirst());

        while (!waitingQueue.isEmpty()) {
            SubwaySection waitingQueueSection = waitingQueue.pollFirst();

            SubwaySectionQueueUtils.pushSectionIfConditionCorrect(orderedSectionsQueue
                    , waitingQueue, waitingQueueSection, orderedSectionsQueue.getFirst());

            waitingQueueSection = waitingQueue.pollFirst();

            if (Objects.isNull(waitingQueueSection)) {
                break;
            }

            SubwaySectionQueueUtils.pushSectionIfConditionCorrect(orderedSectionsQueue
                    , waitingQueue, waitingQueueSection, orderedSectionsQueue.getLast());

        }

        Set<Station> stations = new LinkedHashSet<>();
        orderedSectionsQueue.forEach(section -> {
            stations.add(section.getSourceStation());
            stations.add(section.getTargetStation());
        });

        return new ArrayList<>(stations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwayLine that = (SubwayLine) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(subwaySections, that.subwaySections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subwaySections);
    }

}
