@echo off
title EventForge Launcher

echo ================================
echo   EventForge Kafka Lab
echo ================================
echo.

echo Checking Kafka container...

docker ps --filter "name=eventforge-kafka" --filter "status=running" --format "{{.Names}}" | findstr "eventforge-kafka" >nul

if errorlevel 1 (
    echo Kafka is not running.
    echo Starting Docker Compose...
    docker compose up -d
    timeout /t 5 >nul
)

echo.
echo Creating topic order.created if needed...

docker exec eventforge-kafka ^
  /opt/kafka/bin/kafka-topics.sh ^
  --create ^
  --if-not-exists ^
  --topic order.created ^
  --bootstrap-server localhost:9092 ^
  --partitions 3 ^
  --replication-factor 1

echo.
echo Starting consumers...

start "Consumer A - Inventory" cmd /k ^
docker exec -it eventforge-kafka ^
/opt/kafka/bin/kafka-console-consumer.sh ^
--topic order.created ^
--bootstrap-server localhost:9092 ^
--group inventory-group

start "Consumer B - Inventory" cmd /k ^
docker exec -it eventforge-kafka ^
/opt/kafka/bin/kafka-console-consumer.sh ^
--topic order.created ^
--bootstrap-server localhost:9092 ^
--group inventory-group

start "Consumer C - Analytics" cmd /k ^
docker exec -it eventforge-kafka ^
/opt/kafka/bin/kafka-console-consumer.sh ^
--topic order.created ^
--bootstrap-server localhost:9092 ^
--group analytics-group

echo Starting producer...

start "Producer" cmd /k ^
docker exec -it eventforge-kafka ^
/opt/kafka/bin/kafka-console-producer.sh ^
--topic order.created ^
--bootstrap-server localhost:9092

echo.
echo EventForge is ready!
echo.
echo Topic:
echo   order.created
echo.
echo Consumer groups:
echo   Consumer A + B = inventory-group
echo   Consumer C     = analytics-group
echo.
pause