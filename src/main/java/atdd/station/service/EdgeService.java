package atdd.station.service;

import atdd.station.domain.Edge;
import atdd.station.dto.EdgeDto;
import atdd.station.repository.EdgeRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("edgeService")
public class EdgeService
{
    @Resource(name = "edgeRepository")
    private EdgeRepository edgeRepository;

    public Edge create(EdgeDto edgeDto)
    {
        return edgeRepository.save(edgeDto.toEntity());
    }
}
