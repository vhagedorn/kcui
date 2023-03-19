I hope you're enjoying KanjiCard UI! This file contains information about customizing your flashcards.

If you're curious, read it through, but *don't worry* if you mess up the files, just <u>delete 
them and they will reset</u>.

~Vadim

# Template files
These files are used to generate the flashcards. If you do not know HTML, then don't edit them.

If someone shares templates with you, paste them in this folder. Delete them to reset to default.

# Editing

**NOTE**: For now, Landscape mode is *not supported*.

## Overview

* `card_stylesheet.css` defines some general rules used by both files
* `back_side_card.html` defines the back side containing the pronounciations, grammar, definitions, and stroke order diagrams
* `frontside_card.html` defines the frontside card containing the kanji

### Global styles

Here are the primary styles from `card_stylesheet.css`:

* `.printable` - anything that should have special treatment with `@media print`
* `.watermark` - shows a big watermark on the screen, but hides when printing
* `.vertical` - defines vertical text

Note that each `.html` file has its own styles inside (explained with each `id`, below).

### Placeholders

Placeholders are filled in by `id` and are not currently customizable.
If you choose to add your own styles, they will not be applied to any generated elements (such as `<li>` definitions).
However, parent styles should still be applied, and any changes you make to the actual classes should reflect in the output.

The default `frontside_card.html` and `back_side_card.html` include dummy placeholders to help you get started.
#### front

* `#kanji` - the kanji value

#### back
* `#diagrams` - stroke order diagrams are added as base64 `<img>` tags with class `.stroke-center`
* `#pronounciation` - pronounciations are added as `<span>` tags inside a `<div>`, with classes `.psym` for the symbol ('⛛' for now), and `.pron` for the pronounciation value
* `#type` - the element's value is set to the grammar string (parts of speech). The element
* `#definition` - children are cleared and definitions are added as unstyled `<li>` tags