# ADR-001: Upgrade to Spring Boot 4.x and switch deployment packaging from WAR to executable JAR

## Status
Proposed

## Date
2026-11-05

## Context

The application is currently on Spring Boot 3.2.10, Java 21, and Gradle 8.11.1 (see [`docs/code_analysis_20261105.md`](../code_analysis_20261105.md) for the full as-is baseline). The intended next step is to upgrade Java, Spring Boot, and all dependencies to their latest stable versions.

As of 2026-11-05, the latest stable Spring Boot release is **4.1.2** (Spring Framework 7, Jakarta EE 11 / Servlet 6.1 baseline, minimum Java 17). This is a **major** version boundary, not a routine minor bump — Spring Boot 3.x and 4.x differ enough that the Spring Boot project's own migration guide recommends landing on the latest available 3.5.x release first, to absorb accumulated 3.3/3.4/3.5 deprecations before attempting the 4.0 jump in one step.

Two separate but related questions arise from this:

1. **Whether to target Spring Boot 4.x now, or stop at the latest 3.5.x.** The application has no stakeholder-facing deadline forcing 4.x; staying on 3.5.x is a legitimate, lower-risk choice. But 3.x will eventually go end-of-life, and the current effort is already an "upgrade to latest" exercise, not an incremental patch.
2. **Whether to keep WAR packaging.** The application currently builds a WAR (`war` Gradle plugin, `Bootstrap extends SpringBootServletInitializer`) and that WAR is the artifact copied into the Docker image (`Dockerfile`: `COPY --from=builder /app/build/libs/*.war app.war`). Web research on Spring Boot 4 found `SpringBootServletInitializer` is deprecated-leaning but not confirmed removed; the official migration guide must be checked directly before relying on it. Separately, this application has no actual deployment target that requires a WAR — it is never deployed to an external application server; every path to production is the Docker image, which runs `java -jar` against an embedded Tomcat. The WAR mechanism exists in the codebase but serves no current use case.

These two questions are bundled into one ADR because the packaging decision only needs resolving *in the context of* the Boot-4 migration — if Boot 4 cleanly supports WAR, the packaging question becomes optional cleanup; if it does not, the packaging question becomes a blocking prerequisite. Either way, the decision should be made once, before Phase 4 of the upgrade plan, rather than discovered mid-migration.

## Decision

1. **Target Spring Boot 4.1.2**, reached via the staged path already defined in `docs/code_analysis_20261105.md` §9: latest Java 21→25, Gradle 8.11.1→9.8.2, Spring Boot 3.2.10→latest 3.5.x, *then* 3.5.x→4.1.2, each step independently built and tested before the next begins.
2. **Switch deployment packaging from WAR to an executable JAR with embedded Tomcat**, independent of whether Spring Boot 4 continues to support WAR. Concretely:
   - Remove the `war` Gradle plugin; use `bootJar` instead of `bootWar`.
   - Remove `SpringBootServletInitializer` from `Bootstrap` — revert to a plain `@SpringBootApplication` class with `SpringApplication.run(...)`.
   - Update `Dockerfile` to build and copy the JAR (`build/libs/*.jar`) instead of the WAR, and change the `ENTRYPOINT` accordingly.
   - Re-verify the actuator `HEALTHCHECK` and container startup behavior after the switch.

## Rationale

- **Staging through 3.5.x first is the vendor-recommended path**, not an arbitrary precaution — it converts one high-risk jump into two lower-risk ones, each with its own test checkpoint, consistent with this project's "smallest change that moves toward a solution, validate before compounding" discipline.
- **Spring Boot 4's minimum Java version is 17**, which decouples the language-runtime upgrade (Java 21→25) from the framework-major-version upgrade (Boot 3→4). Each can be validated independently (as already reflected in the phased plan), reducing the number of simultaneous variables when a test fails.
- **The WAR/packaging question is better resolved by removing the dependency on an uncertain answer than by betting on it.** The application's only real deployment path today is the Docker image running an embedded container — WAR packaging is vestigial relative to actual usage. Switching to an executable JAR removes a deployment mode nobody uses, sidesteps the open question of whether `SpringBootServletInitializer` still works correctly on Boot 4, and is a well-trodden, low-risk mechanical change (plugin swap + one class edit + one Dockerfile edit) regardless of the Spring Boot version.
- **Making this decision once, in an ADR, before Phase 4 begins** avoids discovering a packaging blocker mid-migration, when sunk effort creates pressure to work around rather than resolve it properly.

## Alternatives considered

### A. Stay on the latest Spring Boot 3.5.x; do not attempt 4.x in this cycle
- **Pros:** Lower risk, smaller diff, avoids the Spring Framework 7 transition entirely.
- **Cons:** Defers the inevitable; the stated goal of this effort is "upgrade to the latest version," and stopping short of the actual latest leaves the application on a line that will reach end-of-life sooner.
- **Rejected:** Contradicts the explicit goal of this upgrade effort. Revisit only if Phase 4 (§9 of the analysis doc) surfaces a blocking incompatibility within its time-box (see Rollback criteria below).

### B. Jump directly from 3.2.10 to 4.1.2 without staging through 3.5.x
- **Pros:** Fewer total build/test cycles.
- **Cons:** Combines framework-minor-version deprecations with the major-version transition, making any failure harder to localize; goes against Spring's own migration guidance.
- **Rejected:** Higher risk for no real time savings — the 3.5.x waypoint is itself a normal upgrade step this application would need to pass through regardless.

### C. Keep WAR packaging and assume continued Spring Boot 4 support
- **Pros:** No change to `Bootstrap`, `build.gradle` (`war` plugin), or `Dockerfile`; smaller diff for this ADR's scope.
- **Cons:** Rests on an unconfirmed assumption (deprecated-leaning, not verified removed or retained); if wrong, discovered only during Phase 4 execution, mid-migration.
- **Rejected:** The application has no use case that needs WAR today, so there is no benefit to preserving it worth the risk of an unverified assumption.

### D. Move to deployment on an external/traditional application server (Tomcat, WildFly) instead of the embedded-container Docker model
- **Pros:** Would make WAR packaging load-bearing again, resolving the ambiguity by making WAR necessary rather than optional.
- **Cons:** No stakeholder need identified for this; reverses the project's existing Docker-first, embedded-container deployment strategy for no stated benefit.
- **Rejected:** Out of scope — not something this upgrade effort has a reason to introduce.

## Trade-offs

- The Spring Boot 4 migration itself carries real risk independent of packaging: Spring Security's `SecurityFilterChain` API has continued to shift across releases, actuator default exposure may change again, and transitively-managed Jackson/Hibernate versions move with the Boot BOM. This ADR accepts that risk as the cost of reaching "latest," mitigated by the staged Phase 1–4 approach and the existing ArchUnit/JUnit regression gate.
- The packaging switch touches three files (`build.gradle`, `Bootstrap.java`, `Dockerfile`) and requires re-validating the container health check — a small, mechanical cost, accepted because it removes an open question rather than carrying it forward.
- Resilience4j (`resilience4j-spring-boot3` artifact) and springdoc-openapi both need their Spring-Boot-4-compatible artifact coordinates confirmed at execution time (naming may change with the Boot major version) — this is a known follow-up, not resolved by this ADR.

## Consequences

- `docs/code_analysis_20261105.md` §9 Phase 4 proceeds under this decision: the WAR→JAR switch happens as part of Phase 4, not deferred to Phase 7 cleanup.
- `Bootstrap.java` loses its `extends SpringBootServletInitializer` and the `war`/`eclipse-wtp` Gradle plugins become removable (`eclipse-wtp` exists specifically to support WAR/Eclipse-web-tooling workflows and has no purpose once the app is JAR-only).
- `README.md`'s technology-stack section must be updated to reflect "executable JAR" deployment once this lands (tracked under Phase 7 of the upgrade plan).
- `ArchitectureTest`'s existing rule — "no classes should depend on anything annotated `@Deprecated`" — will continue to be the automated check that this migration does not quietly reintroduce a deprecated Spring Boot 4 API.

## Rollback criteria

Roll back the **Spring Boot 4.x** portion of this decision (retreating to the latest Spring Boot 3.5.x) if, within Phase 4's time-box:
- A required dependency (Resilience4j, springdoc-openapi, or another direct dependency) has no Spring-Boot-4-compatible release available yet, or
- The Spring Framework 7 transition breaks `BasicSecConfiguration` or actuator behavior in a way that cannot be resolved within the phase's scope, or
- The official Spring Boot 4.0 migration guide, once consulted directly, reveals a breaking change with no acceptable workaround for this application.

The **WAR→JAR packaging** portion of this decision is **not** contingent on the Spring Boot version and should not be rolled back even if the Boot 4 jump itself is rolled back — it is independently justified (§ Rationale) and low-regret either way.
