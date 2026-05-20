@echo off
REM Build and Push Docker Image to Docker Hub

echo ========================================
echo Building and Pushing HR Application
echo ========================================

REM Load environment variables from .env file
for /f "tokens=1,2 delims==" %%a in (.env) do (
    if "%%a"=="DOCKER_USERNAME" set DOCKER_USERNAME=%%b
)

if "%DOCKER_USERNAME%"=="" (
    echo ERROR: DOCKER_USERNAME not found in .env file
    exit /b 1
)

echo Docker Username: %DOCKER_USERNAME%
echo.

REM Step 1: Build the Docker image
echo [1/3] Building Docker image...
docker build -t %DOCKER_USERNAME%/hr-application:latest .
if errorlevel 1 (
    echo ERROR: Docker build failed
    exit /b 1
)
echo Docker image built successfully!
echo.

REM Step 2: Tag the image (optional, already tagged as latest)
echo [2/3] Tagging image...
docker tag %DOCKER_USERNAME%/hr-application:latest %DOCKER_USERNAME%/hr-application:latest
echo Image tagged successfully!
echo.

REM Step 3: Push to Docker Hub
echo [3/3] Pushing image to Docker Hub...
docker push %DOCKER_USERNAME%/hr-application:latest
if errorlevel 1 (
    echo ERROR: Docker push failed. Make sure you are logged in with 'docker login'
    exit /b 1
)
echo Image pushed successfully!
echo.

echo ========================================
echo Build and Push Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Deploy on server: docker compose -f docker-compose.prod.yml down
echo 2. Pull latest image: docker compose -f docker-compose.prod.yml pull
echo 3. Start services: docker compose -f docker-compose.prod.yml up -d
echo.
