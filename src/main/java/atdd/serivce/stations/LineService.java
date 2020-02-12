package atdd.serivce.stations;

import atdd.domain.stations.Line;
import atdd.domain.stations.LineRepository;
import atdd.web.dto.line.LineCreateRequestDto;
import atdd.web.dto.line.LineListResponseDto;
import atdd.web.dto.line.LineResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LineService {
    private final LineRepository lineRepository;

    public Line create(LineCreateRequestDto requestDto){
        return lineRepository.save(requestDto.toEntity());
    }

    public void delete(Long id){
        Line line=checkId(id);
        lineRepository.delete(line);
    }

    public List<Line> readList() {
        return lineRepository.findAll();
    }

    public LineResponseDto read(Long id) {
        Line line =checkId(id);
        LineResponseDto dto=new LineResponseDto();
        return dto.toRealDto(line);
    }

    public Line checkId(Long id){
        return lineRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("해당 노선이 존재하지 않습니다."));
    }
}
