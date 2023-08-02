# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 1단계 - 지하철 구간 추가 기능 개선

1. 역 사이에 새로운 역을 등록할 경우

- **상행역 기준으로 추가**   
  기존 구간 A-C에 신규 구간 A-B를 추가하는 경우 A역을 기준으로 추가

- **하행역 기준으로 추가**  
  기존 구간 A-C에 신규 구간 B-C를 추가하는 경우 B역을 기준으로 추가

### 예외케이스

- 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

## 2단계 - 지하철 구간 제거 기능 개선

1. 구간 삭제에 대한 제약 사항 변경 구현
- 기존에는 마지막 역 삭제만 가능했는데 위치에 상관 없이 삭제가 가능하도록 수정
- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- 중간역이 제거될 경우 재배치를 함
- 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
- 거리는 두 구간의 거리의 합으로 정함

인수테스트 
1. 지하철 구간 삭제 
```
/**
 * Given 2개의 지하철 노선을 생성하고
 * When 가운데 지하철 역을 삭제하면
 * Then 지하철 노선은 하나의 구간을 가지고, 총 길이는 기존과 동일하다.
 */
```

2. 지하철 구간 삭제 불가능
```
/**
 * Given 1개의 지하철 노선을 생성하고
 * When 상행 종점역 지하철 역을 삭제하면
 * Then 지하철 역을 삭제 할 수 없고, section-1005 에러 코드를 반환한다.
 */
```

3. 지하철 구간에 존재하지 않는 역 삭제시도
```
/**
 * Given 2개의 지하철 노선을 생성하고
 * When 노선에 포함되지 않은 역을 삭제하면
 * Then 지하철 역을 삭제 할 수 없고, station-1001 에러 코드를 반환한다.
 */
```
