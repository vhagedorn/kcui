# Hello there
I hope you're enjoying KanjiCard UI! This file contains information about customizing your flashcards.

If you're curious, read it through, but *don't worry* if you mess up the files, just <u>delete 
them and they will reset</u>.

Enjoy o/

~Vadim

PS: Make sure you read the [epilogue](#epilogue) before attempting to modify anything.

# Template files
These files are used to generate the flashcards. If you do not know HTML, then don't edit them.

If someone shares templates with you, paste them in this folder. Delete them to reset to default.

## Editing

**NOTE**: For now, Landscape mode is *not supported*.

### Overview

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

# API
While I don't provide any API or external hooks, it is possible to modify exported flashcards. The PDF generation happens via a child Chrome process.
Initially, I used [electron-pdf](https://github.com/fraserxu/electron-pdf), but once I figured out how it worked, I found and used [jvppeteer](https://github.com/fanyong920/jvppeteer) instead.
After I explain how the flashcards are generated, I'll show you how you can intercept and modify export requests.

## Workflow

1) Card data is stored in via SQLite3 in `library.db`, and images are cached in `render.cache` (it is safe to delete this image cache if it becomes large).
2) Card is created via UI or loaded from DB. 
3) Templates are loaded (from this `templates` directory, or from the JAR's resources).
4) HTML files are populated, filling in placeholders with actual data.
5) HTML files are uploaded to an in-memory file server.
6) The conversion service (the Chrome process, or the Electron server), is queried with the URL to the HTML file hosted on the in-memory file server (owned by this Java process).
7) The conversion service converts the HTML file and returns the resulting PDF as a stream.
8) The PDF is then merged/saved/etc.

## Interception

You will have control over the HTML and PDF versions of the card during steps 6 and 7.

**NOTE**: Previews will *not* be generated with [electron-pdf](https://github.com/fraserxu/electron-pdf).

- Maybe in the future I'll make an actual API...

## Example

The [electron-pdf](https://github.com/fraserxu/electron-pdf) option is still in the code, and to enable it, 
run the program setting the property `electron-url` to your Electron server's convert URL.
For example, if your server is on port `8081`, and your endpoint is `/pdfexport` (as in the example below), then run: 
```bash
java -Delectron-url="http://localhost:8081/pdfexport" -jar ui.jar 
```
Look at their [documentation](https://github.com/fraserxu/electron-pdf#node-usage) to see how you can intercept the print requests and modify the resulting PDF to your liking.

Here is a working `index.js` to simply generate a 4×6 PDF of the URL provided:
```js
const ElectronPDF = require('electron-pdf')
const express = require('express')
const bodyParser = require('body-parser')
const stream = require('stream');

// start this with `npm run start` or `npm run watch` (for auto-reloading)

var app = express()
app.use(bodyParser.json())

// hostname = '0.0.0.0' // must be adapter or Java won't see it // actually, leaving it out works too
port = 8081

var exporter = new ElectronPDF()
exporter.on('charged', () => {
	//Only start the express server once the exporter is ready
	app.listen(port, /*hostname,*/ function() {
		console.log(`Export Server running at http://${/*hostname*/"localhost"}:${port}`);
	})
})
exporter.start()

app.post('/pdfexport', function(req,res){
    console.log("POST request receieved: " + req.body.url)
    // derive job arguments from request here
    source = req.body.url
    target = 'target.pdf'
    const jobOptions = {
	  /**
	    r.results[] will contain the following based on inMemory
          false: the fully qualified path to a PDF file on disk
          true: The Buffer Object as returned by Electron
	    
	    Note: the default is false, this can not be set using the CLI
	   **/
	  inMemory: true,
	  closeWindow: false
	}
	const options = {
  		pageSize : {
			width: 4,
			height: 6
		},
		marginsType: 0 /// Normal
				   //1 /// None
				   //2 /// Minimal
	}
	exporter.createJob(source, target, options, jobOptions).then( job => {
		job.on('job-complete', (r) => {
			var readStream = new stream.PassThrough();
  			readStream.end(r.results[0]);

			res.set('Content-disposition', 'attachment');
  			res.set('Content-Type', 'application/pdf');
			res.status(200)

			readStream.pipe(res)
			  
			process.nextTick(() => {job.destroy()}) // This is important!
		})
		job.render()
	})	
})
```

Along with a relevant `package.json`:
```json
{
  "name": "epdf",
  "version": "1.0.0",
  "description": "html to pdf printer using browser",
  "main": "index.js",
  "scripts": {
    "start": "electron index.js",
    "watch": "nodemon --exec electron index.js"
  },
  "author": "vadim",
  "dependencies": {
    "body-parser": "^1.20.2",
    "electron": "^23.1.1",
    "electron-pdf": "^20.0.0",
    "express": "^4.18.2"
  }
}
```

Inside the `/pdfexport` callback is where you would modify the HTML, print options, or generated PDF.
Right now, it's not possible to do much unless you host _another_ server with your modified files.
I plan to add an upload endpoint to the in-memory server to ease modding, but it's not present right now.

## Epilogue

**PLEASE DON'T USE THIS "API".** Modify the templates to your liking, and please them to me as I am not a CSS wizard.
I put this here because I can't help myself, but it was never intended to be used. My only hope is that it might help me remember how my code works in the future.

