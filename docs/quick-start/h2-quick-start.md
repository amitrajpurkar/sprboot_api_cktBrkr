# H2 Database Quick Start Guide

## 🚀 Getting Started

### Run the Application
```bash
./gradlew bootRun
```

That's it! The application starts with H2 database automatically initialized with sample data.

---

## 🗄️ Access H2 Console

**URL:** http://localhost:8080/h2-console

**Login Credentials:**
- **JDBC URL:** `jdbc:h2:mem:sampledb`
- **Username:** `sa`
- **Password:** (leave empty)

Click **Connect** to access the database console.

---

## 📊 Sample Data Available

### Products Table (10 items)
- Scooby Doo series toys (5 items)
- Cars series toys (5 items)

### Insurance Members Table (1 member)
- John Deer with 3 policies and 6 plans

---

## 🔍 Useful SQL Queries

### View All Products
```sql
SELECT * FROM PRODUCTS;
```

### View All Members
```sql
SELECT * FROM INSURANCE_MEMBERS;
```

### View Member Policies
```sql
SELECT * FROM MEMBER_POLICIES;
```

### View Policy Plans
```sql
SELECT * FROM POLICY_PLANS;
```

### Search Products by Name
```sql
SELECT * FROM PRODUCTS WHERE NAME LIKE '%scooby%';
```

---

## 🧪 Run Tests

```bash
./gradlew test
```

All repository tests now pass automatically!

---

## 🐳 Docker Deployment

```bash
# Build and start
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

---

## 📝 Key Features

✅ **No Setup Required** - Database is embedded  
✅ **Auto-Initialized** - Sample data loaded on startup  
✅ **Web Console** - Inspect data via browser  
✅ **Fast Tests** - No external dependencies  
✅ **Self-Contained** - Perfect for demos  

---

## 🔗 API Endpoints

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health
- **H2 Console:** http://localhost:8080/h2-console

---

## 📚 More Information

See [H2_MIGRATION.md](src/main/docs/H2_MIGRATION.md) for complete migration details.
