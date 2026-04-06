# AI-Powered Video Generator - Java Console Application

A console-based video creation tool written in Java that transforms collections of photos and videos into AI-narrated portrait-format videos. The program uses OpenAI's API for image generation, script writing, and text-to-speech, combined with FFmpeg for video processing.

---

## Features

* **AI Image Generation**: Creates an artistic "essence" image and a geographic map showing the journey from oldest to newest location.
* **Smart Script Writing**: Analyzes visual content using multimodal AI to generate natural narration.
* **Audio Synthesis**: Converts the script to speech.
* **Video Processing**: Normalizes all media to consistent format, concatenates segments, and applies portrait conversion with blur effects.
* **Audio Optimization**: Audio normalized to YouTube standards (-14 LUFS, -1 dBTP, 7 LU).
* **Metadata**: Extracts GPS coordinates and dates to create chronological narratives.

---

## Requirements

1. **Java Development Kit (JDK)** installed.
2. **ExifTool** installed and in system PATH (for metadata extraction).
3. **FFmpeg** installed and in system PATH (for video processing).
4. **OpenAI API Key** set as environment variable `OpenAIToken`.
5. Media files with GPS and date metadata (images or videos).

---

## Compilation and Execution

1. **Set up API key**:
   ```bash
   export OpenAIToken="your-api-key-here"
   ```
   Or on Windows:
   ```cmd
   set OpenAIToken=your-api-key-here
   ```

2. **Compile**:
   ```bash
   javac *.java
   ```

3. **Run**:
   ```bash
   java Main
   ```

4. **Follow the prompts**:
   - Enter the number of files you want to process
   - Provide the full path to each file
   - Wait for AI processing (images, script, audio)
   - Wait for video compilation
   - Enter your desired output filename (without .mp4)
   - Optionally verify audio loudness levels

---

## Workflow

The program executes the following steps automatically:

1. **Metadata Extraction**: Reads GPS coordinates and creation dates from all files.
2. **Chronological Sorting**: Orders files from oldest to newest.
3. **Essence Image**: Generates an artistic AI image representing the entire collection.
4. **Map Image**: Creates a geographic map showing oldest and newest locations with a motivational quote.
5. **Script Generation**: Analyzes visual content and writes a natural narration script.
6. **Audio Creation**: Converts script to speech (MP3 format).
7. **Video Segments**: Normalizes all media to 1920x1080, 30fps, H.264.
8. **Compilation**: Concatenates segments with audio (delayed 3 seconds after opening).
9. **Portrait Conversion**: Transforms to 1080x1920 with blurred letterbox background.
10. **Audio Verification** (optional): Displays loudness metrics vs. YouTube standards.

---

## Supported Formats

### Images
* PNG, JPG/JPEG, WebP, GIF, BMP, TIFF
* **Note**: HEIC/HEIF may fail depending on FFmpeg decoder availability

### Videos
* MP4, MOV, AVI, MKV, WebM, M4V
* **Note**: Original audio is removed and replaced with generated narration

### Unsupported
* RAW formats (CR2, NEF, ARW, DNG, ORF, etc.)
* SVG (vector graphics)
* AVIF, JPEG XL (depends on FFmpeg version)

---

## Output Files

The program generates the following files in the working directory:

* `essenceImage.png` - AI-generated artistic representation
* `mapImage.png` - Geographic map with location markers
* `narration.mp3` - Text-to-speech audio narration
* `segment_000.mp4` - Opening image segment
* `segment_1.mp4`, `segment_2.mp4`, ... - User media segments
* `segment_last.mp4` - Closing map segment
* `input.txt` - FFmpeg concatenation file
* `final_video.mp4` - Horizontal compiled video (intermediate)
* `[your-name].mp4` - Final portrait video with blur effects

---

## Video Specifications

### Segment Normalization
* Resolution: 1920×1080 (horizontal)
* Frame rate: 30 fps
* Codec: H.264 (libx264)
* Pixel format: YUV420p
* Still image duration: 3 seconds each

### Final Output
* Resolution: 1080×1920 (portrait)
* Background: Blurred and stretched source video
* Foreground: Letterboxed original content
* Audio: AAC 192 kbps, normalized to YouTube standards
* Audio delay: 3 seconds (starts after opening image)

### YouTube Audio Standards
* Integrated Loudness: -14 LUFS
* True Peak: -1 dBTP
* Loudness Range: 7 LU

---

## Performance Notes

* Processing time: 3-10 minutes for 5-10 files (varies with file size and API latency)
* API calls: ~10-30 seconds each for image generation
* Video encoding: CPU-intensive, scales with file size and quantity
* Network required: For OpenAI API communication

---

## Troubleshooting

### "Environment variable 'OpenAIToken' not found"
* Ensure the API key is set correctly as an environment variable
* Restart your terminal/command prompt after setting it

### "No data" for GPS or Date
* The file lacks metadata (common in screenshots or edited images)
* Files without dates are sorted using a fallback value

### Map image doesn't appear in final video
* Check for warnings during segment creation
* Verify `mapImage.png` was generated successfully
* Ensure audio is not cutting the video short

### FFmpeg errors
* Verify FFmpeg is installed: `ffmpeg -version`
* Check that FFmpeg is in your system PATH
* Ensure sufficient disk space for temporary files

### Portrait blur effect not working
* This occurs when segments are created in vertical format first
* Update to the latest version which normalizes to horizontal before conversion

---

## Example Usage

```bash

# Sample interaction:
Please specify the amount of files you want to process
> 3

Provide path for file 1:
> /Users/you/Pictures/vacation1.jpg

Provide path for file 2:
> /Users/you/Pictures/vacation2.jpg

Provide path for file 3:
> /Users/you/Videos/vacation3.mp4

[Processing] Creating the essence image...
Image saved as essenceImage.png

[Processing] Creating the map image...
Image saved as mapImage.png

[Processing] Generating narration script...
Generated Script:
A sunset over the mountains. Friends laughing at the beach. Waves crashing on the shore.

[Processing] Generating audio narration...
Audio saved as narration.mp3

[Processing] Creating video segments...
  - Processing opening image...
  - Processing image file 1/3...
  - Processing image file 2/3...
  - Processing video file 3/3...
  - Processing closing map...
  - Compiling final video with audio...
Video created successfully: final_video.mp4

Enter final video name (without .mp4):
> summer_memories

[Processing] Converting to portrait format with blur effect...
Final video created: summer_memories.mp4

Do you want to verify audio loudness levels? (yes/no):
> yes

[Verifying] Checking audio loudness levels...
  Input Integrated: -14.0 LUFS
  Input True Peak: -1.0 dBTP
  Input LRA: 7.0 LU

YouTube Standards:
  Target Loudness: -16 to -14 LUFS
  Target True Peak: -2 to -1 dBTP
  Target LRA: 5 to 10 LU

Process completed successfully :D!
Final video: summer_memories.mp4
```

---

## Credits

Developed by Marco Rodrigo Campoy Mendoza  
Powered by OpenAI API, FFmpeg, and ExifTool

---

## License

This project is provided as-is for educational purposes.
