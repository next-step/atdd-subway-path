package atdd.station.service;

import atdd.station.domain.Line;
import atdd.station.domain.Subway;
import atdd.station.dto.LineDto;
import atdd.station.repository.LineRepository;
import atdd.station.repository.StationRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("lineService")
public class LineService
{
    @Resource(name = "lineRepository")
    private LineRepository lineRepository;

    public Line create(LineDto lineDto)
    {
        List<Subway> subways = lineDto.getSubways();
        return lineRepository.save(lineDto.toEntity(lineDto.getName(), subways));
    }

    public List<Line> findLines()
    {
        List<Line> lines = lineRepository.findAll();
        return lines;
    }

    public Optional<Line> findById(long id)
    {
        return lineRepository.findById(id);
    }

    public void deleteLineById(long id)
    {
        lineRepository.deleteById(id);
    }
}
