package atdd.station.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwaySectionRepository extends JpaRepository<SubwaySection, Long> {
    List<SubwaySection> findAllBySourceStationOrTargetStation(Station sourceStation,
                                                              Station targetStation);

    List<SubwaySection> findAllBySubwayLineAndSourceStationOrTargetStation(SubwayLine subwayLine,
                                                                           Station sourceStation,
                                                                           Station targetStation);

    List<SubwaySection> findBySubwayLine(SubwayLine subwayLine);

    void deleteAllBySourceStationOrTargetStation(Station sourceStation,
                                                 Station targetStation);
}
