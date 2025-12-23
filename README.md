# Elanik Report Studio

![Java](https://img.shields.io/badge/Java-21%2B-blue.svg)
![Build](https://img.shields.io/badge/build-Maven-green)
![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20Cross--platform-lightgrey)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

**A professional JavaFX desktop application for generating structured PDF/HTML reports from measurement data.**

---

## Motivation & Use Cases

Elanik Report Studio automates and professionalizes the reporting process for measurement data in technical, scientific, and industrial environments. It is ideal for:
- Metallurgy labs and foundries
- Quality control departments
- Research and academic projects
- Any workflow requiring structured, multilingual, and portable reports

This project demonstrates your ability to solve real-world problems with robust, maintainable, and user-friendly software.

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

## Prerequisites

- Java JDK 21 or newer
- JavaFX 21 SDK (controls, fxml, graphics, base)
- Maven 3.6+
- jpackage (included with JDK 14+)

---

## Installation & Build Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/elanik-report-studio.git
   cd elanik-report-studio
   ```
2. Build a fat JAR:
   ```bash
   mvn clean package
   # Output: target/elanik-report-studio-all.jar
   ```
3. Run the application:
   ```bash
   java -jar target/elanik-report-studio-all.jar
   ```
4. (Optional) Package as a Windows EXE using jpackage:
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

---

## FAQ / Troubleshooting

**The app doesn't start or shows a JavaFX error:**
- Ensure you are using JDK 21+ and the correct JavaFX version. Check your `pom.xml` and local Maven repository for matching versions.

**The EXE doesn't open or crashes:**
- Run with `--win-console` to see error messages. Make sure all required JavaFX modules are included in the `--add-modules` argument.

**Fonts or non-Latin characters are missing in the PDF:**
- Add the required TTF fonts (e.g., DejaVu, Roboto) to `src/main/resources/fonts`.

**How do I add a new language?**
- Add a new `messages_xx.properties` file in `src/main/resources/lang` and update the language selection logic.

---

## Roadmap / Planned Features
- Add unit and integration tests for core services
- Implement CI/CD with GitHub Actions
- Add macOS/Linux packaging (DMG, AppImage)
- Enhance UI with charts and advanced controls
- Expand localization (add more languages)
- User preferences and settings dialog
- Automatic update checker

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

## About

Professional JavaFX desktop application for generating structured, multilingual PDF/HTML reports from measurement data. Features modern UI, SQLite integration, advanced templating, and cross-platform packaging. Ideal for labs, quality control, and research.

---


