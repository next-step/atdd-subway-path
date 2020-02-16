package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.domain.TimeTable;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import atdd.line.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineAssembler lineAssembler;
    private final LineRepository lineRepository;

    public LineService(LineAssembler lineAssembler, LineRepository lineRepository) {
        this.lineAssembler = lineAssembler;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponseDto create(LineCreateRequestDto requestDto) {
        final TimeTable timeTable = new TimeTable(requestDto.getStartTime(), requestDto.getEndTime());
        final Line line = Line.create(requestDto.getName(), timeTable, requestDto.getIntervalTime());

        final Line saved = lineRepository.save(line);

        return lineAssembler.convertToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<LineResponseDto> findAll(String name) {
        return lineRepository.findAll().stream()
                .filter(line -> isSameName(line, name))
                .map(lineAssembler::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private boolean isSameName(Line line, String name) {
        if (!StringUtils.hasText(name)) {
            return true;
        }
        return line.isSameName(name);
    }

    @Transactional
    public void delete(Long lineId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 lineId 입니다. lineId : [" + lineId + "]"));

        lineRepository.delete(line);
    }

}
