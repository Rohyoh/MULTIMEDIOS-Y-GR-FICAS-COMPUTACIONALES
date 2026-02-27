# Image Editor - Java Console Application

A simple console-based image editor written in Java that allows you to perform basic operations on PNG images. The program enables progressive editing by saving changes to a temporary output file.

---

## Features

* **Rotate**: Rotate a user-selected rectangular area by 90°, 180°, or 270° (counter-clockwise).
* **Invert Colors**: Invert the colors of a selected rectangular area.
* **Crop**: Crop the image to the selected rectangle.
* **Persistent Output**: Results are saved to `src/editOutput.png`. If this file exists on startup, it is loaded instead of the original to allow continuous editing.

---

## Requirements

1.  **Java Development Kit (JDK)** installed.
2.  An image file named `edit.png` placed inside the `src` folder (relative to the project root).

---

## Compilation and Execution

1.  **Prepare the image**: Place your target image inside the `src` folder and name it `edit.png`.
2.  **Compile**:
    ```bash
    javac src/*.java
    ```
3.  **Run**:
    ```bash
    java src.Main
    ```
---

## Coordinate Selection

For **Rotate**, **Invert Color**, and **Crop**, you must define a rectangular area using two opposite corners: **A** (top-left) and **C** (bottom-right).

**Visual Guide:**
```text
xA, yA |-------------| xB, yB
       |             |
       |             |
       |             |
xD, yD |-------------| xC, yC