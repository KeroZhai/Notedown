# Notedown

This is a simple markdown editor for writing notes.

## About

Notedown is developed with SWT and JFace, and the core part which parses Markdown into HTML is based on [vsch/flexmark-java](https://github.com/vsch/flexmark-java).

## Features

* Notes management.
* Font and color change in Source page.
* Theme change:
  * Warm
  * Light
  * Dark
  
## Release Notes

### v1.2.0

* New Features
    * Add markers(eg: bold, code block) conveniently by using "Wrap/Unwrap with" in pop-up menu.
    * Font change in source page.
    * Theme change.
* Changes
    * Change app name from "Notes" to "Notedown".

### v1.2.1

* New Features
    * Support pasting images(Need to save it in a pop-up dialog first).
    * Insert images, linkes or tables by using "Insert..." in pop-up menu.
* Changes
    * Now the source page will show by default instead of display page after creating a new note.
* Bug fixes
    * Fix some small bugs.

### v1.2.2

* Changes
    * The style of `<code></code>` looks better now when using Dark Theme.
* Bug fixes
    * Fix the bug that actual file isn't deleted sometimes.
    * Fix the bug that the display page will always re-render everytime when switching to it, even there is no recent edit.
    * The images in first tab is displayed correctly now.

### v1.2.3

* New Features:
    * Drag and drop `.md` file to open it.
    * Delete the whole line by pressing `Ctrl+D`.
* Changes:
    * File name ends with "..." now if it's too long(more than 12 characters).
    * New line indents the same as the previous line.
* Bug Fixes:
    * Fix the bug that a file can be opened multiple times.
    * Correct the wrong part about sublists in `Welcome.md`.



