# PAP-SHOP - Setup Guide

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0+
- Gradle 8.x
- Google Gemini API Key

## 🔧 Configuration Setup

### 1. Database Configuration (MySQL)

Create a MySQL database:
```sql
CREATE DATABASE papshop;
```

Set environment variables:
```bash
# Windows CMD
set DB_URL=jdbc:mysql://localhost:3306/papshop
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password

# Windows PowerShell
$env:DB_URL="jdbc:mysql://localhost:3306/papshop"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"

# Linux/macOS
export DB_URL=jdbc:mysql://localhost:3306/papshop
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
```

### 2. JWT Secret Key

Generate a secure random string (minimum 32 characters):
```bash
# Windows
set JWT_SECRET=your_super_secret_jwt_key_min_32_chars

# Linux/macOS
export JWT_SECRET=your_super_secret_jwt_key_min_32_chars
```

### 3. Email Configuration (Gmail)

**Step 1:** Enable 2-Factor Authentication in your Gmail account

**Step 2:** Generate App Password:
- Go to: https://myaccount.google.com/apppasswords
- Select "Mail" and "Other (Custom name)"
- Copy the 16-character password

**Step 3:** Set environment variables:
```bash
# Windows
set MAIL_USERNAME=your_email@gmail.com
set MAIL_PASSWORD=your_16_char_app_password

# Linux/macOS
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_16_char_app_password
```

### 4. Cloudinary Configuration

**Step 1:** Create free account at https://cloudinary.com

**Step 2:** Get credentials from Dashboard

**Step 3:** Set environment variables:
```bash
# Windows
set CLOUDINARY_CLOUD_NAME=your_cloud_name
set CLOUDINARY_API_KEY=your_api_key
set CLOUDINARY_API_SECRET=your_api_secret

# Linux/macOS
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
```

### 5. Google Gemini API (for AI Chatbot)

**Step 1:** Get API key from https://aistudio.google.com/app/apikey

**Step 2:** Set environment variable:
```bash
# Windows
set GOOGLE_AI_API_KEY=AIzaSy...your_key_here

# Linux/macOS
export GOOGLE_AI_API_KEY=AIzaSy...your_key_here
```

## 🚀 Running the Application

### Option 1: Using Gradle
```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun
```

### Option 2: Using JAR
```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/pap-shop-0.0.1-SNAPSHOT.jar
```

## 📝 Alternative: Using .env file (Recommended)

Create a `.env` file in project root:
```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/papshop
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# JWT
JWT_SECRET=your_super_secret_jwt_key_min_32_chars

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_16_char_app_password

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Google AI
GOOGLE_AI_API_KEY=AIzaSy...your_key_here
```

Then load it before running:
```bash
# Windows (PowerShell)
Get-Content .env | ForEach-Object { $var = $_.Split('='); [Environment]::SetEnvironmentVariable($var[0], $var[1]) }

# Linux/macOS
export $(cat .env | xargs)
```

## 🔍 Verify Configuration

After starting the application, check:

1. **Application started successfully:**
   ```
   Started PapShopApplication in X.XXX seconds
   ```

2. **Database connected:**
   ```
   HikariPool-1 - Start completed.
   ```

3. **Swagger UI accessible:**
   - http://localhost:8080/swagger-ui.html

4. **AI Chatbot initialized:**
   ```
   >>> RAG ingestion completed
   ```

## 🐛 Troubleshooting

### Database Connection Error
```
Error: Communications link failure
```
**Solution:** Check MySQL is running and credentials are correct

### JWT Error
```
Error: JWT secret key is required
```
**Solution:** Set JWT_SECRET environment variable

### Email Error
```
Error: Authentication failed
```
**Solution:** Use App Password, not regular Gmail password

### Cloudinary Error
```
Error: Invalid cloud name
```
**Solution:** Verify Cloudinary credentials from dashboard

### Gemini API Error
```
Error: API key not valid
```
**Solution:** Get new API key from https://aistudio.google.com/app/apikey

## 📚 API Documentation

Once running, access:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs

## 🎯 Key Features

- ✅ User Authentication (JWT)
- ✅ Product Management
- ✅ Shopping Cart
- ✅ Order Processing
- ✅ Image Upload (Cloudinary)
- ✅ Email Notifications
- ✅ AI Chatbot (Semantic Search + RAG)
- ✅ Product Reviews
- ✅ Real-time Announcements (WebSocket)

## 📖 Additional Documentation

- [README.md](README.md) - Project overview

## 🔗 Reference Links

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Google Gemini API](https://ai.google.dev/docs)
* [Cloudinary Documentation](https://cloudinary.com/documentation)

