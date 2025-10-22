# MkDocs Documentation Guide

## Overview

This project uses **MkDocs** with the **Material** theme to generate beautiful, searchable documentation from all markdown files.

## What is MkDocs?

MkDocs is a fast, simple static site generator designed for building project documentation. It uses Markdown files to create a professional documentation website.

### Features Enabled
- ✅ **Material Theme** - Modern, responsive design
- ✅ **Dark/Light Mode** - User preference toggle
- ✅ **Search** - Full-text search across all documentation
- ✅ **Navigation** - Organized hierarchical navigation
- ✅ **Code Highlighting** - Syntax highlighting for code blocks
- ✅ **Responsive** - Mobile-friendly design
- ✅ **Fast** - Instant page loading
- ✅ **Customizable** - Custom CSS and theme options

## Installation

MkDocs is already installed for this project. If you need to reinstall:

```bash
pip3 install mkdocs mkdocs-material
```

### Verify Installation
```bash
mkdocs --version
```

## Documentation Structure

```
sprboot_api_cktBrkr/
├── mkdocs.yml                    # MkDocs configuration
├── docs/                         # Documentation root
│   ├── index.md                  # Home page (from README.md)
│   ├── quick-start/              # Quick start guides
│   │   ├── h2-quick-start.md
│   │   ├── frontend-quickstart.md
│   │   ├── docker-quick-start.md
│   │   └── gatling-quick-reference.md
│   ├── stylesheets/              # Custom CSS
│   │   └── extra.css
│   └── [all docs from src/main/docs/]
└── site/                         # Generated site (gitignored)
```

## Usage

### 1. Start Development Server

Start a local server with live reload:

```bash
mkdocs serve
```

Access the documentation at: **http://127.0.0.1:8000**

The server will automatically reload when you edit any markdown files.

### 2. Build Static Site

Generate the static HTML site:

```bash
mkdocs build
```

Output will be in the `site/` directory.

### 3. Deploy to GitHub Pages

Deploy directly to GitHub Pages:

```bash
mkdocs gh-deploy
```

This will build the site and push to the `gh-pages` branch.

## Configuration

The `mkdocs.yml` file contains all configuration:

### Key Sections

#### Site Information
```yaml
site_name: Spring Boot API with Circuit Breaker
site_description: Full-stack production-ready application
site_author: Development Team
```

#### Theme Configuration
```yaml
theme:
  name: material
  palette:
    - scheme: default      # Light mode
    - scheme: slate        # Dark mode
  features:
    - navigation.tabs      # Top-level tabs
    - navigation.instant   # Instant loading
    - search.suggest       # Search suggestions
    - content.code.copy    # Copy code button
```

#### Navigation Structure
The `nav` section defines the documentation hierarchy:
- Home (index.md from README.md)
- Quick Start Guides
- Architecture
- Implementation Guides
- Migration Guides
- Test Results
- Migration Status
- Additional Documentation

## Adding New Documentation

### 1. Add Markdown File

Place your markdown file in the appropriate location:
- Quick starts: `docs/quick-start/`
- Detailed docs: `docs/`

### 2. Update Navigation

Edit `mkdocs.yml` and add your file to the `nav` section:

```yaml
nav:
  - Your Section:
    - Your Page: docs/your-file.md
```

### 3. Preview Changes

```bash
mkdocs serve
```

## Markdown Features

### Code Blocks with Syntax Highlighting

\`\`\`java
public class Example {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
\`\`\`

### Admonitions (Callouts)

```markdown
!!! note "Note Title"
    This is a note admonition.

!!! tip "Pro Tip"
    This is a tip admonition.

!!! warning "Warning"
    This is a warning admonition.

!!! danger "Danger"
    This is a danger admonition.
```

### Task Lists

```markdown
- [x] Completed task
- [ ] Pending task
- [ ] Another pending task
```

### Tables

```markdown
| Feature | Status |
|---------|--------|
| Backend | ✅ Complete |
| Frontend | ✅ Complete |
```

### Tabs

```markdown
=== "Java"
    \`\`\`java
    System.out.println("Hello");
    \`\`\`

=== "JavaScript"
    \`\`\`javascript
    console.log("Hello");
    \`\`\`
```

### Mermaid Diagrams

\`\`\`mermaid
graph LR
    A[Frontend] --> B[Backend]
    B --> C[Database]
\`\`\`

## Customization

### Custom CSS

Edit `docs/stylesheets/extra.css` to customize styling:

```css
:root {
  --md-primary-fg-color: #3f51b5;
  --md-accent-fg-color: #536dfe;
}
```

### Theme Colors

Edit `mkdocs.yml` to change theme colors:

```yaml
theme:
  palette:
    primary: indigo
    accent: indigo
```

Available colors: red, pink, purple, deep purple, indigo, blue, light blue, cyan, teal, green, light green, lime, yellow, amber, orange, deep orange

## Search

MkDocs includes built-in search functionality:
- Full-text search across all pages
- Search suggestions
- Keyboard shortcut: `/` or `s`

## Navigation Features

### Tabs
Top-level navigation tabs for major sections

### Sections
Expandable/collapsible sections in sidebar

### Table of Contents
Automatic TOC generation from headers

### Breadcrumbs
Navigation path showing current location

## Best Practices

### 1. Use Descriptive Headers
```markdown
# Main Title (H1)
## Section (H2)
### Subsection (H3)
```

### 2. Add Metadata
Include front matter for better organization:
```markdown
---
title: Page Title
description: Page description
---
```

### 3. Link Between Pages
Use relative links:
```markdown
[Link to other page](../other-page.md)
```

### 4. Include Code Examples
Always include syntax highlighting:
\`\`\`language
code here
\`\`\`

### 5. Use Admonitions
Highlight important information with admonitions

## Deployment Options

### 1. GitHub Pages
```bash
mkdocs gh-deploy
```

### 2. Netlify
- Connect your repository
- Build command: `mkdocs build`
- Publish directory: `site`

### 3. Read the Docs
- Import your repository
- MkDocs is auto-detected

### 4. Self-Hosted
Build and deploy the `site/` directory to any web server:
```bash
mkdocs build
# Copy site/ to your web server
```

## Troubleshooting

### Port Already in Use
```bash
mkdocs serve -a 127.0.0.1:8001
```

### Build Errors
Check `mkdocs.yml` syntax:
```bash
mkdocs build --strict
```

### Missing Dependencies
Reinstall MkDocs:
```bash
pip3 install --upgrade mkdocs mkdocs-material
```

## Useful Commands

| Command | Description |
|---------|-------------|
| `mkdocs serve` | Start dev server |
| `mkdocs build` | Build static site |
| `mkdocs gh-deploy` | Deploy to GitHub Pages |
| `mkdocs --version` | Show version |
| `mkdocs --help` | Show help |

## Resources

- **MkDocs Documentation:** https://www.mkdocs.org
- **Material Theme:** https://squidfunk.github.io/mkdocs-material
- **Markdown Guide:** https://www.markdownguide.org
- **Material Icons:** https://fonts.google.com/icons

## Project-Specific Notes

### Documentation Sources
- **index.md** - Generated from `README.md`
- **quick-start/** - Copied from root quick start guides
- **docs/** - Copied from `src/main/docs/`

### Automatic Updates
When you update markdown files in:
- `README.md` - Manually copy to `docs/index.md`
- Root quick starts - Manually copy to `docs/quick-start/`
- `src/main/docs/` - Manually copy to `docs/`

Or use the sync script (if created).

### Live Preview
Always use `mkdocs serve` during development to see changes in real-time.

---

**MkDocs Version:** 1.6.1  
**Material Theme Version:** 9.6.22  
**Last Updated:** October 22, 2025
