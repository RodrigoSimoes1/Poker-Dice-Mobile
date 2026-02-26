# Poker Dice - Mobile Client

This repository contains the native Android mobile application for the Poker Dice ecosystem. The application is built with Kotlin and Jetpack Compose, providing a modern interface for the multiplayer gaming platform.

## Project Context

This project was developed for the **PDM** (Mobile Device Programming) course. It serves as a mobile client that requires the backend services provided by the API developed for the **DAW** (Web Applications Development) course. The system demonstrates a distributed architecture where the mobile client integrates with a dedicated web service to enable multiplayer functionality.

## Prerequisites

This application is a client-side project and **cannot function standalone**. It requires the [Poker Dice API (DAW)](https://github.com/RodrigoSimoes1/Poker-Dice-Web) to be up and running.

* The backend must be accessible via the network.
* Ensure all database migrations and server configurations are completed before launching the mobile app.

## Technical Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Network Client:** Ktor
* **Concurrency:** Kotlin Coroutines
* **Serialization:** Kotlinx Serialization
* **Real-time Communication:** Server-Sent Events (SSE)

## Key Features

* Secure authentication and user session management.
* Dynamic lobby system for multiplayer matchmaking.
* Real-time game state synchronization via SSE.
* Complete game logic integration, including dice rolling and match status updates.
* User balance and profile management.

## Environment Setup

The project uses a secure configuration system for API endpoints. To run the application:

1. Create a `local.properties` file in the project root.
2. Define the base URL of your API:
   ```properties
   API_BASE_URL=[https://your-api-endpoint.com](https://your-api-endpoint.com)