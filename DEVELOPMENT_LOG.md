## 📅 Development Log (시스템 설계 및 디버깅 기록)

### 📂 [2026-04-07] Phase 1: 시스템 기초 설계 및 도메인 모델링
> **"도메인 정립 및 레이어드 아키텍처 구축"**

* **Core Domain Modeling**
    * `Member`, `Post`, `Comment` 엔티티 설계를 통해 서비스 핵심 객체 간 연관관계 정의.
    * **Mapping Strategy**: `1:N` 관계 설정 및 성능 최적화를 위한 **지연 로딩(LAZY)** 및 양방향 매핑 전략 채택.
* **Persistence Layer**
    * `Spring Data JPA` 도입으로 반복적인 SQL 작성 지양 및 객체 중심 데이터 접근 로직 구축.
* **Architecture Strategy (Layer-based Packaging)**
    * **의사결정**: 도메인별 패키지 분리가 아닌 **기능별 레이어 분할(Entity, Service, Repository)** 방식 채택.
    * **Rationale**: 관리 테이블 수가 적은 현재 규모에서는 레이어별 응집이 파일 탐색 효율과 전체 가독성 면에서 유리하다고 판단 (Over-engineering 방지).

---

### 📂 [2026-04-08] Phase 2: 회원 시스템 기초 골격 구축
> **"로그인, 회원가입 구현"**

* **Domain Logic Foundation**
    * 회원 정보 저장을 위한 `Member` 엔티티 및 중복 검증 로직을 포함한 `MemberService` 뼈대 구현.
* **Web-Service Connection**
    * `joinForm.html` → `MemberController` → `MemberService`로 이어지는 **데이터 흐름(Data Flow)** 설계.
    * DB 존재 여부를 확인하는 기초적인 로그인 인증 프로세스 구축.
* **DTO(Data Transfer Object) 도입**
    * 컨트롤러-클라이언트 간 데이터 전송 전용 통로인 `JoinDTO`, `LoginDTO` 생성.
    * 비즈니스 엔티티를 보호하고 계층 간 데이터를 명확히 규정하는 설계적 기틀 마련.

---

### 📂 [2026-04-09] Phase 3: 세션 기반 인증 및 시스템 디버깅
> **"로그인 인증 구현""**

* **Advanced Design Decisions**
    * **Strict Separation**: 보안성 확보를 위해 View 계층에 엔티티 노출을 전면 차단하고 DTO 활용 강제.
    * **Session Optimization**: 비밀번호가 제거된 최소 정보만 담는 **`SessionDTO`**를 도입하여 서버 메모리 효율 및 보안성 확보.
* **System Debugging & Troubleshooting**
    * **Case 1 (View Rendering)**: 순수 HTML 사용 시 타임리프 문법 미작동 확인 → `HomeController`를 통한 중앙 집중식 렌더링으로 시스템 관문 일원화.
    * **Case 2 (Routing)**: 로그인 후 세션 정보 누락 현상 발생 → `redirect:/`를 강제하여 `HomeController`의 인증 판정 로직을 반드시 거치도록 라우팅 시스템 정렬.
    * **Case 3 (Authentication)**: 로그아웃 기능 미비 확인 → `@PostMapping` 기반의 세션 무효화(`invalidate`) 로직을 구현하여 인증 주기(Life Cycle) 완성.

---

### 📂 [2026-04-10] Phase 4: 게시글 시스템 기초 골격 구축
> **"게시글 등록 및 열람 기능 구현"**

* **Core Implementation**
    * **Post Registration**: `PostSaveDTO`를 통한 데이터 캡슐화 및 `PostService` 내 정적 팩토리 메서드(`createPost`)를 활용한 객체 생성 로직 구축.
    * **Detail View**: `PathVariable` 기반의 단건 조회 시스템 및 `EntityNotFoundException`을 활용한 예외 처리 정렬.
    * **Ordered List**: `Spring Data JPA` 메서드 쿼리를 활용하여 최신순(Desc) 정렬 조회 로직 완성.
* **System Debugging & Troubleshooting**
    * **Case 1 (N+1 Problem)**: 상세 페이지 조회 시 연관된 `Member` 엔티티 호출로 인한 추가 쿼리 병목 지점을 로그로 확인 → **Fetch Join** 도입 필요성 도출.
    * **Case 2 (Security Gap)**: UI 레벨의 버튼 은닉은 1차 방어일 뿐, URL 직접 접근을 방어하기 위한 **Service Layer의 이중 검증**이 필수적임을 인지.
* **Architectural Insight**
    * **Immutable DTO**: `SessionDTO`를 **불변 객체(Immutable Object)**로 리팩토링.
        * 시스템 권한 검증의 기준이 되는 세션 데이터의 임의 변경을 원천 차단하여 **데이터 무결성** 확보.
        * 상세 페이지 권한 대조 시 `post.member.id`와 비교되는 기준 데이터를 '읽기 전용'으로 엄격히 관리.

---

### 📂 [2026-04-11] Phase 5: QueryDSL 아키텍쳐 통합 및 조회 성능 개선
> **"기술적 타협이 아닌 시스템적 근거에 기반한 데이터 접근 계층 구현"**

* **Core Implementation**
    * **QueryDSL Integration**
        * **문제 정의** : 문자열 기반의 JPQL은 컴파일 타입의 에러 검출이 불가, 런타임시에 발견되는 문제를 내포.
        * **해결 방안** : QueryDSL을 활용하여 타입 안전한 쿼리 작성 및 컴파일 타임의 검증이 가능하도록 설계.
        * **기대 효과** : 쿼리 오타 및 타입 안정성 확보, 추후 기능 구현에 있어 QueryDSL이 필수적임을 확립.
    * **Custom Repository**
        * **설계 구조**: `PostRepository` -> `JpaRepository` + `PostRepositoryCustom` (다중 상속)
        * **통찰**: Service Layer가 데이터 접근 기술에 직접 의존하지 않도록 설계를 고려, 단일 창구를 유지하면서 물리적 구현은 분리.
        * **트레이드 오프**: 엔터프라이즈급 어댑터 패턴의 유연성과 1인 프로젝트의 생산성에서 현실적 최적점이라 판단.
* **System Debugging & Troubleshooting**
    * **N + 1문제의 해결(Fetch Join)**
        * **현상** : 게시글 목록 조회 시 작성사(`Member`) 정보를 가져오기 위해 추가 쿼리가 발생하는 지점을 확인.
        * **조치** : QueryDSL 의 `fetchJoin()`을 명시적으로 사용하여 객체 그래프를 한 번의 쿼리로 병합.
        * **지표** : N + 1개의 쿼리를 1개의 쿼리로 최적화하여 DB 리소스 점유율 개선.
    * **Gradle & IDE 빌드 충돌 해결**
        * **현상**: 생성된 소스 경로 인식 불가 및 빌드 시 중복 클래스 생성 문제.
        * **해결**: `build.gradle` 설정을 통해 생성 경로를 명시적으로 분리하고, 빌드 라이프사이클에 맞춰 자동 생성 프로세스 안정화.
* **Architectural Insight**
    * **도메인 중심의 응집도 향상**
        * 서비스 레이어에서 데이터 접근 로직은 하나의 책임, `Post` 관련된 데이터 조회는 기술이 무엇이든 `PostRepository`라는 인터페이스 속에 숨겨야 함.
        * 다만 내부적으로 `Custom` 인터페이스를 통해 기술적 책임을 물리적으로 분리해 SRP, 개발 생산성, 편의성의 균형을 맞추려 노력.

---

### 📂 [2026-04-12] Phase 6: QueryDSL 검색/페이징 구현
> **"동적 쿼리를 활용한 정밀한 데이터 필터링 및 페이징 구축"**

* **Core Implementation**
    * **QueryDSL 기반 동적 검색(Search)
        * **문제 정의** : 다중 검색 조건(제목, 작성자)에 따라 수동으로 JPQL을 조립할 경우 쿼리 가독성 저하 및 런타임 에러 위험 증가
        * **해결 방안** : QueryDSL 의 `where` 절 내 `BooleanExpression` 을 반환하는 동적 메서드 체이닝 구조 채택.
        * **기대 효과** : 컴파일 타임의 타입 안정성 확복 및 검색 조건의 자유로움 조합 가능.
    * **물리 페이징 처리(Pagination)**
        * **설계 구조** : Spring Data의 `Pageable` 과 QueryDSL의 `offset`, `limit` 를 결합하여 원자적 데이터 절삭.
        * **통찰** : 전체 데이터를 메모리에 로드하지 않고 DB레벨에서 필요한 페이지 규격(Size : 10)만큼 추출하여 서버 부하 최소화.
        * **트레이드 오프** : 데이터 조회화 카운트 쿼리를 분리하여 개수 계산 시 불필요한 조인 비용을 제거하는 성능 최적화 수행.
* **System Debugging & Troubleshooting**
    * **검색 컨텍스트 유지(State Persistence)**
        * **현상** : 페이지 번호 이동시 검색 파라미터가 유실되어 전체 목록으로 리다이렉트 되는 데이터 파이프라인 결함 발견
        * **조치** : Thymeleaf 뷰 레이어에서 페이징 URL 생성시 현재 검색 조건 (`PostSearchCondition`)을 쿼리 스트링으로 강제 바인딩
        * **지표** : 페이지 전환 및 상세 보기 후 돌아오기 시에도 검색 필터링 상태가 유지됨을 확인
* **Architectural Insight**
    * **SQL 중심의 설계 우선주의**
        * JPA/QueryDSL은 수단일 뿐, 시스템의 본질은 '정확하고 효율적인 SQL 집합의 추출'에 있음. SQL의 논리 구조를 먼저 설계한 후 자바 코드로 조립하는 '데이터 우선(Data-First)' 접근 방식이 안정적인 시스템의 근간임을 확립.
    * **UX 중심의 페이징 블록 제어**
        * 단순 이전/다음 버튼이 아닌, 슬라이딩 윈도우 방식을 적용하여 하단 페이징 바가 일정한 범위(10개)를 유지하며 레이아웃 안정성을 확보하도록 제어.

---

### 📂 [2026-04-13] Phase 7: 복합 도메인(POST, COMMENT) 성능 최적화 및 N + 1 문제 해결
> **"단순 기능 구현 이상의 무결성과 조회 성능 최적화"**

* **Core Implementation**
    * **Soft Delete Foundation**
        * **문제 정의** : 단순 삭제는 데이터 휘발성을 높여 장애 복구 및 통계 활용에 불가.
        * **해결 방안** : `CommentStatus` (`NORMAL`, `DELETE`) 도입을 통해 물리적 삭제가 아닌 논리적 삭제 상태를 관리.
    * **QueryDSL 확장 -> CommentDomain**
        * **설계 구조** : `CommentRepository` -> `JpaRepository` + `CommentRepositoryCustom` 구조를 적용 댓글 도메인에도 타입 안정성이 보장된 동적 쿼리 주입.
        * **기대 효과** : 추후 진행될 댓글 페이징 등 로직을 위한 확장성 확보.
* **System Debugging & Troubleshooting**
    * **N + 1 문제 해결**
        * **현상** : 게시글 목록(10개) 조회 시, 각 게시글의 댓글 수를 집계하기 위해 개별적으로 `COUNT` 쿼리가 발생하는 N+1 병목(총 12회 쿼리) 확인.
        * **조치** :
            1. 서비스 레이어에서 게시글 ID 리스트를 추출하여 `CommentRepository`에 전달.
            2. QueryDSL의 `IN`절과 `GROUP BY`절을 활용하여 한번의 쿼리로 게시글의 댓글 수 일괄 집계.
            3. 조회된 `Tuple` 리스트를 자바 `Map`의 구조로 변환하여 메모리상의 `O(1)`의 속도로 게시글과 댓글 수 매핑.
        * **지표** : 쿼리 횟수를 12회에서 3회(게시글 + 카운트 + 댓글 집계)로 감소시킴.
* **Architectural Insight**
    * **도메인 독립성(Domain Integrity)**
        * **통찰** : 성능 최적화를 위해 `PostRepositroy`에서 댓글을 조인하여 집계도 가능하나, 이는 `Post`가 `Comment`의 내부를 알아야하는 강한 결합을 초래.
        * **결정** : 성능은 Batch 조회로 잡고 로직은 서비스 레이어 조립 방식을 채택하여 각 도메인 리포지토리의 독립된 책임을 유지.
    * **데이터 구조의 공학적 선택(`Map`)**
        * 단순히 반복을 돌리는 것이 아닌 `StreamAPI`, `Map`을 결합하여 시간 복잡도를 줄이는 것이 대규모 트래픽을 견디는 설계의 기초임을 확인.

---

### 📂 [2026-04-15] Phase 8: 성능 최적화 및 시스템 안정성 강화
> **"Batch Fetching을 통한 쿼리 상수화 `O(1)`및 전역 인프라 구축"**

* **Core Implementation**
    * **Hierarchy Comment**
        * **문제 정의** : 부모 댓글 (10개) 조회 후, 각 부모의 대댓글을 개별 조회할 경우 부모 개수만큼의 추가 쿼리 발생(N + 1).
        * **해결 방안** : `@BatchSize(size = 100)` 전략을 채택, Hibernate의 배치 페칭 매커니즘을 통해 부모 ID들을 `IN` 로 묶어 대댓글을 일괄 로드하게 설계.
        * **기대 효과** : 데이터 규모 (N)와 무관하게 댓글 조회 쿼리 수를 일정하게 유지하여 시스템 예측 가능성 확보.
    * **Global Infra**
        * **설계 구조** : `HandlerInterceptor`를 통한 중앙 집중식 권한 검증 및 `@ControllerAdvice` 기반의 전역 예외 처리기 구축.
        * **Rationale** :  개별 컨트롤러에서 인증 및 예외 로직을 분리함으로써 비즈니스 로직의 응집도를 높이고 시스템 전반의 횡단 관심사(Cross-cutting Concerns)를 효율적으로 관리.
* **System Debugging & Troubleshooting**
    * **다중 연관 관계의 N + 1 문제 해결**
        * **현상** : 댓글 엔티티 최적화 후에도 대댓글 작성자(`Member`) 정보를 개별적으로 호출하는 추가 쿼리 로그 확인.
        * **조치** :
            1. `Member` 엔티티 클래스 레벨에 `@BatchSize` 를 전역 적용
            2. 대댓글 로드시 연관된 작성자 엔티티 들 가지도 `IN` 절 기반의 벌크 조회가 일어나도록 유도.
        * **지표** : 상세 페이지 로드시 발생하는 쿼리 수를 약 5, 6(페이징 적용시)개 수준의 상수로 고정.
* **Architectural Insight**
    * **프레임워크 매커니즘을 이용한 복잡성 제어**
        * **통찰** : 모든 최적화 로직을 서비스 레이어에서 수동 조립시 코드 복잡도가 비대해져 유지보수 난해해짐.
        * **결정** : JPA의 지연 로딩 최적화 기능(`BatchSize`)를 적극 활용하여 런타임 성능과 최소한의 코드 복잡도 사이에서의 나름의 최적화를 도출.
    * **사용자 경험과 시스템 안정성의 결합**
        * 단순히 에러를 로그에 남기는 것이 아닌, 전역 예외 처리기를 통해 정돈된 에러 페이지를 제공함.

---

### 📂 [2026-04-16] Phase 9: DTO 리팩토링, 테스트
> **"DTO 리팩토링을 통한 각 계층 최적화, 및 테스트 코드 작성"**

* **Core Implementation**
    * **DTO Refactoring**
        * **문제 정의** : 서비스 레이어에서 엔티티를 직접 반환하거나 컨트롤러가 엔티티에 의존하는 설계는 변경에 취약하고, 보안상의 문제가 있음.
        * **해결 방안** : 모든 서비스 메서드의 반환 타입을 전용 DTO로 전환하고 `StreamAPI` 를 활용한 변환 로직을 서비스 내부로 캡슐화.
        * **기대 효과** : 엔티티 내부 구조가 변경되어도 외부 인터페이스의 영향을 받지 않는 유연성 확보.
    * **Comment Tree**
        * **설계 구조** : 부모 - 자식 관계의 엔티티 리스트를 서비스 레이어에서 `CommentResponse` 트리 구조로 리팩토링함.
        * **기대 효과** : 단순 평면적 데이터 나열이 아닌, 계층 구조를 서비스 레이어에서 정제하여 뷰 레이어의 렌더링 복잡성 감소.
* **System Debugging & Troubleshooting**
    * **Batch Fetching에 의한 테스트 격리 실패**
        * **현상** : DB에는 데이터가 존재함에도 테스트 코드에서 오류가 반복 발생. 로그 확인 결과 테스트 환경의 영속성 컨텍스트에 남아있는 잔상까지 함께 조회하려다가 내부 정합성이 깨짐.
        * **조치** :
            1. 테스트 환경(`application.properites`) 에서 `default_batch_fetch_size` 를  `0`으로 일시 격리.
        * **지표** : `IN` 절의 쿼리를 단일 조회 쿼리로 상수화하여 테스트 안정성 확보.
* **Architectural Insight**
    * **영속성 컨텍스트**
        * **통찰** : 성능을 위해 인프라 설정(`BatchSize`)이 역설적으로 테스트 환경의 신뢰도를 하락시킬 수 있음을 인지.

---

### 📂 [2026-04-17] Phase 10: 인프라 마이그레이션, 시스템 검증
> **"MySQL 이식을 통한 실전 데이터 환경 및 계층 통합 테스트"**

* **Core Implementation**
    * **MySQL Infrastructure Migration**
        * **결정** : 인메모리 DB(H2)에서 실제 운영 환경과 동일한 MySQL 8.0으로 DB 이식.
        * **디버깅** : 엔티티의 제약조건과 `address` 임베디드 타입 검증.
    * **Test Suite**
        * **Member** : 회원 가입시 중복 방어 및 로그인 인증 실패 시나리오 조사.
        * **Post & Comment** : 서비스 레이어의 비즈니스 로직이 실제 DB 트랜잭션 내에서 동작하는 지를 입증.
        * **결과** : 도메인(`Entity`) - 저장소(`Repository`) - 서비스(`Service`)로 이어지는 객체 그래프의 탐색과 조작 전 과정에 대해 초록불(Success) 확보.

---
