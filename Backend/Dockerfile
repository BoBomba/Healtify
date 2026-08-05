# Stary Dockerfile pakowal jar, ktory juz musial byc wczesniej zbudowany recznie
# (np. przez `mvn package` na hoscie) i wrzucany do obrazu jaki jest w target/.
# Problem: jesli ktos zapomnial przebudowac jar przed `docker compose build`,
# obraz cicho pakowal STARY kod bez zadnego bledu builda (tak sie realnie stalo -
# jar mial usuniete juz z repo klasy jak Activity/NutritionLog/WaterIntake).
#
# FROM openjdk:21
# FROM eclipse-temurin:21-jdk
#
# ADD healtify/target/*.jar application.jar
#
# ENTRYPOINT ["java", "-jar", "application.jar"]

# --- Etap 1: build ---
# Kompiluje aplikacje ze zrodel wewnatrz obrazu, wiec jar zawsze odpowiada
# aktualnemu kodowi w repo, niezaleznie od tego co lezy lokalnie w target/.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Najpierw same pliki potrzebne do pobrania zaleznosci (cache warstwy Dockera:
# zmiana kodu zrodlowego nie wymusza ponownego pobierania zaleznosci Mavena).
COPY healtify/mvnw ./
COPY healtify/.mvn .mvn
COPY healtify/pom.xml ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

# Teraz kod zrodlowy i wlasciwy build jara (testy pomijamy, docker to nie CI).
COPY healtify/src src
RUN ./mvnw -q clean package -DskipTests

# --- Etap 2: runtime ---
# Obraz koncowy zawiera tylko JRE/JDK i gotowy jar ze stage'u build, bez
# Mavena i zrodel, wiec jest mniejszy.
FROM eclipse-temurin:21-jdk

COPY --from=build /app/target/*.jar application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]
