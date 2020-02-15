package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.domain.TimeTable;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import atdd.line.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
