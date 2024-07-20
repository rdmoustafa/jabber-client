# Jabber Client

This project is the client side for the Jabber social media application. It is implemented using JavaFX and provides a graphical user interface for interacting with the backend services.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Features

- User registration and login
- Create, read, update, and delete messages
- Like and unlike messages
- JavaFX-based graphical user interface

## Requirements

- Java JDK 18
- JavaFX SDK

## Installation

1. **Clone the repository:**
    ```bash
    git clone https://github.com/rdmoustafa/jabber-client.git
    cd jabber-client
    ```

2. **Set up JavaFX:**
   - Download the JavaFX SDK from the [official website](https://openjfx.io/).
   - Extract the SDK and set up the environment variables or paths according to your development setup.

3. **Compile the project:**
    ```bash
    javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/**/*.java
    ```

4. **Run the application:**
    ```bash
    java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin Client
    ```

## Usage

After running the application, a graphical user interface will appear. You can use this interface to interact with the backend services, including:

- Registering an account
- Logging in
- Posting messages
- Liking messages

## License

This project is licensed under the MIT License.

---

Thank you for checking out the Jabber Client project!
