Świetnie 🎉 — czyli faktycznie chodziło o **Spotify Developer Dashboard / Users and Access**!
To jest bardzo częsta pułapka przy integracjach Spotify, więc dobrze, że to już działa 💪

Poniżej masz gotową sekcję **README.md**, którą możesz wkleić na koniec swojego projektu.
Zawiera opis konfiguracji, uruchomienia i najczęstszych problemów (w tym ten z „Forbidden”).

---

```markdown
---

## 🎧 Spotify Stats App — README

### 📖 Opis projektu
Spotify Stats App to aplikacja webowa, która pozwala użytkownikom zalogować się na konto Spotify i zobaczyć swoje:
- najczęściej słuchane utwory (`Top Tracks`)
- najczęściej słuchanych artystów (`Top Artists`)
- statystyki dla różnych okresów czasu (`short_term`, `medium_term`, `long_term`)

Backend napisany jest w **Spring Boot (Java 21+)**, frontend w **React (Vite + TypeScript)**.

---

### 🧩 Technologie

#### Backend
- Spring Boot 3.x
- Spring Data JPA + PostgreSQL
- Spotify Web API (biblioteka `se.michaelthelin.spotify`)
- Maven

#### Frontend
- React + TypeScript
- Tailwind CSS
- Vite

---

### ⚙️ Konfiguracja środowiska

#### 1. Klucze Spotify API
1. Wejdź na [Spotify Developer Dashboard](https://developer.spotify.com/dashboard/).
2. Utwórz nową aplikację (lub użyj istniejącej).
3. Skopiuj:
   - `Client ID`
   - `Client Secret`
4. W polu **Redirect URIs** dodaj dokładnie:
```

[http://127.0.0.1:8080/api/get-user-code](http://127.0.0.1:8080/api/get-user-code)

````
5. W sekcji **Users and Access** dodaj wszystkie konta Spotify, które mają mieć dostęp (do 25 w trybie development).

---

#### 2. Plik `application.properties`

Ustaw zmienne konfiguracyjne backendu:

```properties
server.port=8080

# URL backendu (do redirect_uri)
redirect.server.ip=http://127.0.0.1:8080

# URL frontendu (do przekierowań po logowaniu)
custom.server.ip=http://127.0.0.1:5173

# Spotify API credentials
app.api.key=TWÓJ_CLIENT_ID
app.api.secret=TWÓJ_CLIENT_SECRET

# Baza danych
spring.datasource.url=jdbc:postgresql://localhost:5432/spotify_stats
spring.datasource.username=postgres
spring.datasource.password=haslo
spring.jpa.hibernate.ddl-auto=update
````

---

#### 3. Uruchomienie aplikacji

W terminalu (Windows):

```bash
# Uruchom backend (Spring Boot)
mvnw spring-boot:run

# Uruchom frontend (React)
cd frontend
npm install
npm run dev
```

Aplikacja backendowa działa na porcie `8080`, frontend na `5173`, w Dockerze frontend na 3000

---

### Działanie aplikacji

1. Użytkownik wchodzi na stronę logowania (frontend).
2. Po kliknięciu "Zaloguj przez Spotify" następuje przekierowanie do `https://accounts.spotify.com/authorize`.
3. Spotify po autoryzacji przekierowuje na:

   ```
   http://127.0.0.1:8080/api/get-user-code?code=...
   ```
4. Backend wymienia `code` na `access_token` i `refresh_token`, zapisuje użytkownika do bazy, i przekierowuje na frontend:

   ```
   http://127.0.0.1:5173/home?userId=SPOTIFY_USER_ID
   ```
5. Użytkownik może teraz przeglądać swoje statystyki.

---

### Najczęstsze problemy

| Problem                                      | Przyczyna                                     | Rozwiązanie                                                                                |
| -------------------------------------------- | --------------------------------------------- | ------------------------------------------------------------------------------------------ |
| `Spotify forbidden for this user: Forbidden` | Konto nie dodane do aplikacji                 | Wejdź w Spotify Dashboard → „Users and Access” → dodaj adres e-mail użytkownika            |
| `Invalid redirect_uri`                       | Błędny redirect URI                           | Upewnij się, że w Dashboardzie i w `application.properties` redirect URI są **identyczne** |
| `404 Not Found` przy `home`                  | Frontend nie działa w Dockerze / innym porcie | Sprawdź `custom.server.ip`                                                                 |
| `JAVA_HOME not defined`                      | Brak konfiguracji JDK w PATH                  | Ustaw `JAVA_HOME=C:\Users\<user>\.jdks\<jdk-version>` i dodaj `\bin` do PATH               |

---


---

### Struktura projektu

```
spotify_stats/
├── api/
│   ├── config/
│   │   └── SpotifyConfiguration.java
│   ├── controller/
│   │   └── ApiController.java
│   ├── service/
│   │   ├── UserProfileService.java
│   │   ├── UserTopArtistsService.java
│   │   └── UserTopTracksService.java
│   ├── dto/
│   │   ├── SongDTO.java
│   │   └── ArtistDTO.java
│   ├── persistence/
│   │   ├── entity/
│   │   │   └── UserDetails.java
│   │   └── repository/
│   │       └── UserDetailsRepository.java
├── frontend/
│   └── (plik Vite/React)
└── pom.xml
```

---

Autor: Piotr Cholewicki
Repozytorium: [https://github.com/PiotrCholewicki/Spotify_app


