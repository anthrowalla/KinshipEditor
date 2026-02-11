# KinshipEditor - Java to JavaScript Conversion Progress

## Project Overview

KinshipEditor is a kinship/genealogy diagram editor originally written as a Java AWT application in the late 1990s (copyright 2006, Centre for Social Anthropology and Computing, University of Kent, by Michael D. Fischer). It has been converted to a JavaScript web application using HTML5 Canvas, CSS, and ES Modules.

## GitHub Repository

https://github.com/anthrowalla/KinshipEditor

## Project Location

`/Users/mike/Dropbox/HRAF/2026/KinshipEditor/KinshipEditor/` (web app)
`/Users/mike/Dropbox/HRAF/2026/KinshipEditor/src/` (original Java source)

## What Was Done

### 1. Full Java-to-JavaScript Port

Ported the entire Java AWT application (~9,500 lines across 36 Java files) to ~2,100 lines of JavaScript/HTML/CSS in 4 module files.

**Key conversions:**
- Java AWT Graphics &rarr; Canvas 2D API
- Java static arrays &rarr; JavaScript arrays + ES module exports
- Java custom LinkedList &rarr; native JavaScript arrays
- Java custom XML parser (XFile.java) &rarr; DOMParser
- Java FileDialog &rarr; HTML5 File API + Blob download
- JPEG export &rarr; Canvas.toBlob PNG export
- Java Timer thread &rarr; setInterval

### 2. Files Created

| File | Description |
|------|-------------|
| `index.html` | Main HTML: menubar (File/Labels/Help + logo), property panel, canvas, dialogs |
| `styles.css` | Full styling for all UI components |
| `js/model.js` | Data model: Person, Marriage classes, state management, utility functions |
| `js/renderer.js` | Canvas + SVG 2D rendering: symbols, lines, labels |
| `js/fileio.js` | XML load/save for .kin file format |
| `js/app.js` | Main app: event handling, UI, menus, animation |
| `resources/logoKinship.png` | Application logo (displayed top-right in menubar) |
| `resources/help.txt` | User documentation (loaded via fetch into floating panel) |
| `resources/Family.Kin` | Sample kinship data file |

### 3. Features Implemented

- **Person symbols**: Female (circle), Male (triangle), Neuter (square)
- **Union symbol**: Equals sign (=)
- **Relationship lines**: Spouse lines (to top of union), child/sibling lines (to bottom of union)
- **Mouse interactions**: Click to select, drag to move, shift+drag to create relationships, shift+ctrl+click to delete, alt/meta+drag to remove relationships
- **Union dragging**: Shift+drag union moves nuclear family, Alt+drag union moves entire lineage
- **Property panel**: Birth/death years, name, comment, reason (for unions)
- **Labels menu**: No Label, Initials, First, Last, Whole name display options
- **File operations**: New, Open (.kin/.xml), Save, Save As, Clear All
- **Render/Export**: Render Visible (visible area as PNG), Render Chart (full chart as PNG), Render SVG (full chart as vector SVG with timeline filtering)
- **Timeline animation**: Step through years showing births/deaths/marriages over time
- **Editable toggle**: Lock/unlock the diagram
- **Fix Ego**: Lock the ego selection
- **Help panel**: Floating, draggable, 300px wide, loads help.txt via fetch
- **Logo**: Displayed top-right in the menubar
- **Save confirmation dialog**: Prompts before New/Open/Clear if dirty

### 4. Bugs Found and Fixed

| Bug | Severity | Description | Fix |
|-----|----------|-------------|-----|
| 5 | Critical | Year comparison used string `<=` instead of numeric | Changed to `parseInt(value, 10)` with numeric `<=` |
| 3 | Critical | Union index management during XML loading could overwrite marriages | Cleaner `targetIdx = id - 1` approach |
| 18 | Medium | `delSib()` didn't clean up `parentalUnions` array | Direct filtering of parentalUnions + recalculate parents |
| 9 | Medium | Normal drag set selectLine, causing accidental relationships | Removed selectLine assignment during normal drag |
| 8 | Medium | ctrl+shift triggered both delete AND relationship logic | Restructured to exclusive if/else branches |
| 10 | Medium | Context menu positioned wrong after scrolling | Moved context menu inside canvas-container |
| 21 | Medium | Canvas fixed at 2000x2000, elements beyond invisible | Dynamic canvas sizing from content bounding box |
| 14 | Low | Dead try/catch around parseInt (JS never throws) | Replaced with proper `isNaN` check |
| 16 | Low | Unused birthLabel/deathLabel variables | Removed |
| -- | Low | Missing radix on parseInt calls | Added explicit radix 10 throughout |

### 5. Repository Setup

- Initialized git repository with `.gitignore` (excludes `.DS_Store`, `.claude/`, `webapp` symlink, `dist/`, `CVS/`, Java build artifacts)
- Created public GitHub repo at https://github.com/anthrowalla/KinshipEditor
- Pushed initial commit with all 61 files (original Java source + JavaScript web port + resources)

### 6. Application Architecture

```
KinshipEditor/              (repo root)
  .gitignore
  progress.md               - This file
  build.xml                 - Original Ant build file
  Manifest                  - Original JAR manifest
  lib/                      - Original Java libraries
  src/                      - Original Java source (36 files)
  resources/                - Original Java resources
  KinshipEditor/            (web app)
    index.html              - Main HTML structure
    styles.css              - All styles
    start_server            - Server launch script
    Family.Kin              - Sample data file
    js/
      app.js                - Main controller (~877 lines)
      model.js              - Data model (~320 lines)
      renderer.js           - Canvas + SVG rendering (~553 lines)
      fileio.js             - XML I/O (~250 lines)
    resources/
      logoKinship.png       - Logo image
      help.txt              - Help documentation
      Family.Kin            - Sample data file
```

### 7. Recent Enhancements (2025)

**January 2025:**
- Added **Render SVG** feature: Exports kinship diagram as SVG vector format with proper timeline filtering (respects reference year for both people and unions)
- Changed **delete interaction** from Ctrl+click to **Shift+Ctrl+click** to avoid browser context menu interference
- SVG export matches canvas display exactly by using `hasBegun()` and `hasEnded()` checks
- Browser context menu prevented when Shift+Ctrl keys are held during delete operation

### 8. Data Model

- **`state`** object: `folks[]`, `folkIndex`, `knots[]`, `knotIndex`, `idCounter`, `refYear`, `doLabel`, `symbolSize`
- **`Person`** class: `myId`, `sex`, `location`, `name`, `comment`, `yob/yod`, `marriages[]`, `parents`, `parentalUnions[]`, `selected`, `drawn`
- **`Marriage`** class: `location`, `mid/id`, `type`, `begin/end`, `comment`, `reason`, `spouses[]`, `sibset[]`, `drawn`

### 8. How to Run

```bash
cd /Users/mike/Dropbox/HRAF/2026/KinshipEditor/KinshipEditor/
python3 -m http.server 8080
```

Then open `http://localhost:8080` in a browser.

**Note:** A local HTTP server is required because the app uses ES Modules (`import`/`export`).

## Completed Milestones

1. Full Java-to-JavaScript port
2. Code review and 10 bug fixes (2 critical, 4 medium, 4 low)
3. Logo and Help menu with floating draggable panel
4. GitHub repository created and pushed

## Pending / Future Work

- None at this time. All user requests have been addressed.

## Java Source Files (Reference)

The original Java source files are in `src/`:
- `Person.java` (433 lines) - Individual in genealogy
- `Marriage.java` (491 lines) - Union between people
- `Kind.java` (172 lines) - Gender/symbol types
- `KinshipEditor.java` (1051 lines) - Main frame/controller
- `KinEditPanel.java` (898 lines) - Canvas editing panel
- Plus ~31 additional support files (help dialogs, XML parsing, JPEG encoding, etc.)
