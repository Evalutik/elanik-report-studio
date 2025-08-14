# Build Instructions

## 1. Prerequisites

Before building the application, make sure you have:

- **Java JDK 20** (or your development version)
- **JavaFX 20 SDK** (modules: `controls`, `fxml`, `graphics`, `base`)
- **Maven** (for building the fat JAR)
- **IntelliJ IDEA** (optional, for project setup and inspection)
- **jpackage** (included with JDK 14+)

> **Tip:** On Windows, the JDK is usually under:
>
> ```
> C:\Program Files\Java\jdk-20
> ```
>
> In IntelliJ, check: `File → Project Structure → SDKs`.

---

## 2. JavaFX Setup

JavaFX modules are downloaded via Maven and located in your local Maven repository:
```
C:\Users<YourUser>.m2\repository\org\openjfx\
```

Example paths for Windows:
```
javafx-controls\20\javafx-controls-20-win.jar
javafx-fxml\20\javafx-fxml-20-win.jar
javafx-graphics\20\javafx-graphics-20-win.jar
javafx-base\20\javafx-base-20-win.jar
```
> **Tip:** Use the `-win.jar` versions. Ignore `-sources.jar` and `-javadoc.jar`.

---

## 3. Build a Fat JAR

1. Open IntelliJ IDEA → **Build → Build Artifacts**.
2. Select your main JAR artifact, e.g., `elanik-reporter-all.jar`.
3. Click **Build**.
4. The resulting JAR is located in:`<project-root>\target\elanik-reporter-all.jar`


> **Tip:** This JAR contains all your project classes and dependencies.

---

## 4. Create Windows EXE with jpackage

Open **PowerShell** and run:

```powershell
jpackage `
  --input target `
  --name ElanikReporter `
  --main-jar elanik-reporter-all.jar `
  --main-class com.example.App `
  --type exe `
  --win-console `
  --module-path "C:\Users\User\.m2\repository\org\openjfx\javafx-base\20\javafx-base-20-win.jar;C:\Users\User\.m2\repository\org\openjfx\javafx-controls\20\javafx-controls-20-win.jar;C:\Users\User\.m2\repository\org\openjfx\javafx-fxml\20\javafx-fxml-20-win.jar;C:\Users\User\.m2\repository\org\openjfx\javafx-graphics\20\javafx-graphics-20-win.jar" `
  --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics,java.sql
```
Explanation:
- `--input target` → folder with your JAR
- `--name ElanikReporter` → name of the EXE and program
- `--main-jar elanik-reporter-all.jar` → main JAR file
- `--main-class com.example.App` → fully qualified main class
- `--type exe` → Windows executable
- `--win-console` → shows console for logs/errors
- `--module-path` → paths to JavaFX modules
- `--add-modules` → all required modules, including java.sql

---

## 5. EXE Output

After running jpackage, the EXE is located in:

`<project-root>\ElanikReporter\ElanikReporter.exe`

> **Tip:** By default, jpackage creates a small installer folder. You can move it anywhere, e.g., C:\Program Files\ElanikReporter.

---

## 6. Debugging

If the EXE doesn’t open:
Use `--win-console` to see error messages in PowerShell.
Ensure all JavaFX modules and java.sql are included in `--add-modules`.
Verify the main class and JAR path.

---

## 7. Tips & Common Issues

Always use the `-win.jar` versions of JavaFX on Windows.
Keep project structure clean: target/ for build artifacts, .m2/ for Maven dependencies.
If upgrading Java or JavaFX, update `--module-path`.
EXE bundles a runtime, so end users don’t need Java installed.
