============= Image Editor - Java Console Application =============
A simple console-based image editor written in Java that allows you to perform basic operations on a PNG image: rotate, invert colors, and crop a selected rectangular region. The program loads an image from src/edit.png and saves all changes to src/editOutput.png, enabling progressive editing.

============= Features =============
Rotate – Rotate a user‑selected rectangular area by 90°, 180°, or 270° (counter‑clockwise).

Invert Colors – Invert the colors of a selected rectangular area.

Crop – Crop the image to the selected rectangle.

Persistent Output – After each operation, the result is saved to src/editOutput.png. If this file exists on startup, it is loaded instead of the original, allowing you to continue editing.

============= Requirements =============
An image file named edit.png placed inside the src folder (relative to the project root).

============= Compilation and Execution =============
1. Place your image – Put the image you want to edit inside the src folder and name it edit.png.
2. Compile 
3. Run 

============= Coordinate Selection =============
For Rotate, Invert Color, and Crop you will be asked to define a rectangular area using two opposite corners: A (top‑left) and C (bottom‑right). The visual guide printed in the console looks like this:

xA, yA |-------------| xB, yB
       |             |
       |             |
       |             |
       |             |
       |             |
xD, yD |-------------| xC, yC

Note: Coordinates are pixel indices starting from (0,0) at the top‑left of the image. Make sure your values are within the image dimensions.

============= Operation Details =============
Rotate: After selecting the rectangle, you choose an angle (90, 180, or 270). The content inside the rectangle is rotated counter‑clockwise and placed back into the same rectangle area. The rest of the image remains unchanged.

Invert Color: Every pixel inside the rectangle is inverted (RGB values become 255 – original).

Crop: The image is cropped to the rectangle. The new image replaces the old one, and subsequent operations work on the cropped version.

After each successful operation, the image is saved to src/editOutput.png, and a confirmation message is printed.

============= Important Notes =============
The program does not validate that the entered coordinates are inside the image bounds or that xA < xB and yA < yC. Entering invalid values may lead to unexpected results or exceptions.

The rotation algorithm uses an inverse transformation to avoid holes; the original rectangle area is temporarily filled with black before drawing the rotated content.

The output is always saved as a PNG file, regardless of the original format.

If you want to start over with the original image, simply delete src/editOutput.png and run the program again.




