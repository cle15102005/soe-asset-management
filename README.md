# SOE Asset Management System

## Prerequisites
* Node.js (v18+)
* Docker Desktop
* Java 17+ & Maven

## How to run locally

**1. Start the Database**
```bash
docker-compose up -d
```

**2. Start the Frontend**
```bash
cd frontend
npm install
npm run dev
```

**3. Start the Backend**
Open the `backend` folder in IntelliJ IDEA and run `SoeAssetManagementApplication.java`.