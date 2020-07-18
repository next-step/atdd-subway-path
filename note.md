# 노트

AS-IS
- PathFinder가 외부 라이브러리를 사용하는 어댑터
    - Line을 직접 의존하는 문제가 존재함
        - 그 때 그 때 만들고 성능의 문제가 거슬리면 캐싱하면 됨
- PathService에서 해결해야할 문제
    - 빠른 구현을 위해 편의 상 다른 도메인의 레포지토리를 직접 사용하도록 했음
    - DTO를 여기서 조립하고 있음
    
TO-BE
- PathFinder
    - LineResponse 리스트를 사용하는 형태로 변경
    - 성능의 문제는 내부적으로 캐시를 두어 해결
    - 스프링의 관리를 받는 컴포넌트로 등록해서 해결
- PathService
    - 레포지토리를 사용하지 않고 서비스를 의존하게 할 것
    - 아 ㅇㅅㅇ 타입이 추가됐네
- PathFinderResult
    - DTO 및 assembler 역할을 하는 친구 추가해보자.