# Spring Boot JPA Board

## 🚀 프로젝트 한 줄 요약
JPA 실무 핵심 역량을 깊이 있게 다진 게시판 프로젝트 — QueryDSL, N+1 완전 해결, 계층형 댓글, MySQL 이식, 테스트 코드까지 구현

## 🛠 Tech Stack
- Language: Java 21
- Framework: Spring Boot 3.5.13
- Persistence: Spring Data JPA, QueryDSL, **MySQL 8.0**
- View: Thymeleaf, Bootstrap 5
- Test: JUnit5, AssertJ, `@DataJpaTest`, `@SpringBootTest`
- Tools: IntelliJ IDEA, Git, GitHub

## 🎯 핵심 성과 (Key Achievements)

**조회 성능 최적화**
- 게시글 목록 N+1 문제 해결 → 쿼리 **12회 → 3회**로 감소
- 계층형 댓글(대댓글) 최적화 → `@BatchSize` + IN 절 Batch Fetching 적용으로 상세 페이지 쿼리 **6회 이하로 상수화**

**아키텍처 및 설계**
- QueryDSL 기반 타입 안전 동적 검색 & 페이징 (검색 조건 유지)
- Self-referencing을 활용한 무한 계층 대댓글 구조
- DTO 계층별 리팩토링 + Immutable SessionDTO 설계

**인프라 & 테스트**
- H2 → **MySQL 8.0** 실환경 이식 완료
- Repository, Service, Domain Layer에 걸친 통합 및 단위 테스트 작성

---

## ⚡ 주요 Troubleshooting & Insight

**1. N+1 문제 해결**
- 현상: 게시글 목록 및 댓글 조회 시 N+1 쿼리 폭발
- 해결: QueryDSL Fetch Join → `@BatchSize` + Batch IN 조회 전략으로 전환
- 결과: 데이터 규모와 무관하게 예측 가능한 쿼리 성능 확보

**2. 계층형 댓글 성능 최적화**
- `@BatchSize`를 Member, Comment 엔티티에 전략적으로 적용
- Service Layer에서 Comment Tree 구조 조립으로 View Layer 부하 감소

---

## 📂 Project Structure
```text
src/main/java/com/project/spring_jpa_board/
├── config/           # QueryDSL, Interceptor
├── domain/
│   ├── entity/       # @BatchSize 적용
│   ├── repository/   # Custom Repository (QueryDSL)
│   └── service/      # DTO 변환 및 비즈니스 로직
├── web/
│   ├── dto/          # 계층별 최적화 DTO
│   ├── advice/       # GlobalExceptionHandler
│   └── interceptor/  # 인증·권한 검증
└── resources/templates/error/
```

---
## 📅 Development Journey (설계 및 구현 기록)
  ### **[상세 개발 로그 전체 보기 (DEVELOPMENT_LOG.md)](./DEVELOPMENT_LOG.md)**

  * **phase 1-3** : 도메인 설계 및 세션 인증
  * **phase 4-6** : QueryDSL 동적 쿼리 & 페이징
  * **phase 7-9** : N+1 해결, Soft Delete, DTO 리팩토링
  * **phase 10** : MySQL 이식 및 테스트 코드 작성

---
## 🧪 테스트 전략 (Test Strategy)
  * Member : 중복 가입 방어 및 인증 실패 시나리오 조사.
  * post : 동적 검색조건 (제목 / 작성자)에 페이징 쿼리 정확도 검증.
  * Comment : 부모 - 자식 연관관계 매핑 및 삭제 상태 전이 로직 검증.