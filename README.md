# Atipera Task

Aplikacja Spring Boot służąca do pobierania informacji o publicznych repozytoriach użytkowników GitHub (z wyłączeniem forków) wraz z danymi o ich gałęziach (branches).

## Technologie
* Java 25
* Spring Boot 4.0.6
* Gradle

## Wymagania
Do uruchomienia projektu wymagane jest zainstalowane środowisko **JDK 25**.

## Uruchomienie
1. Sklonuj repozytorium lub pobierz pliki projektu.
2. Przejdź do katalogu głównego projektu.
3. Uruchom aplikację za pomocą Gradle:

   W systemie Windows:
   ```bash
   .\gradlew.bat bootRun
   ```

   W systemie Linux/macOS:
   ```bash
   ./gradlew bootRun
   ```

Aplikacja domyślnie uruchamia się na porcie 8080.

## Użycie
Aplikacja udostępnia jeden główny punkt końcowy (endpoint):

**GET** `/repo/{username}`

Przykład:
`http://localhost:8080/repo/octocat`

## Format odpowiedzi
W przypadku sukcesu (HTTP 200), aplikacja zwraca listę repozytoriów w formacie JSON:

```json
[
  {
    "repoName": "hello-world",
    "ownerLogin": "octocat",
    "branches": [
      {
        "branchName": "main",
        "lastCommitSha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"
      }
    ]
  }
]
```

## Obsługa błędów
Jeśli użytkownik nie zostanie znaleziony (HTTP 404), aplikacja zwróci odpowiedź w formacie:

```json
{
  "status": 404,
  "message": "GitHub username 'octocat' not found."
}
```
