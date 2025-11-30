# Repository Guidelines

## Project Structure & Module Organization
- Source lives in `src/main/java`; shared configs and properties in `src/main/resources`.
- Unit and integration tests go in `src/test/java`; test fixtures in `src/test/resources`.
- Keep domain-specific packages grouped by feature (e.g., `com.example.region.boundary`, `service`, `persistence`) to avoid a monolith `util` package.
- Example layout:
  - `src/main/java/.../api` for controllers or entrypoints
  - `src/main/java/.../service` for core logic
  - `src/main/java/.../persistence` for repositories/entities
  - `src/main/resources` for configuration, seeds, and templates

## Build, Test, and Development Commands
- `./mvnw clean verify`: full build with unit tests; use before pushes.
- `./mvnw test`: fast feedback; runs JUnit tests only.
- `./mvnw spring-boot:run`: run the app locally with live config from `src/main/resources`.
- `./mvnw fmt:format` and `./mvnw fmt:check`: format or verify formatting via Spotless/Google Java Format if configured.

## Coding Style & Naming Conventions
- Java 17+ recommended; use 2-space indentation in XML and 4-space in Java.
- Follow standard Java naming: `PascalCase` for classes, `camelCase` for methods/fields, `UPPER_SNAKE_CASE` for constants.
- Keep package names lowercase and feature-oriented; avoid generic names.
- Prefer constructor injection over field injection; keep controllers thin and services cohesive.
- Avoid unchecked nulls; use `Optional` at boundaries, and validate inputs early.

## Testing Guidelines
- Use JUnit 5 and Mockito; favor clear, behavior-focused tests.
- Name tests by behavior: `ClassNameMethod_ShouldExpectedBehavior`.
- For integration tests, use `@SpringBootTest` or slice tests (`@WebMvcTest`, `@DataJpaTest`) and isolate external effects with testcontainers or mocks.
- Aim for meaningful coverage on domain logic; prioritize tests around business rules and error handling.

## Commit & Pull Request Guidelines
- Use Conventional Commits (`feat:`, `fix:`, `chore:`, `test:`, `docs:`). Example: `feat: add region aggregation service`.
- Keep commits scoped and reversible; avoid mixing refactors with behavioral changes.
- Pull requests should include: brief summary, screenshots for HTTP/UI changes, steps to reproduce and test, and linked issue/ticket IDs.
- Ensure `./mvnw clean verify` passes before requesting review; note any known gaps or follow-ups.

## Security & Configuration Tips
- Keep secrets out of the repo; use environment variables or a secrets manager.
- Provide sane defaults in `application.yml` but guard environment-specific overrides with profiles (e.g., `application-local.yml`, `application-prod.yml`).
- Validate all external inputs (HTTP, messaging, persistence) and log with context; prefer structured logging for traceability.
