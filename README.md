# Spring Boot JPA Board Toy Project
## 🚀 프로젝트 개요: Spring-JPA Board (신입 개발자 핵심 역량 증명)
- 본 프로젝트는 신입 백엔드 개발자로서 갖춰야 할 가장 본질적인 기술적 역량—웹 인증 메커니즘 
- JPA를 활용한 객체 중심의 데이터 모델링
- 효율적인 계층 간 데이터 전송을 위해 구축
---
## 🛠 Tech Stack (기술 스택)
- Language: Java 21
- Framework: Spring Boot 3.5.13
- Persistence: Spring Data JPA, H2 Database
- View Engine: Thymeleaf, Bootstrap 5
- Tool: IntelliJ IDEA, Git, Git Hub, MacBook Pro
---
## 📂 Project Structure (프로젝트 구조)
```text
spring-jpa-board/
├── .gradle/
├── .idea/
├── src/
│   └── main/
│       ├── java/com/project/spring_jpa_board/
│       │   ├── config/          # 시스템 설정
│       │   ├── domain/
│       │   │   ├── entity/      # Member, Post, Comment, Address
│       │   │   ├── repository/  # JpaRepository 인터페이스
│       │   │   └── service/     # 비즈니스 로직 (MemberService 등)
│       │   ├── exception/       # 공통 예외 처리
│       │   └── web/
│       │       ├── controller/  # HomeController, MemberController
│       │       └── dto/         # JoinDTO, LoginDTO, SessionDTO
│       └── resources/
│           ├── static/          # CSS, JS, 이미지 (정적 파일)
│           ├── templates/       # Thymeleaf 뷰 (joinForm, home 등)
│           └── application.properties # 시스템 환경 설정
├── build.gradle                 # 빌드 및 의존성 관리
└── README.md                    # 프로젝트 문서
```
---
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
