# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 실습 - 단위 테스트 작성

- [x] 지하철 구간 관련 단위 테스트를 완성하세요. 
  - [x] 구간 단위 테스트 (LineTest)
  - [x] 구간 서비스 단위 테스트 with Mock (LineAddSectionServiceMockTest)
  - [x] 구간 서비스 단위 테스트 without Mock (LineAddSectionServiceTest)
- [x] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링 하세요.

<br>

## 1단계 - 구간 추가 요구사항 반영

### 기능 요구사항(완료 조건)

- [ ] 노선에 역 추가시 노선 가운데 추가 할 수 있다.
  - [ ] 가운데 추가되는 구간의 길이는 기존 구간의 길이보다 짧아야 한다.
- [x] 노선에 역 추가시 노선 처음에 추가 할 수 있다.
- [x] 이미 등록되어있는 역은 노선에 등록될 수 없다.
