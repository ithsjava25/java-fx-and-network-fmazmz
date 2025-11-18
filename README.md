# JavaFX Chat App 💬

A JavaFX-based chat client using [ntfy](https://docs.ntfy.sh/) for backend messaging.

## Features
- MVC architecture
- Send text messages to configurable topic via [JSON POST](https://docs.ntfy.sh/publish/#publish-as-json)
- Receive messages via [JSON stream](https://docs.ntfy.sh/subscribe/api/)
- Backend URL via env variable (not committed)
- Branch + PR workflow (no direct commits to `main`)
- Unit tests for `Model` class
- (Advanced) Send files via "Attach local file" option


## Requirements

- **Java**
    - **Version**: `25`

- **Maven Compiler Plugin**
    - **Version**: `3.11.0`
    - **Configuration**:
        - **Release**: `25`

## Usage
1. Set `JAVA_HOME` to JDK 25.
2. Create a **.env** file with the required variables. You can also clone and fill **.env.example** and rename it to `.env`.
2. Start with:
   ```bash
   ./mvnw clean javafx:run
