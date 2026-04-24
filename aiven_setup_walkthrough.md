# 🚀 Aiven MySQL Setup – Step-by-Step Visual Guide

Follow these steps exactly to move your database from your computer to the cloud.

---

### 1. Create Your Account
1.  Go to [aiven.io](https://aiven.io/).
2.  Click the **"Get started for free"** button (top right).
3.  Sign up using your **Google account** (easiest) or your Email.
4.  If it asks you to "Create a Project," just name it **"FarmFeed-Project"** and click Create.

---

### 2. Create the MySQL Service
1.  On your Dashboard, click the big **"Create service"** button.
2.  **Select Service Type**: Click on the **MySQL** icon.
3.  **Select Cloud Provider**: Click on **Google Cloud** (GCP) or **AWS**.
4.  **Select Region**: Look for a region that has a **"Free plan available"** label (e.g., *google-europe-west1* or *aws-us-east-1*). **You MUST select a region with a Free label to avoid being charged.**
5.  **Select Service Plan**: Scroll down to the **"Free Plan"** box and click it. It will say "$0.00/month."
6.  **Name your service**: At the bottom, change the name to **`farmfeed-db`**.
7.  Click the **"Create service"** button.

---

### 3.  **Wait for "Running" Status**
*   Your service is now **Running** (Green circle).

---

### 4. Copy Your Connection Details
On your Aiven page, look for the **"Connection information"** section. You should see these exact details:

1.  **Host**: `farmfeed-db-vedantdivate2506-e780.i.aivencloud.com`
2.  **Port**: `19512`
3.  **User**: `avnadmin`
4.  **Password**: `AVNS_Tel6xlP-9aWPBxKt61b`
5.  **Database name**: `defaultdb`

---

### 5. Connect via MySQL Workbench
1.  Open **MySQL Workbench** on your computer.
2.  Click the **"+"** icon next to "MySQL Connections."
3.  **Connection Name**: Type **`Aiven-Cloud-DB`**.
4.  **Hostname**: Paste `farmfeed-db-vedantdivate2506-e780.i.aivencloud.com`.
5.  **Port**: Change this to **`19512`**.
6.  **Username**: Type **`avnadmin`**.
7.  Click **"Store in Vault..."** and paste your **Aiven Password**.
8.  Click **"Test Connection."** 
    *   If it says "Successfully made the MySQL connection," click **OK**.

---

### 6. Import Your Tables (Final Step)
1.  Double-click your new **"Aiven-Cloud-DB"** connection to open it.
2.  Go to the top menu: **File** -> **Open SQL Script**.
3.  Select your local `FarmFeed` SQL file (the one that creates your tables).
4.  Click the **Lightning Bolt icon** ⚡ to run the script.
5.  Your tables (farmer, vendor, admin, etc.) are now live in the cloud!

---

**Next Stop**: Once this is done, you are ready for **Step 2: Backend Deployment**!
