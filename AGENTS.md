# AGENTS.md

## Project Snapshot
- Multi-module Maven backend (`pom.xml`) for an affiliate commerce system.
- Two runtime entrypoints: `mshop-admin/src/main/java/com/mailvor/AdminServer.java` and `mshop-app/src/main/java/com/mailvor/AppServer.java`.
- Active modules: `mshop-admin`, `mshop-app`, `mshop-store`, `mshop-db`, `mshop-system`, `mshop-tool`, `mshop-log`, `mshop-gen`, `mshop-shop`, `mshop-mp`, `mshop-redis`.

## Architecture You Need First
- Boundary is audience-based: admin APIs in `mshop-admin`, mobile/user APIs in `mshop-app`.
- Core business logic is mostly in `mshop-store`; shared persistence scaffolding is in `mshop-db` (`BaseServiceImpl`, `CoreMapper`).
- Controllers in admin/app usually orchestrate services and return `ApiResult` (`mshop-system/src/main/java/com/mailvor/api/ApiResult.java`).
- Mapper discovery is centralized in both boot classes via `@MapperScan({"com.mailvor.modules.*.service.mapper", "com.mailvor.config"})`.

## Security and Request Flow Conventions
- Admin auth is Spring Security + JWT (`mshop-admin/.../SecurityConfig.java`), with `@AnonymousAccess` endpoints auto-whitelisted.
- App auth is interceptor-based (`mshop-app/.../common/interceptor/`):
  - `@AuthCheck` = login/scope required (validated by `PermissionInterceptor`).
  - `@UserCheck` = optional user context (resolved by `UserInterceptor`).
  - Read current user through `LocalUser` in business code.
- App token contract is `Authorization: Bearer <token>`.

## Data and Config Patterns
- Config is frequently DB-backed and Redis-cached (`MwSystemConfigServiceImpl#getData` and related getters/setters).
- Multi-platform keys are generated with `TkUtil.getMixedPlatformKey(...)`.
- Dynamic datasource path: `mshop-app/.../DruidConfig.java` + `mshop-system/.../DataSourceAspect.java` + `@DataSource`.

## Build/Run Workflow (Repo-Specific)
- Full build from repo root: `mvn clean install`.
- Tests are skipped by default in parent/admin/app POMs, so explicitly enable when needed: `mvn test -DskipTests=false`.
- Run admin API: `mvn -pl mshop-admin -am spring-boot:run`.
- Run app API: `mvn -pl mshop-app -am spring-boot:run`.

## Packaging and Environment Gotchas
- `mshop-admin/pom.xml` and `mshop-app/pom.xml` exclude `**/application*.yml` from jar packaging.
- Runtime config files are under `mshop-admin/src/main/resources/config/` and `mshop-app/src/main/resources/config/`.
- Repo currently contains plaintext sample secrets in config; never copy them into docs, logs, or PR comments.

## External Integration Surface
- Third-party commerce/payment integrations are concentrated in `mshop-store` and `mshop-app/modules/pay`.
- Several required dependencies are local `system`-scope jars from `lib/` (for example Taobao, JD, PDD, Yinsheng, Aspose).
- Keep payment callbacks anonymous in `mshop-app/.../PayNotifyController.java` (`/pay/notify/*`, `/ios/notify`).

## Existing AI Rule Sources Checked
- Glob scan results: `README.md`, `lib/README.md`, `lib/meituan-union-java/README.md`, `lib/yop-java-sdk-4.3.3-20230421155139-saas/README.md`.

