package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import atdd.station.dto.subwayLine.SubwayLineCreateRequestDto;
import atdd.station.dto.subwayLine.SubwayLineCreateResponseDto;
import atdd.station.dto.subwayLine.SubwayLineDetailResponseDto;
import atdd.station.dto.subwayLine.SubwayLineListResponseDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service("subwayLineService")
public class SubwayLineService {
    @Resource(name = "subwayLineRepository")
    private SubwayLineRepository subwayLineRepository;

    @Transactional
    public SubwayLineCreateResponseDto create(SubwayLineCreateRequestDto subwayLine) {
        return SubwayLineCreateResponseDto.toDtoEntity(subwayLineRepository.save(subwayLine.toEntity()));
    }

    public SubwayLineListResponseDto list() {
        return SubwayLineListResponseDto.toDtoEntity(subwayLineRepository.findAll());
    }

    public SubwayLine findById(long id) {
        return subwayLineRepository.findById(id).orElseThrow(IllegalAccessError::new);
    }

    public SubwayLineDetailResponseDto detail(long id) {
        return SubwayLineDetailResponseDto.toDtoEntity(findById(id));
    }

    @Transactional
    public void delete(long defaultId) {
        findById(defaultId).deleteSubwayLine();
    }

    @Transactional
    public SubwayLine update(long id, List<Station> stations) {
        SubwayLine subwayLine = findById(id);
        SubwayLine updatedSubwayLine = subwayLine.updateSubwayByStations(stations);
        return subwayLineRepository.save(updatedSubwayLine);
    }

    public void deleteStation(long defaultId, String name) {
        SubwayLine subwayLine = findById(defaultId);
        subwayLine.deleteStationByName(name);
    }
}
