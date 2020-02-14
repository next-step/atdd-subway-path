package atdd.station.service;

import atdd.station.entity.Line;
import atdd.station.repository.LineRepository;
import atdd.station.usecase.LineDTO;
import atdd.station.usecase.LineUsecase;
import atdd.station.usecase.ListWrapper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LineService implements LineUsecase {

  @Autowired
  LineRepository lineRepository;

  private final LineModelMapper lineModelMapper = new LineModelMapper();

  @Override
  public LineDTO addLine(LineDTO line) {
    return lineModelMapper.EntityToDTO(
        lineRepository.save(
            lineModelMapper.DTOToEntity(line)
        )
    );
  }

  @Override
  public ListWrapper<LineDTO> getLines() {
    Iterable<Line> lines = lineRepository.findAll();
    List<LineDTO> lineDTOS = StreamSupport
        .stream(lines.spliterator(), false)
        .map(
            line -> this.lineModelMapper.EntityToDTO(line)
        ).collect(Collectors.toList());
    return new ListWrapper<LineDTO>(lineDTOS);
  }

  @Override
  public LineDTO getLine(Long lineID) {
    return lineModelMapper.EntityToDTO(
        lineRepository.findById(lineID).get()
    );
  }
}
