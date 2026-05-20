# Docker Setup Guide for HR Application

This guide explains how to build, run, and deploy the Human Resource Application using Docker.

## Prerequisites

- Docker Desktop installed and running
- Docker Hub account (for pushing images)
- `.env` file configured with your environment variables

## Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Database Configuration
DB_URL=jdbc:mysql://mysql:3306/hr_database
DB_USERNAME=your_username
DB_PASSWORD=your_secure_password

# JPA/Hibernate Configuration
JPA_SHOW_SQL=true
JPA_HIBERNATE_DDL_AUTO=update

# Server Configuration
SERVER_PORT=8080

# Docker Hub Configuration (for pushing images)
DOCKER_USERNAME=your_dockerhub_username
```

## Local Development

### 1. Build and Run with Docker Compose

```bash
# Build and start all services (app, MySQL, Redis)
docker-compose up --build

# Run in detached mode
docker-compose up -d --build

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes database data)
docker-compose down -v
```

### 2. Access the Application

- Application: http://localhost:8080
- API Base Path: http://localhost:8080/api/v1
- MySQL: localhost:3306
- Redis: localhost:6379

## Building and Pushing to Docker Hub

### 1. Login to Docker Hub

```bash
docker login
```

Enter your Docker Hub username and password when prompted.

### 2. Build the Docker Image

```bash
# Build the image with your Docker Hub username
docker build -t your_dockerhub_username/hr-application:latest .

# Build with a specific version tag
docker build -t your_dockerhub_username/hr-application:v1.0.0 .
```

### 3. Push to Docker Hub

```bash
# Push the latest tag
docker push your_dockerhub_username/hr-application:latest

# Push a specific version
docker push your_dockerhub_username/hr-application:v1.0.0
```

### 4. Push Multiple Tags

```bash
# Tag the image with multiple versions
docker tag your_dockerhub_username/hr-application:latest your_dockerhub_username/hr-application:v1.0.0
docker tag your_dockerhub_username/hr-application:latest your_dockerhub_username/hr-application:stable

# Push all tags
docker push your_dockerhub_username/hr-application:latest
docker push your_dockerhub_username/hr-application:v1.0.0
docker push your_dockerhub_username/hr-application:stable
```

## Production Deployment

### Using Pre-built Image from Docker Hub

1. Update `.env` file with production values:
```env
DOCKER_USERNAME=your_dockerhub_username
DB_PASSWORD=strong_production_password
JPA_HIBERNATE_DDL_AUTO=validate
JPA_SHOW_SQL=false
```

2. Run with production compose file:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Useful Docker Commands

### Container Management

```bash
# List running containers
docker ps

# List all containers
docker ps -a

# Stop a specific container
docker stop hr_application

# Remove a container
docker rm hr_application

# View container logs
docker logs hr_application

# Follow logs in real-time
docker logs -f hr_application

# Execute commands in running container
docker exec -it hr_application sh
```

### Image Management

```bash
# List images
docker images

# Remove an image
docker rmi your_dockerhub_username/hr-application:latest

# Remove unused images
docker image prune

# Remove all unused images, containers, networks
docker system prune -a
```

### Database Management

```bash
# Connect to MySQL container
docker exec -it hr_mysql mysql -u root -p

# Backup database
docker exec hr_mysql mysqldump -u root -p hr_database > backup.sql

# Restore database
docker exec -i hr_mysql mysql -u root -p hr_database < backup.sql
```

### Redis Management

```bash
# Connect to Redis CLI
docker exec -it hr_redis redis-cli

# Check Redis keys
docker exec -it hr_redis redis-cli KEYS '*'

# Flush Redis cache
docker exec -it hr_redis redis-cli FLUSHALL
```

## Health Checks

All services include health checks:

```bash
# Check service health status
docker-compose ps

# Check application health endpoint
curl http://localhost:8080/actuator/health
```

## Troubleshooting

### Application won't start

1. Check if MySQL is healthy:
```bash
docker-compose logs mysql
```

2. Check application logs:
```bash
docker-compose logs app
```

3. Verify environment variables:
```bash
docker-compose config
```

### Database connection issues

1. Ensure MySQL is running and healthy
2. Check database credentials in `.env`
3. Verify network connectivity:
```bash
docker network inspect human_resource_application_hr_network
```

### Port conflicts

If ports are already in use, modify them in `.env`:
```env
SERVER_PORT=8081
```

Or map to different host ports in `docker-compose.yml`.

## Architecture

The Docker setup includes:

- **Application Container**: Spring Boot app running on Java 17
- **MySQL Container**: Database with persistent volume
- **Redis Container**: Caching layer with persistent volume
- **Bridge Network**: Allows containers to communicate
- **Health Checks**: Ensures services are ready before starting dependent services

## Security Notes

- Never commit `.env` files with real credentials
- Use strong passwords in production
- Consider using Docker secrets for sensitive data
- Run containers as non-root users (already configured)
- Keep base images updated regularly

## Performance Tuning

### Java Memory Settings

Adjust in `docker-compose.yml`:
```yaml
environment:
  JAVA_OPTS: "-Xmx1024m -Xms512m -XX:+UseG1GC"
```

### Database Optimization

Add MySQL configuration in `docker-compose.yml`:
```yaml
command: --max_connections=200 --innodb_buffer_pool_size=512M
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Build and Push Docker Image
  run: |
    docker build -t ${{ secrets.DOCKER_USERNAME }}/hr-application:${{ github.sha }} .
    docker push ${{ secrets.DOCKER_USERNAME }}/hr-application:${{ github.sha }}
```

## Support

For issues or questions:
1. Check application logs: `docker-compose logs app`
2. Verify environment configuration: `docker-compose config`
3. Review Docker documentation: https://docs.docker.com
