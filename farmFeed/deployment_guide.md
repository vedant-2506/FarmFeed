# FarmFeed – Deployment Guide

This guide details how to deploy the FarmFeed system to production using free-tier cloud tools.

---

## 🔹 Step 1: Database Setup (Aiven)
We will use **Aiven** for a managed MySQL database.

1.  **Create Account**: Go to [aiven.io](https://aiven.io/) and sign up.
2.  **Create Service**: Select **MySQL** and choose the **Free Plan** (available in specific regions).
3.  **Connection Details**: Once the service is running, copy the following:
    *   **Host**
    *   **Port** (usually 3306)
    *   **User** (usually `avnadmin`)
    *   **Password**
4.  **Import Schema**:
    *   Open MySQL Workbench on your local machine.
    *   Connect to the Aiven database using the details above.
    *   Run your local `FarmFeed` schema SQL to create the tables.

---

## 🔹 Step 2: Backend Deployment (Render)
We will deploy the Spring Boot application to **Render**.

1.  **Connect GitHub**: Go to [render.com](https://render.com/), create a **New Web Service**, and connect your repository: `https://github.com/vedant-2506/FarmFeed`.
2.  **Configure Build**:
    *   **Runtime**: Java
    *   **Build Command**: `./mvnw clean package -DskipTests`
    *   **Start Command**: `java -jar target/farmfeed-1.0.0.jar`
3.  **Environment Variables**: Add these in the "Environment" tab:
    *   `DB_URL` = `jdbc:mysql://[AIVEN_HOST]:[AIVEN_PORT]/defaultdb?useSSL=true`
    *   `DB_USER` = `avnadmin`
    *   `DB_PASS` = `[AIVEN_PASSWORD]`
    *   `PORT` = `8080`
4.  **Deploy**: Render will build the JAR and provide a URL (e.g., `https://farmfeed-backend.onrender.com`).

---

## 🔹 Step 3: Frontend Update
Update your frontend to point to the live backend.

1.  Open `src/main/resources/static/js/config.js`.
2.  Update the API URL:
    ```javascript
    const API_BASE_URL = "https://farmfeed-backend.onrender.com";
    ```
3.  **Commit and Push**:
    ```bash
    git add src/main/resources/static/js/config.js
    git commit -m "Update API URL for production"
    git push origin main
    ```

---

## 🔹 Step 4: Frontend Deployment (Netlify)
We will deploy the static files to **Netlify**.

1.  **New Site**: Go to [netlify.com](https://www.netlify.com/), click **Add New Site** → **Import from GitHub**.
2.  **Select Repo**: Choose `https://github.com/vedant-2506/FarmFeed`.
3.  **Configure Build**:
    *   **Base Directory**: (Leave blank)
    *   **Build Command**: (Leave blank)
    *   **Publish Directory**: `src/main/resources/static`
4.  **Deploy**: Netlify will provide your live website link (e.g., `https://farmfeed.netlify.app`).

---

## 🔹 Step 5: Final Production Links

| Link | Usage | URL |
| :--- | :--- | :--- |
| **Main Platform** | Farmers & Vendors | `https://[YOUR_NETLIFY_URL]/Home.html` |
| **Admin Panel** | Admin Only | `https://[YOUR_NETLIFY_URL]/AdminLogin.html` |

---
*Note: Ensure the backend is awake (Render free tier sleeps after 15 mins of inactivity) before testing the live site.*
