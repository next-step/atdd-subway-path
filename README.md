# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

**Step1 단위 테스트 작성**
- 작업 순서
    1. 실제 존재하는 로직에 대한 단위테스트 작성
       - LineServiceTest : addSection(), deleteSection() 등
       - Line : getSection() 등
       - LineServiceMockTest: addSection() 등 
    2. 리팩토링 대상에 대한 단위테스트 작성 
       - Line : addSection(), deleteSection() 등
    3. 리팩토링 진행
       - Line : addSection(), deleteSection() 등 
    
**Step2 지하철 구간 추가 리팩터링**
- 작업 순서
    1. 변경된 스펙에 대한 성공 시나리오 인수 테스트 작성
    2. 개별 기능 단위 테스트와 함께 변경 시나리오 구현
    3. 변경된 스펙 예외 케이스에 대한 인수 테스트(or 단위 테스트) 작성
    4. 예외 케이스 구현
    