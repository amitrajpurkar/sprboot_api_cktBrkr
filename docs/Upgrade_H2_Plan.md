# Database Migration Plan: MongoDB → H2 In-Memory

**Current:** MongoDB  
**Target:** H2 In-Memory Database  
**Effort:** 1-2 days  
**Risk:** Medium  
**Prerequisites:** Spring Boot 3.2.x completed

---

## Executive Summary

Migration from **MongoDB** (NoSQL) to **H2** (in-memory SQL) for simplified demo/testing.

**Why H2?**
- ✅ Zero configuration
- ✅ Fast startup
- ✅ Embedded in application
- ✅ Web console included
- ✅ Perfect for demos

**Scope:**
- 2 Entities: `Product`, `InsuranceMember` (with nested Policy/Plan)
- 2 Repositories: `ProductRepository`, `MemberRepository`
- MongoDB queries → JPA/JPQL

---

## Phase 0: Preparation (1 hour)

### 0.1 Backup

```bash
git checkout -b backup/before-h2-migration
git commit -am "Backup before H2 migration"
git checkout -b feature/migrate-to-h2
```

### 0.2 Schema Design

**Relational Schema:**
- `products` table (simple 1:1)
- `members` table
- `policies` table (1:N with members)
- `plans` table (1:N with policies)

---

## Phase 1: Dependencies (30 min)

### 1.1 Update build.gradle

```gradle
dependencies {
    // ❌ REMOVE
    // implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
    
    // ✅ ADD
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.h2database:h2:2.2.224'
}
```

```bash
./gradlew clean build --refresh-dependencies
```

---

## Phase 2: Configuration (30 min)

### 2.1 Update application.properties

```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.defer-datasource-initialization=true

# Initialize data
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data.sql
```

### 2.2 Create data.sql

**File:** `src/main/resources/data.sql`

```sql
-- Products
INSERT INTO products (id, name, description, price) VALUES 
('1', 'Laptop', 'High-performance laptop', '$1299.99'),
('2', 'Mouse', 'Wireless mouse', '$29.99'),
('3', 'Keyboard', 'Mechanical keyboard', '$89.99');

-- Members
INSERT INTO members (id, party_id, firstname, lastname, date_of_birth) VALUES 
('M001', 'P001', 'John', 'Doe', '1985-03-15'),
('M002', 'P002', 'Jane', 'Smith', '1990-07-22');

-- Policies
INSERT INTO policies (member_id, policy_id, hcc_id, group_name, division, policy_start_date, policy_expiry_date) VALUES 
('M001', 'POL001', 'HCC001', 'Group A', 'Div 1', '2024-01-01', '2024-12-31');

-- Plans
INSERT INTO plans (policy_id, plan_definition_id, plan_name, plan_number, status) VALUES 
(1, 101, 'Gold Plan', 'GP001', 'Active');
```

---

## Phase 3: Entities (2 hours)

### 3.1 Product Entity

**File:** `src/main/java/com/anr/entity/Product.java`

```java
package com.anr.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    private String id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "price")
    private String price;

    // Constructors, getters, setters, builder...
}
```

### 3.2 InsuranceMember Entity

**File:** `src/main/java/com/anr/entity/InsuranceMember.java`

```java
package com.anr.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "members")
public class InsuranceMember {

    @Id
    private String id;
    
    @Column(name = "party_id")
    private String partyId;
    
    private String firstname;
    private String lastname;
    
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Policy> policies;

    // Getters, setters...
}
```

### 3.3 Policy Entity

**File:** `src/main/java/com/anr/entity/Policy.java`

```java
package com.anr.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "policies")
public class Policy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "member_id")
    private InsuranceMember member;
    
    private String policyID;
    private String hccID;
    
    @Column(name = "group_name")
    private String group;
    
    private String division;
    
    @Temporal(TemporalType.DATE)
    private Date policyStartDate;
    
    @Temporal(TemporalType.DATE)
    private Date policyExpiryDate;
    
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<Plan> plans;

    // Getters, setters...
}
```

### 3.4 Plan Entity

**File:** `src/main/java/com/anr/entity/Plan.java`

```java
package com.anr.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "plans")
public class Plan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;
    
    private Integer planDefinitionId;
    private String planName;
    private String planNumber;
    private String segment;
    private String planFamily;
    private String coverageType;
    private String status;

    // Getters, setters...
}
```

---

## Phase 4: Repositories (1 hour)

### 4.1 ProductRepository

**File:** `src/main/java/com/anr/repository/ProductRepository.java`

```java
package com.anr.repository;

import com.anr.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByName(String name);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findProductsWithDescriptionContaining(String keyword);
}
```

### 4.2 MemberRepository

**File:** `src/main/java/com/anr/repository/MemberRepository.java`

```java
package com.anr.repository;

import com.anr.entity.InsuranceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<InsuranceMember, String> {

    List<InsuranceMember> findByFirstname(String firstname);
    
    List<InsuranceMember> findByLastname(String lastname);

    @Query("SELECT m FROM InsuranceMember m WHERE m.dateOfBirth BETWEEN :from AND :to")
    List<InsuranceMember> findMembersWithinBirthdayRange(Date from, Date to);
}
```

---

## Phase 5: Update Services (30 min)

### 5.1 Update MemberService

**File:** `src/main/java/com/anr/service/MemberService.java`

```java
package com.anr.service;

import com.anr.entity.InsuranceMember;
import com.anr.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public InsuranceMember saveOne(InsuranceMember member) {
        return memberRepository.save(member);
    }

    public void saveBatch(List<InsuranceMember> members) {
        memberRepository.saveAll(members);
    }
    
    public List<InsuranceMember> findAll() {
        return memberRepository.findAll();
    }
}
```

### 5.2 Update ProductService

**File:** `src/main/java/com/anr/service/ProductService.java`

```java
package com.anr.service;

import com.anr.entity.Product;
import com.anr.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product saveOne(Product product) {
        return productRepository.save(product);
    }
}
```

---

## Phase 6: Update Configuration (30 min)

### 6.1 Remove MongoDB Config

```bash
rm src/main/java/com/anr/config/LocalmdbMongoConfig.java
```

### 6.2 Update Bootstrap

**File:** `src/main/java/com/anr/Bootstrap.java`

```java
@SpringBootApplication  // Remove MongoDB exclusion
public class Bootstrap extends SpringBootServletInitializer {
    // ...
}
```

---

## Phase 7: Update Tests (1 hour)

### 7.1 Update ProductServiceTest

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks private ProductService service;
    @Mock private ProductRepository repository;

    @Test
    void insert_one_product() {
        Product product = new Product.ProductBuilder("1", "Test").build();
        when(repository.save(product)).thenReturn(product);
        
        Product saved = service.saveOne(product);
        assertNotNull(saved);
    }
}
```

### 7.2 Create Integration Test

```java
@SpringBootTest
@AutoConfigureTestDatabase
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void testFindByName() {
        List<Product> products = repository.findByName("Laptop");
        assertFalse(products.isEmpty());
    }
}
```

---

## Phase 8: Verification (1 hour)

### 8.1 Build and Run

```bash
./gradlew clean build
./gradlew bootRun
```

### 8.2 Test H2 Console

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (empty)
```

### 8.3 Test Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Test API
curl -u user:password http://localhost:8080/api/v1/default
```

### 8.4 Verify Data

```sql
-- In H2 Console
SELECT * FROM products;
SELECT * FROM members;
SELECT * FROM policies;
SELECT * FROM plans;
```

---

## Migration Checklist

- [ ] Dependencies updated
- [ ] H2 configuration added
- [ ] data.sql created
- [ ] Product entity migrated
- [ ] InsuranceMember entity migrated
- [ ] Policy entity created
- [ ] Plan entity created
- [ ] ProductRepository migrated
- [ ] MemberRepository migrated
- [ ] Services updated
- [ ] MongoDB config removed
- [ ] Bootstrap updated
- [ ] Tests updated
- [ ] Application builds
- [ ] Application runs
- [ ] H2 console accessible
- [ ] Data loads correctly

---

## Rollback Plan

```bash
git checkout backup/before-h2-migration
```

---

## Success Criteria

- ✅ Application builds without errors
- ✅ All tests pass
- ✅ H2 console accessible
- ✅ Data persists in memory
- ✅ Queries work correctly
- ✅ No MongoDB dependencies

---

## Timeline

| Phase | Duration |
|-------|----------|
| Preparation | 1 hour |
| Dependencies | 30 min |
| Configuration | 30 min |
| Entities | 2 hours |
| Repositories | 1 hour |
| Services | 30 min |
| Config Updates | 30 min |
| Tests | 1 hour |
| Verification | 1 hour |
| **Total** | **8-10 hours (1-2 days)** |

---

**All upgrade plans complete!**
