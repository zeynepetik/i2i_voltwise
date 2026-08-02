# Maven + JDK 17 içeren imajla projeyi derliyoruz.
# Bu stage'in katmanları sadece build sırasında kullanılır, final imaja taşınmaz.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Önce sadece pom.xml'i kopyalayıp dependency'leri indiriyoruz.
# Böylece sadece kaynak kod değiştiğinde (pom.xml aynı kaldığında),
# Docker layer cache'i dependency indirme adımını atlar — build çok hızlanır.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Şimdi kaynak kodu kopyalayıp derliyoruz.
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Runtime ---
# Sadece JRE içeren, JDK/Maven içermeyen hafif bir imaj.
# Final imajda derleme araçlarının bulunmasına gerek yok, imaj boyutunu küçültür.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Build stage'inden sadece derlenmiş JAR'ı alıyoruz.
COPY --from=build /app/target/*.jar app.jar

# Spring Boot varsayılan olarak container dışına açık bir HTTP portu sunmuyor
# (bu servis sadece Kafka client'ı, REST endpoint yok), yine de Actuator
# eklediyseniz varsayılan 8080'i açık bırakmak zarar vermez.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]