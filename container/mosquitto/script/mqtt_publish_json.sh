#!/bin/sh

set -e

TOPIC=$1

cities="Seoul Busan Tokyo Osaka Beijing Shanghai"

# Infinite Loop
while true; do
    # Generate random temperature (-10 to 50)
    temperature=$(awk 'BEGIN { srand(); printf "%.1f", -10 + rand() * 60 }')

    # Generate random humidity (0 to 100)
    humidity=$(awk 'BEGIN { srand(); printf "%.1f", rand() * 100 }')

    # Pick a random city (필요하시면 사용하세요~)
    #city=${cities[$RANDOM % ${#cities[@]}]}

    city=$(echo $cities | tr ' ' '\n' | shuf -n 1)

    # Timestamp
    timestamp=$(TZ=Asia/Seoul date -Iseconds)

    # Create JSON message
    message="{\"type\":\"sensor\",\"city\":\"$city\",\"temperature\":$temperature,\"humidity\":$humidity,\"timestamp\":\"$timestamp\"}"

    mosquitto_pub -h localhost -t "$TOPIC" -m "$message"

    echo "Sent Message [ $message ] (TOPIC: $TOPIC)"

    #sleep random time between 0 ~ 3 seconds
    sleep_time=$(awk -v min=0 -v max=3 'BEGIN { srand(); print min + rand() * (max - min)}')
    sleep "$sleep_time"
done
