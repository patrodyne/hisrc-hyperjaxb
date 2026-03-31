Code Style Guide: Modified Allman (BSD)
---------------------------------------

This project uses a modified Allman (BSD) code formatting style. The Allman style (also known as the BSD style) is a source code formatting convention where the opening brace of a code block is placed on a new line. Named after Eric Allman, who wrote many BSD Unix utilities, it is highly favored for its visual clarity and ease of matching pairs of braces.

### Key Characteristics (Modified Allman)

+ **Encoding:** Default to UTF-8 without a Byte Order Mark (BOM). End of line character is LF.
+ **Tabs:** Use real tabs for efficiency and display customization.
+ **Brace Placement:** The opening brace { is placed on its own line, directly below the control statement (like if, while, or a function signature).
+ **Alignment:** Both the opening and closing braces are vertically aligned at the same indentation level as the parent statement.
+ **Indentation:** The code within the braces is indented to the next level (typically tabs of length 4).
+ **Single-Line Block:** Braces are omitted for single statements, generally.
+ **Space After Keywords:** A space is typically placed after control keywords (like if, for, while) before the opening parenthesis.
+ **Function Calls:** There is usually no space between a function name and the opening parenthesis.
* **Padding:** A space after the opening parenthesis and before the closing one to increase readability.
