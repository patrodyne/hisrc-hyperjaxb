# build.sh
This Bash script is a TUI (Terminal User Interface) wrapper for Apache Maven. It uses a tool called `whiptail` to provide a visual menu for managing Java/Maven projects.
Instead of typing long Maven commands, you pick an option from a list. Here is a breakdown of its main functions:
## 1. The Interactive Menu
Depending on your settings, it shows either a "Full" or "Basic" menu that allows you to:

* **Audit the Project:** Validate POM files, check for newer plugin/dependency versions, or view the dependency tree.
* **Build & Test:** Run standard Maven cycles like clean, install, package, and test.
* **Inspect POMs:** The `comparepom` function generates an "effective POM" (your POM plus all inherited settings) and opens a diff tool (like meld or vimdiff) to compare it against your local pom.xml.

## 2. Smart Logging and Output

* **Output Function:** It automatically decides how to show you the build results. If you’re in a desktop environment, it might just run; if you're in a pure terminal, it pipes the output into `less` so you can scroll through the logs easily.
* **Logging:** If `BUILD_LOG` is enabled, it saves everything to a build.log file using the `tee` command.

## 3. Environment Setup

* It looks for two helper scripts in its directory: `build-CFG.sh` (for configuration) and `build-INC.sh` (for shared functions).
* It ensures you have the necessary tools installed (mvn, vim, xmllint, and whiptail) before it starts.

## 4. Developer Shortcuts

* **v) option:** Quickly opens the current directory in vim.
* **Sub-project support:** It’s designed to be called from sub-folders (e.g., ../../build.sh) so you can run parent build actions from anywhere in a large project.




