# Upgrade Plan: Java 14 → Java 21 LTS & Spring Boot 2.3.3 → 3.2.x

**Current State:** Java 14.0.2, Spring Boot 2.3.3.RELEASE, Gradle 6.6  
**Target State:** Java 21 LTS (already installed ✅), Spring Boot 3.2.x, Gradle 9.x  
**Estimated Effort:** 3-5 days  
**Risk Level:** Medium-High

**Note:** Java 21 is already installed on this machine, so Java installation steps can be skipped.

---

## Phase 0: Pre-Upgrade Preparation

### 0.1 Backup and Version Control

```bash
git checkout -b backup/pre-upgrade-java14-sb2.3.3
git push origin backup/pre-upgrade-java14-sb2.3.3
git checkout -b feature/upgrade-java21-sb3.2
```

### 0.2 Document Current State

```bash
./gradlew clean test jacocoTestReport
cp -r build/reports backup/
./gradlew dependencies > backup/dependencies-before.txt
```

### 0.3 Create Test Checklist

- [ ] Application starts
- [ ] REST endpoints respond
- [ ] Circuit breaker works
- [ ] Logging aspects work
- [ ] MongoDB operations succeed
- [ ] Swagger UI accessible
- [ ] All tests pass

---

## Phase 1: Gradle 9 Upgrade (2 hours)

### 1.1 Update Gradle Wrapper to Version 9

**Current:** Gradle 6.6  
**Target:** Gradle 9.0 (latest stable)

```bash
# Update to Gradle 9.0
./gradlew wrapper --gradle-version 9.0 --distribution-type all

# Verify upgrade
./gradlew --version
```

**Expected Output:**
```
Gradle 9.0
Build time:   2024-11-...
Revision:     ...
Kotlin:       1.9.24
Groovy:       3.0.21
Ant:          Apache Ant(TM) version 1.10.14
JVM:          21.0.x
OS:           Mac OS X
```

### 1.2 Update build.gradle - Remove JCenter

```gradle
repositories {
    mavenCentral()  // Keep only this
    // jcenter()  ❌ REMOVE
}
```

### 1.3 Test Build

```bash
./gradlew clean build --warning-mode all
```

---

## Phase 2: Java Configuration (1 hour)

### 2.1 Verify Java 21 Installation

**Note:** Java 21 is already installed on this machine. Verify and configure:

```bash
# Verify Java 21 is available
java -version

# Expected output:
# openjdk version "21.0.x" or similar
# Java(TM) SE Runtime Environment
```

**If multiple Java versions exist, ensure Java 21 is active:**

```bash
# Using SDKMAN (if installed)
sdk list java
sdk use java 21.0.x-tem

# Or set JAVA_HOME manually
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
echo $JAVA_HOME
```

### 2.2 Update build.gradle for Java 17 (Intermediate Step)

**Important:** We'll first upgrade to Java 17 for Spring Boot 2.7.x compatibility, then to Java 21 later.

```gradle
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

### 2.3 Create/Update gradle.properties

```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true
```

### 2.4 Test

```bash
./gradlew clean compileJava compileTestJava test
```

---

## Phase 3: Spring Boot 2.7.x Upgrade (1 day)

### 3.1 Update build.gradle - Versions

```gradle
plugins {
    id 'org.springframework.boot' version '2.7.18'
    id 'io.spring.dependency-management' version '1.1.4'
}
```

### 3.2 Add Spring Cloud BOM

```gradle
ext {
    set('springCloudVersion', "2021.0.8")
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```

### 3.3 Update Dependencies

```gradle
dependencies {
    // Spring Boot (managed versions)
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-aop'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'

    // OpenAPI
    implementation 'org.springdoc:springdoc-openapi-ui:1.7.0'
    
    // Circuit Breaker
    implementation 'net.jodah:failsafe:2.4.4'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-hystrix'
    
    // Utilities
    implementation 'com.google.guava:guava:32.1.3-jre'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'commons-io:commons-io:2.15.1'
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    
    // Lombok
    compileOnly 'org.projectlombok:lombok:1.18.30'
    annotationProcessor 'org.projectlombok:lombok:1.18.30'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.mockito:mockito-core:5.7.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.7.0'
    testImplementation 'com.tngtech.archunit:archunit-junit5:1.2.1'
    testImplementation 'io.rest-assured:rest-assured:5.4.0'
}
```

### 3.4 Update JaCoCo

```gradle
jacoco {
    toolVersion = '0.8.11'
}

test {
    useJUnitPlatform()
}
```

### 3.5 Fix Security Configuration

**File:** `src/main/java/com/anr/config/BasicSecConfiguration.java`

```java
@Configuration
@EnableWebSecurity
public class BasicSecConfiguration {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .anyRequest().permitAll();
        return http.build();
    }
}
```

### 3.6 Migrate Tests to JUnit 5

**File:** `src/test/java/com/anr/service/ProductServiceTest.java`

```java
// Change imports
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Test
    void insert_one_product() { ... }
}
```

### 3.7 Build and Test

```bash
./gradlew clean build
./gradlew bootRun
```

Test: `curl http://localhost:8080/actuator/health`

---

## Phase 4: Spring Boot 3.2.x Upgrade (1-2 days)

### 4.1 Update build.gradle

```gradle
plugins {
    id 'org.springframework.boot' version '3.2.1'
    id 'io.spring.dependency-management' version '1.1.4'
}
```

### 4.2 Remove Spring Cloud (Hystrix incompatible)

```gradle
// REMOVE Spring Cloud BOM
// REMOVE: implementation 'org.springframework.cloud:spring-cloud-starter-netflix-hystrix'
```

**File:** `src/main/java/com/anr/Bootstrap.java`

```java
// REMOVE @EnableCircuitBreaker annotation
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class Bootstrap extends SpringBootServletInitializer {
```

### 4.3 Add OpenRewrite for Migration

```gradle
plugins {
    id 'org.openrewrite.rewrite' version '6.10.0'
}

dependencies {
    rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:2.7.0"))
    rewrite("org.openrewrite.recipe:rewrite-spring")
}

rewrite {
    activeRecipe("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2")
}
```

Run: `./gradlew rewriteRun`

### 4.4 Update Dependencies

```gradle
dependencies {
    // Spring Boot starters
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-aop'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'

    // OpenAPI for Spring Boot 3.x
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
    
    // Failsafe (Hystrix removed)
    implementation 'net.jodah:failsafe:2.4.4'
    
    // Utilities
    implementation 'com.google.guava:guava:32.1.3-jre'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'commons-io:commons-io:2.15.1'
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    
    // Lombok
    compileOnly 'org.projectlombok:lombok:1.18.30'
    annotationProcessor 'org.projectlombok:lombok:1.18.30'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 4.5 Update OpenAPI Config

**File:** `src/main/java/com/anr/config/OpenApiConfig.java`

```java
import org.springdoc.core.models.GroupedOpenApi;  // Updated import

@Configuration
public class OpenApiConfig {
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("sb-svc-public")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SampleBackend API")
                        .description("Spring Boot 3.2.x Sample")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")));
    }
}
```

### 4.6 Update Security Config (Lambda DSL)

**File:** `src/main/java/com/anr/config/BasicSecConfiguration.java`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}
```

### 4.7 Temporarily Disable Hystrix Code

**File:** `src/main/java/com/anr/logging/ControllerLoggingAspect.java`

Replace Hystrix command with direct execution:

```java
@Around("execution(* com.anr.controller.MainSBController.getSampleResponse(..))")
public SBResponseModel logSampleResponse(ProceedingJoinPoint jointpoint, ...) {
    long startTime = System.currentTimeMillis();
    SBResponseModel response;
    
    try {
        response = (SBResponseModel) jointpoint.proceed();
    } catch (Throwable t) {
        response = failures.getSampleFailureResponse(...);
    }
    
    // Logging logic remains same
    return response;
}
```

Rename: `CircuitBreakerHystrixConfig.java` → `CircuitBreakerHystrixConfig.java.deprecated`

### 4.8 Build and Test

```bash
./gradlew clean build
./gradlew bootRun
```

---

## Phase 5: Java 21 Upgrade (2 hours)

### 5.1 Verify Java 21 (Already Installed ✅)

**Note:** Java 21 is already installed on this machine. Just verify it's active:

```bash
# Verify Java 21
java -version

# Should show: openjdk version "21.0.x" or java version "21.0.x"
```

### 5.2 Update build.gradle to Java 21

```gradle
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
```

### 5.3 Update Gradle Compatibility

Ensure Gradle 9 is being used (already upgraded in Phase 1):

```bash
./gradlew --version
# Should show: Gradle 9.0
```

### 5.4 Test Compilation and Build

```bash
# Clean build with Java 21
./gradlew clean build test

# Verify all tests pass
./gradlew test --info
```

### 5.5 Enable Java 21 Features (Optional)

**Virtual Threads (Project Loom):**

Add to `application.properties`:

```properties
# Enable virtual threads (Java 21 feature)
spring.threads.virtual.enabled=true
```

**Benefits:**
- Improved throughput for I/O-bound operations
- Better resource utilization
- Simplified concurrent programming

**Pattern Matching Enhancements:**

You can now use Java 21 pattern matching features in your code:

```java
// Record patterns (Java 21)
if (obj instanceof Point(int x, int y)) {
    System.out.println("Point at: " + x + ", " + y);
}

// Switch pattern matching
String result = switch (obj) {
    case String s -> "String: " + s;
    case Integer i -> "Integer: " + i;
    case null -> "null value";
    default -> "Unknown type";
};
```

### 5.6 Update Documentation

Update any references to Java version in:
- `README.md`
- `build.gradle` comments
- JavaDoc headers

---

## Phase 6: Final Verification (4 hours)

### 6.1 Run All Tests

```bash
./gradlew clean test jacocoTestReport
```

### 6.2 Verify Endpoints

```bash
# Health
curl http://localhost:8080/actuator/health

# API
curl -u user:password http://localhost:8080/api/v1/default

# Swagger
open http://localhost:8080/swagger-ui/index.html
```

### 6.3 Compare Coverage

```bash
diff backup/coverage-before-upgrade build/reports/coverage
```

### 6.4 Performance Testing

Run load tests to ensure no performance regression.

---

## Phase 7: Documentation Updates

### 7.1 Update README.md

```markdown
## Requirements
- Java 21 LTS
- Gradle 9.0+
- Spring Boot 3.2.x
- MongoDB (optional)
```

### 7.2 Update Comments

Remove outdated references to Java 14, Spring Boot 2.x.

### 7.3 Create Migration Notes

Document breaking changes and migration steps.

---

## Rollback Plan

If issues arise:

```bash
git checkout backup/pre-upgrade-java14-sb2.3.3
```

---

## Post-Upgrade Tasks

1. **Replace Hystrix with Resilience4j** (see Upgrade_CircuitBreaker_Plan.md)
2. **Add ArchUnit tests**
3. **Enable proper security**
4. **Add containerization (Docker)**
5. **Set up CI/CD pipeline**

---

## Common Issues & Solutions

### Issue 1: javax.* imports not found

**Solution:** Run OpenRewrite or manually replace with jakarta.*

### Issue 2: WebSecurityConfigurerAdapter not found

**Solution:** Use SecurityFilterChain bean approach

### Issue 3: Hystrix not compatible

**Solution:** Remove temporarily, replace with Resilience4j

### Issue 4: Test failures

**Solution:** Migrate all tests to JUnit 5

### Issue 5: MongoDB connection issues

**Solution:** Verify MongoDB driver compatibility

---

## Success Criteria

- ✅ Application builds without errors
- ✅ All tests pass
- ✅ Application starts successfully
- ✅ All endpoints respond correctly
- ✅ Swagger UI accessible
- ✅ No security vulnerabilities in dependencies
- ✅ Code coverage maintained or improved

---

## Timeline Summary

| Phase | Duration | Critical Path | Notes |
|-------|----------|---------------|-------|
| Phase 0: Preparation | 2 hours | Yes | Backup and baseline |
| Phase 1: Gradle 9 | 2 hours | Yes | 6.6 → 9.0 |
| Phase 2: Java Config | 1 hour | Yes | Java 21 already installed ✅ |
| Phase 3: Spring Boot 2.7 | 1 day | Yes | Intermediate version |
| Phase 4: Spring Boot 3.2 | 1-2 days | Yes | Major upgrade |
| Phase 5: Java 21 | 2 hours | No | Already installed, just config |
| Phase 6: Verification | 4 hours | Yes | Testing and validation |
| Phase 7: Documentation | 2 hours | No | Update docs |
| **Total** | **3-4 days** | | Reduced due to Java 21 pre-installed |

---

**Next Steps:** After completing this upgrade, proceed to `Upgrade_CircuitBreaker_Plan.md` to replace Hystrix with Resilience4j.

---

## Appendix A: Gradle 9 Specific Features & Benefits

### Why Gradle 9?

Gradle 9.0 (released November 2024) brings significant improvements:

**Performance Enhancements:**
- ✅ **Configuration cache improvements** - Faster build times (up to 90% faster)
- ✅ **Better incremental builds** - Smarter change detection
- ✅ **Parallel execution optimizations** - Better CPU utilization

**Java 21 Support:**
- ✅ **Full Java 21 compatibility** - Native support for latest JDK features
- ✅ **Virtual threads support** - Better integration with Project Loom
- ✅ **Pattern matching** - Gradle scripts can use Java 21 features

**Build Performance:**
- ✅ **Faster dependency resolution** - Improved caching mechanisms
- ✅ **Better daemon performance** - Reduced memory footprint
- ✅ **Optimized task execution** - Smarter task graph

### Gradle 9 Configuration Cache

Enable for maximum performance:

**File:** `gradle.properties`

```properties
# Enable configuration cache (Gradle 9 feature)
org.gradle.configuration-cache=true
org.gradle.configuration-cache.problems=warn

# Other performance settings
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
```

**Expected Benefits:**
- First build: Normal speed
- Subsequent builds: 50-90% faster

### Gradle 9 Breaking Changes to Watch

**1. Deprecated Features Removed:**
- Old `compile` configuration (use `implementation`)
- Legacy `testCompile` (use `testImplementation`)
- Already fixed in this codebase ✅

**2. Plugin API Changes:**
- Some internal APIs changed
- Spring Boot plugin 3.2.x is fully compatible ✅

**3. Kotlin DSL Improvements:**
- Not applicable (using Groovy DSL)

### Gradle 9 New Features You Can Use

**1. Declarative Gradle (Preview):**

```gradle
// More readable build configuration
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.1'
}

// Cleaner dependency declarations
dependencies {
    implementation platform('org.springframework.boot:spring-boot-dependencies:3.2.1')
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

**2. Improved Test Reporting:**

```gradle
test {
    useJUnitPlatform()
    
    // Enhanced test output (Gradle 9)
    testLogging {
        events "passed", "skipped", "failed", "standardOut", "standardError"
        showExceptions true
        showCauses true
        showStackTraces true
        exceptionFormat "full"
    }
}
```

**3. Better Dependency Insights:**

```bash
# New Gradle 9 command for better dependency analysis
./gradlew dependencies --configuration runtimeClasspath --scan

# Interactive build scan
./gradlew build --scan
```

### Gradle 9 Compatibility Matrix

| Tool/Framework | Gradle 9 Compatible | Notes |
|----------------|---------------------|-------|
| Java 21 | ✅ Yes | Full support |
| Spring Boot 3.2.x | ✅ Yes | Fully tested |
| JUnit 5 | ✅ Yes | Native support |
| Lombok | ✅ Yes | Works with annotation processing |
| JaCoCo 0.8.11+ | ✅ Yes | Full compatibility |
| MongoDB Driver | ✅ Yes | No issues |

### Migration from Gradle 6.6 to 9.0

**Major Version Jumps:**
- 6.6 → 7.x → 8.x → 9.0

**Gradle handles this automatically**, but be aware:

**Removed in Gradle 7:**
- `compile` → `implementation` (already fixed ✅)
- `runtime` → `runtimeOnly` (already fixed ✅)

**Removed in Gradle 8:**
- Some deprecated APIs (not used in this project ✅)

**New in Gradle 9:**
- Configuration cache stable
- Better Java toolchain support
- Enhanced build performance

### Troubleshooting Gradle 9

**Issue 1: Configuration cache warnings**

```bash
# Run with warnings to see issues
./gradlew build --configuration-cache --warning-mode all

# Fix by making tasks configuration-cache compatible
```

**Issue 2: Plugin compatibility**

```bash
# Check plugin versions
./gradlew buildEnvironment

# Update plugins if needed
```

**Issue 3: Daemon issues**

```bash
# Stop all daemons
./gradlew --stop

# Restart with Gradle 9
./gradlew --version
./gradlew build
```

### Gradle 9 Performance Tuning

**Optimal settings for this project:**

**File:** `gradle.properties`

```properties
# Gradle 9 optimized settings
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
org.gradle.configuration-cache=true
org.gradle.configuration-cache.problems=warn

# File system watching (Gradle 9 improvement)
org.gradle.vfs.watch=true

# Build cache
org.gradle.unsafe.configuration-cache=true
```

**Expected build time improvements:**
- Initial build: ~30-60 seconds
- Incremental builds: ~5-10 seconds (with configuration cache)
- Clean builds: ~20-30 seconds

### Verifying Gradle 9 Installation

```bash
# Check Gradle version
./gradlew --version

# Expected output:
# ------------------------------------------------------------
# Gradle 9.0
# ------------------------------------------------------------
# Build time:   2024-11-xx xx:xx:xx UTC
# Revision:     xxxxx
# Kotlin:       1.9.24
# Groovy:       3.0.21
# Ant:          Apache Ant(TM) version 1.10.14 compiled on August 16 2023
# JVM:          21.0.x (Oracle Corporation 21.0.x+xx-LTS)
# OS:           Mac OS X 14.x.x aarch64

# Test build with Gradle 9
./gradlew clean build --info

# Check for any deprecation warnings
./gradlew build --warning-mode all
```

---

## Appendix B: Java 21 Features Available After Upgrade

Since Java 21 is already installed, here are the features you can leverage:

### 1. Virtual Threads (Project Loom)

**Enable in Spring Boot:**

```properties
# application.properties
spring.threads.virtual.enabled=true
```

**Benefits:**
- Massive scalability for I/O operations
- Simplified concurrent code
- Better resource utilization

### 2. Pattern Matching for Switch

```java
// Java 21 pattern matching
public String formatValue(Object obj) {
    return switch (obj) {
        case Integer i -> String.format("int %d", i);
        case Long l -> String.format("long %d", l);
        case Double d -> String.format("double %f", d);
        case String s -> String.format("String %s", s);
        case null -> "null";
        default -> obj.toString();
    };
}
```

### 3. Record Patterns

```java
// Deconstruct records in pattern matching
record Point(int x, int y) {}

public void processPoint(Object obj) {
    if (obj instanceof Point(int x, int y)) {
        System.out.println("Point: " + x + ", " + y);
    }
}
```

### 4. Sequenced Collections

```java
// New methods on List, Set, Map
List<String> list = new ArrayList<>();
list.addFirst("first");
list.addLast("last");
String first = list.getFirst();
String last = list.getLast();
```

### 5. String Templates (Preview)

```java
// Simplified string interpolation (preview feature)
String name = "World";
String message = STR."Hello, \{name}!";
```

---

## Appendix C: Quick Reference Commands

### Gradle Commands

```bash
# Upgrade Gradle wrapper
./gradlew wrapper --gradle-version 9.0 --distribution-type all

# Check version
./gradlew --version

# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Generate coverage
./gradlew jacocoTestReport

# Run application
./gradlew bootRun

# Check dependencies
./gradlew dependencies

# Build scan
./gradlew build --scan

# Stop daemon
./gradlew --stop
```

### Java Commands

```bash
# Check Java version
java -version

# List installed Java versions (macOS)
/usr/libexec/java_home -V

# Set Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify JAVA_HOME
echo $JAVA_HOME
```

### Testing Commands

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests ProductServiceTest

# Run with info logging
./gradlew test --info

# Generate coverage report
./gradlew jacocoTestReport

# View coverage report
open build/reports/coverage/index.html
```

---

**End of Upgrade Plan**
