# Elanik Report Studio

![Java](https://img.shields.io/badge/Java-21%2B-blue.svg)
![Build](https://img.shields.io/badge/build-Maven-green)
![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20Cross--platform-lightgrey)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

**A professional JavaFX desktop application for generating structured PDF/HTML reports from measurement data.**

---

## Motivation & Use Cases

Elanik Report Studio was created to automate and professionalize the reporting process for measurement data in technical, scientific, and industrial environments. It is ideal for:
- Metallurgy labs and foundries
- Quality control departments
- Research and academic projects
- Any workflow requiring structured, multilingual, and portable reports

By building this project, you demonstrate the ability to solve real-world problems with robust, maintainable, and user-friendly software.

---

## Technologies Used

| Technology         | Purpose                                 |
|-------------------|-----------------------------------------|
| Java 21+          | Core application logic                   |
| JavaFX            | Modern desktop UI                        |
| SQLite (sqlite-jdbc) | Local database storage                |
| FreeMarker        | HTML templating for reports              |
| OpenHTMLToPDF     | HTML to PDF conversion                   |
| Maven             | Build, dependency management             |
| jpackage          | Native Windows packaging                 |

---

## Table of Contents
- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Screenshots](#screenshots)
- [Prerequisites](#prerequisites)
- [Installation & Build Instructions](#installation--build-instructions)
- [Running the Application](#running-the-application)
- [Packaging as a Native Windows EXE](#packaging-as-a-native-windows-exe)
- [Project Structure](#project-structure)
- [Development Notes & Tips](#development-notes--tips)
- [Potential Improvements & Interview Talking Points](#potential-improvements--interview-talking-points)
- [License & Contact](#license--contact)

---

## Project Overview

Elanik Report Studio is a desktop application designed to streamline the process of generating, viewing, and exporting detailed measurement reports. It leverages a modern JavaFX interface, stores data in a local SQLite database, and produces professional-quality reports in both PDF and HTML formats using FreeMarker templates and OpenHTMLToPDF. The application supports both English and Russian, making it suitable for international use.

---

## Key Features
- **Modern JavaFX UI:** Intuitive, responsive interface for managing and browsing measurement data.
- **SQLite Database Integration:** Reliable, portable storage for all measurement records.
- **Advanced Report Generation:** Uses FreeMarker templates for flexible, maintainable report layouts.
- **PDF & HTML Export:** High-quality output with embedded fonts for multilingual support.
- **Internationalization:** English and Russian language support out of the box.
- **Cross-Platform Packaging:** Build a fat JAR or a native Windows installer (EXE) with all dependencies.
- **Extensible Architecture:** Clean separation of concerns (UI, services, models, utils) for easy maintenance and future growth.

---

## Screenshots
> _Add screenshots of the main UI and a sample PDF report here to further impress recruiters and reviewers._

---

## Prerequisites

Before building or running Elanik Report Studio, ensure you have the following installed:

- **Java JDK 21** (or your development version; minimum 20)
- **JavaFX 21 SDK** (modules: `controls`, `fxml`, `graphics`, `base`)
- **Maven 3.6+** (for dependency management and building the project)
- **IntelliJ IDEA** (optional, but recommended for development)
- **jpackage** (included with JDK 14+; for native packaging)

> **Tip:** On Windows, the JDK is usually under:
> `C:\Program Files\Java\jdk-21`
> In IntelliJ, check: `File → Project Structure → SDKs` and `File → Project Structure → Project → SDK`.

---

## Installation & Build Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/elanik-report-studio.git
cd elanik-report-studio
```

### 2. JavaFX Setup
JavaFX modules are downloaded via Maven and located in your local Maven repository:
`C:\Users<YourUser>\.m2\repository\org\openjfx\`

Example paths for Windows:
- `javafx-controls/21/javafx-controls-21-win.jar`
- `javafx-fxml/21/javafx-fxml-21-win.jar`
- `javafx-graphics/21/javafx-graphics-21-win.jar`
- `javafx-base/21/javafx-base-21-win.jar`

> **Tip:** Use the `-win.jar` versions. Ignore `-sources.jar` and `-javadoc.jar`.

### 3. Build a Fat JAR
You can build a self-contained JAR with all dependencies using Maven:
```bash
mvn clean package
```
The resulting JAR will be in `target/elanik-report-studio-all.jar`.

### 4. Run the Application
To run the application from the fat JAR:
```bash
java -jar target/elanik-report-studio-all.jar
```
Or, to run directly from Maven (useful for development):
```bash
mvn javafx:run
```

---

## Running the Application

After building, simply execute:
```bash
java -jar target/elanik-report-studio-all.jar
```
Or use your IDE's run configuration (main class: `com.example.App`).

---

## Packaging as a Native Windows EXE

To create a Windows installer using `jpackage`, run (in PowerShell):
```powershell
jpackage \
  --input target \
  --name ElanikReportStudio \
  --main-jar elanik-report-studio-all.jar \
  --main-class com.example.App \
  --type exe \
  --win-console \
  --module-path "C:\Users\<YourUser>\.m2\repository\org\openjfx\javafx-base\21\javafx-base-21-win.jar;C:\Users\<YourUser>\.m2\repository\org\openjfx\javafx-controls\21\javafx-controls-21-win.jar;C:\Users\<YourUser>\.m2\repository\org\openjfx\javafx-fxml\21\javafx-fxml-21-win.jar;C:\Users\<YourUser>\.m2\repository\org\openjfx\javafx-graphics\21\javafx-graphics-21-win.jar" \
  --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics,java.sql
```
The EXE will be located in `./ElanikReportStudio/ElanikReportStudio.exe`.

> **Tip:** Use `--win-console` to see error messages if the EXE doesn't open. Ensure all JavaFX modules and `java.sql` are included in `--add-modules`.

---

## Project Structure
- `src/main/java` — application source code
  - `com.example.App` — JavaFX entry point
  - `services/` — database loading, report generation
  - `models/` — domain objects (Measurement, Report, etc.)
  - `ui/` — controllers and FXML
  - `utils/` — DB connection, formatting, periodic table helper
- `src/main/resources` — FXML, templates, images, fonts, and localization bundles
- `pom.xml` — Maven configuration (plugins for JavaFX and shading)

---

## Development Notes & Tips
- FreeMarker templates are in `src/main/resources/templates` — modify these to change report layout.
- Fonts for PDF output are loaded from `src/main/resources/fonts` — add DejaVu/Roboto TTFs to support Cyrillic/UTF-8.
- If you see version mismatch warnings, align your JDK, Maven compiler plugin, and `javafx.version` in `pom.xml` (recommended: JDK 21 + JavaFX 21).
- Keep your project structure clean: `target/` for build artifacts, `.m2/` for Maven dependencies.
- The EXE bundles a runtime, so end users don’t need Java installed.

---

## Potential Improvements & Interview Talking Points
- Add unit/integration tests for `ReportService` and database access code.
- Implement a CI workflow to build the fat JAR and run static analysis.
- Add cross-platform packaging (macOS DMG, Linux AppImage) to demonstrate cross-platform delivery.
- Enhance the UI with more advanced JavaFX features (charts, drag-and-drop, etc.).
- Expand localization to additional languages.

---

## FAQ / Troubleshooting

**Q: The app doesn't start or shows a JavaFX error.**
A: Ensure you are using JDK 21+ and the correct JavaFX version. Check your `pom.xml` and local Maven repository for matching versions.

**Q: The EXE doesn't open or crashes.**
A: Run with `--win-console` to see error messages. Make sure all required JavaFX modules are included in the `--add-modules` argument.

**Q: Fonts or non-Latin characters are missing in the PDF.**
A: Add the required TTF fonts (e.g., DejaVu, Roboto) to `src/main/resources/fonts`.

**Q: How do I add a new language?**
A: Add a new `messages_xx.properties` file in `src/main/resources/lang` and update the language selection logic.

---

## Roadmap / Planned Features
- [ ] Add unit and integration tests for core services
- [ ] Implement CI/CD with GitHub Actions
- [ ] Add macOS/Linux packaging (DMG, AppImage)
- [ ] Enhance UI with charts and advanced controls
- [ ] Expand localization (add more languages)
- [ ] User preferences and settings dialog
- [ ] Automatic update checker

---

## Contributing

Contributions are welcome! To contribute:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes with clear messages
4. Push to your fork and open a Pull Request
5. Add tests for new features or bug fixes where possible

For major changes, please open an issue first to discuss what you would like to change.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Thanks for Reviewing!

Elanik Report Studio is a compelling, practical project to showcase in interviews and on your GitHub portfolio. It demonstrates your ability to deliver real-world, maintainable, and user-focused software—exactly what employers are looking for in a modern developer.

---


