package atdd.domain.stations;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity(name = "line")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String startTime="05:00";

    @Column(nullable = false)
    private String endTime="23:50";

    @Column(nullable = false)
    private String intervalTime="10";

    @OneToMany(mappedBy = "stations")
    private List<StationLine> stationLines=new ArrayList<>();

    @OneToMany(mappedBy = "line")
    private List<Section> sections=new ArrayList<>();

    public Line(String name){
        this.name=name;
    }

    @Builder
    public Line(String name, List<StationLine> stationLines) {
        this.name = name;
        this.stationLines = stationLines;
    }

    public List<Stations> getStations(){
        return this.stationLines
                .stream()
                .map(StationLine::getStations)
                .collect(Collectors.toList());
    }

    private Section head;
    private Section tail;
    private int size=0;

    public void addFirstSection(Stations source, Stations target){
        Section newSection=new Section(source, target);
        head=newSection;
        if(head != null){
            head.prev=newSection;
        }
        head=newSection;
        size++;
        if(head.next==null){
            tail=head;
        }
    }

    public void addLastSection(Stations source, Stations target){
        Section newSection=new Section(source, target);
        if(size==0){
            addFirstSection(source, target);
        }else{
            tail.next=newSection;
            newSection.prev=tail;
            tail=newSection;
            size++;
        }
    }

    public void removeStation(Stations station){
        station.deleteStation(station.getId());
        changeRelatedSectionAsSource(station);
        changeRelatedSectionAsTarget(station);
        size--;
    }

    public void changeRelatedSectionAsSource(Stations station){
        Section prevSection=station.getSectionAsSource().prev;
        if(prevSection != null){
            Stations newSourceStation=prevSection.getTarget();
            station.getSectionAsSource().deleteSource(station);
            station.getSectionAsSource().changeSource(newSourceStation);
        }
    }

    public void changeRelatedSectionAsTarget(Stations station){
        Section nextSection=station.getSectionAsTarget().next;
        if(nextSection != null){
            Stations newTargetStation=nextSection.getTarget();
            station.getSectionAsTarget().deleteTarget(station);
            station.getSectionAsTarget().changeTarget(newTargetStation);
        }
    }

    public boolean isSourceExist(Section section){
        if(section.getSource().isExist()==false){
            return false;
        }
        return true;
    }

    public boolean isTargetExist(Section section){
        if(section.getTarget().isExist()==false){
            return false;
        }
        return true;
    }
}
