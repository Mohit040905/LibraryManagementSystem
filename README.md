# Library Management System

A console-based **Library Management System** built with **Core Java, JDBC, and MySQL**. Demonstrates clean OOP design with a layered architecture (Model → DAO → Service → UI).

# Tech Stack

| Layer        | Technology              |
|--------------|-------------------------|
| Language     | Java 17                 |
| Database     | MySQL 8                 |
| DB Access    | JDBC (PreparedStatement)|
| Build Tool   | Maven                   |
| IDE          | VS Code / IntelliJ IDEA |

---

# Project Structure

```
LibraryManagementSystem/
├── src/main/java/com/library/
│   ├── Main.java               ← Entry point & console UI
│   ├── model/
│   │   ├── Book.java
│   │   ├── Member.java
│   │   └── IssueRecord.java
│   ├── dao/
│   │   ├── BookDAO.java
│   │   ├── MemberDAO.java
│   │   └── IssueRecordDAO.java
│   ├── service/
│   │   └── LibraryService.java ← Business logic & validation
│   └── util/
│       └── DBConnection.java   ← Singleton DB connection
├── src/main/resources/
│   ├── db.properties           ← DB credentials (not committed)
│   └── schema.sql              ← Database setup script
└── pom.xml
```

---

## ⚙️ Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/Mohit040905/LibraryManagementSystem.git
cd LibraryManagementSystem
```

### 2. Set Up the Database
Open MySQL and run the schema:
```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 3. Configure DB Credentials
Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=your_mysql_password
db.driver=com.mysql.cj.jdbc.Driver
```

### 4. Build with Maven
```bash
mvn clean package
```

### 5. Run
```bash
java -cp LibraryManagementSystem.jar:libs/* com.library.Main
# On Windows:
java -cp "LibraryManagementSystem.jar;libs/*" com.library.Main
```

---

## ✨ Features

- **Book Management** — Add, search (by title/author), view all, delete books
- **Member Management** — Register members, view all members
- **Issue & Return** — Issue books with 14-day due dates, return with overdue detection
- **Reports** — View all active issues, overdue books, member borrow history
- **Validations** — Prevents issuing unavailable books, duplicate issues, invalid inputs
- **SQL Injection Prevention** — All queries use `PreparedStatement`

---

## 🗄️ Database Schema

```sql
books          (book_id, title, author, genre, total_copies, available_copies, added_on)
members        (member_id, name, email, phone, joined_on)
issue_records  (record_id, book_id, member_id, issue_date, due_date, return_date, status)
```

---

## 👨‍💻 Author

**Mohit Morande**  
B.E. Information Technology — GHRCE, Nagpur  
[LinkedIn](https://www.linkedin.com/in/mohit-morande) | [GitHub](https://github.com/Mohit040905)
