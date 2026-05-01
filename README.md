# 🩸 BloodLink — Java Servlet Backend

## Project Structure

```
bloodlink-backend/
├── pom.xml                          ← Maven config & dependencies
├── database.sql                     ← Run this in MySQL first!
└── src/main/java/com/bloodlink/
    ├── util/
    │   ├── DBConnection.java        ← MySQL connection
    │   └── JsonUtil.java            ← JSON response helpers
    ├── model/
    │   ├── User.java
    │   ├── Donor.java
    │   └── BloodRequest.java
    ├── dao/
    │   ├── UserDAO.java             ← DB queries for users
    │   ├── DonorDAO.java            ← DB queries for donors
    │   └── BloodRequestDAO.java     ← DB queries for requests
    └── servlet/
        ├── AuthServlet.java         ← /api/auth/*
        ├── DonorServlet.java        ← /api/donors/*
        ├── BloodRequestServlet.java ← /api/requests/*
        └── AdminServlet.java        ← /api/admin/*
```

---

## ⚙️ Setup Steps

### 1. Install Required Tools
- [Java JDK 11+](https://adoptium.net/)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Apache Tomcat 10](https://tomcat.apache.org/download-10.cgi)
- [MySQL 8](https://dev.mysql.com/downloads/)

---

### 2. Setup MySQL Database

Open MySQL and run:
```sql
SOURCE /path/to/bloodlink-backend/database.sql;
```

Then open `src/main/java/com/bloodlink/util/DBConnection.java`
and update your credentials:
```java
private static final String DB_USER = "root";       // your MySQL username
private static final String DB_PASS = "password";   // your MySQL password
```

---

### 3. Build the Project

Open terminal inside `bloodlink-backend/` folder:
```bash
mvn clean package
```

This creates: `target/bloodlink.war`

---

### 4. Deploy to Tomcat

Copy the WAR file into Tomcat's webapps folder:
```bash
cp target/bloodlink.war /path/to/apache-tomcat-10/webapps/
```

Start Tomcat:
```bash
# On Windows:
C:\tomcat\bin\startup.bat

# On Mac/Linux:
/opt/tomcat/bin/startup.sh
```

Backend runs at: `http://localhost:8080/bloodlink`

---

### 5. Run Frontend

Open `bloodlink/` (your HTML files) with Live Server on VS Code.
Frontend runs at: `http://127.0.0.1:5500`

CORS is already configured — frontend & backend can talk to each other.

---

## 🌐 API Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/logout` | Logout |
| GET  | `/api/auth/me` | Get current user |

### Donors
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/donors/search?bloodGroup=A%2B&city=Mumbai` | Search donors |
| GET | `/api/donors/{id}` | Get donor profile |
| GET | `/api/donors/me` | My donor profile |
| PUT | `/api/donors/availability` | Toggle availability |
| DELETE | `/api/donors/{id}` | Delete donor (admin) |

### Blood Requests
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/requests` | Get active requests |
| GET | `/api/requests/all` | Get all requests (admin) |
| POST | `/api/requests` | Post new request |
| PUT | `/api/requests/{id}` | Update status |

### Admin
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/admin/stats` | Dashboard stats |
| GET | `/api/admin/donors` | All donors |
| GET | `/api/admin/requests` | All requests |

---

## 🧪 Test the API

Test login with curl:
```bash
curl -X POST http://localhost:8080/bloodlink/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"rahul@email.com","password":"password123"}'
```

Search donors:
```bash
curl "http://localhost:8080/bloodlink/api/donors/search?bloodGroup=A%2B&city=Mumbai"
```

---

## 🔗 Connect Frontend to Backend

In your HTML files, replace the hardcoded `donorsData` array with a real API call:

```javascript
// Example: fetch donors from backend in find-donor.html
const BASE_URL = 'http://localhost:8080/bloodlink';

async function searchDonors() {
  const bg   = document.getElementById('bgFilter').value;
  const city = document.getElementById('cityFilter').value;
  const res  = await fetch(`${BASE_URL}/api/donors/search?bloodGroup=${encodeURIComponent(bg)}&city=${city}`);
  const data = await res.json();
  renderDonors(data.donors);
}
```
