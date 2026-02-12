# KinshipEditor

A kinship diagram and genealogy editor for visualizing anthropological relationships. Originally developed as a Java desktop application, now available as a modern web application.

![KinshipEditor](https://img.shields.io/badge/version-2.0-blue) ![License](https://img.shields.io/badge/license-Unknown-orange)

## Overview

KinshipEditor is a specialized tool for creating and editing kinship diagrams using standardized anthropological symbols. It allows researchers and students to document family relationships, marriage unions, and genealogical data through an intuitive drag-and-drop interface.

### Key Features

- **Standardized Symbols**: Female (circle), Male (triangle), Neuter (square)
- **Union Representation**: Equals sign (=) for marriages/relationships
- **Interactive Editing**: Click, drag, and keyboard modifiers for all operations
- **Data Management**: Track names, dates, locations, and comments for individuals and unions
- **Timeline Animation**: Step through years to visualize births and deaths
- **Export Options**: PNG and SVG export for publications and presentations
- **XML Format**: Open .kin file format for data interchange

## Versions

### Web Application (Recommended)

A modern JavaScript/HTML5 canvas implementation running in the browser.

**Features**:
- Cross-platform compatibility
- No installation required
- SVG vector export
- Responsive design
- ES module architecture

**Access**: Open `KinshipEditor/index.html` in a web browser with a local server

### Java Desktop Application (Legacy)

The original Java AWT application for desktop use.

**Requirements**:
- Java Runtime Environment (JRE)
- Ant build system

**Running**: See [Java Application](#java-application) below

## Getting Started

### Web Application

1. **Clone the repository**:
   ```bash
   git clone https://github.com/anthrowalla/KinshipEditor.git
   cd KinshipEditor
   ```

2. **Start a local server**:
   ```bash
   cd KinshipEditor/
   python3 -m http.server 8080
   ```

   Or use the provided script:
   ```bash
   ./start_server
   ```

3. **Open in browser**:
   Navigate to `http://localhost:8080`

### Java Application

1. **Install requirements**:
   - Java JDK
   - Ant build tool

2. **Build the application**:
   ```bash
   ant compile
   ant jar
   ```

3. **Run the application**:
   ```bash
   ant run
   ```

## Usage

### Creating a Diagram

1. **Add individuals**: Use the panel to create male, female, or neuter symbols
2. **Create unions**: Add union (=) symbols to represent marriages
3. **Connect relationships**:
   - **Shift+drag** from a person to a union (spouse connection)
   - **Shift+drag** from a union to a person (child connection)
4. **Move elements**: Click and drag to reposition

### Editing Operations

| Operation | Action |
|-----------|--------|
| Select element | Click |
| Move element | Drag |
| Create relationship | Shift+drag |
| Delete element | Shift+Ctrl+click |
| Remove relationship | Alt+drag |
| Move nuclear family | Shift+drag union |
| Move entire lineage | Alt+drag union |

### Properties Panel

When an element is selected, edit its properties:
- **People**: Name, birth year, death year, comments
- **Unions**: Start year, end year, reason for ending, comments

### Display Options

- **Labels**: Choose between No Label, Initials, First Name, Last Name, or Full Name
- **Timeline**: Animate through years to show family changes
- **Editable**: Toggle edit mode to lock the diagram
- **Fix Ego**: Lock ego selection for analysis

### File Operations

- **New**: Create a blank diagram
- **Open**: Load existing .kin or .xml files
- **Save**: Save work in .kin format
- **Save As**: Export to a new file

### Export Options

- **Render Visible**: Export PNG of visible canvas area
- **Render Chart**: Export PNG of entire kinship diagram
- **Render SVG**: Export as vector SVG (scalable, publication-quality)

## File Format

KinshipEditor uses an XML-based `.kin` format:

```xml
<kinshipdiagram version="1.1">
  <people>
    <person id="1" gender="male" birth="1950" death="2020">
      <name>John Doe</name>
      <comment>Example person</comment>
    </person>
  </people>
  <marriages>
    <marriage id="1" begin="1975" end="1985">
      <spouses>
        <spouse ref="1"/>
        <spouse ref="2"/>
      </spouses>
      <children>
        <child ref="3"/>
      </children>
    </marriage>
  </marriages>
</kinshipdiagram>
```

## Project Structure

```
KinshipEditor/
├── KinshipEditor/          # Web application
│   ├── index.html          # Main HTML file
│   ├── styles.css          # Application styling
│   ├── js/
│   │   ├── app.js          # Main controller
│   │   ├── model.js        # Data model
│   │   ├── renderer.js     # Canvas/SVG rendering
│   │   └── fileio.js       # XML I/O operations
│   ├── resources/          # Images, help text, samples
│   └── start_server        # Server launch script
├── src/                    # Java source files
├── build.xml              # Ant build configuration
├── Manifest               # JAR manifest
└── progress.md            # Conversion documentation
```

## Development History

- **1990s**: Original Java application developed at the Centre for Social Anthropology and Computing, University of Kent by Michael D. Fischer
- **2006**: Copyright date of original application
- **2025**: JavaScript web port completed with modern enhancements
- **2025**: Added SVG export, improved delete operations, bug fixes

## Contributing

This is a research tool maintained by the anthropology community. Contributions, bug reports, and feature requests are welcome.

## License

Copyright © Michael D. Fischer, Centre for Social Anthropology and Computing, University of Kent, 2006.

## Acknowledgments

- **Original Development**: Michael D. Fischer, Centre for Social Anthropology and Computing, University of Kent
- **Web Port**: 2025 conversion to modern web technologies
- **Purpose**: Developed for anthropological research and genealogy studies

## Support

For questions, issues, or contributions, please visit the [GitHub repository](https://github.com/anthrowalla/KinshipEditor).

## Sample Data

The `resources/Family.Kin` file contains example kinship data to explore the application's capabilities.
