# ti.waveform - Display a waveform (amplitude) in Titanium

<img src="preview.png"/>

Android module that will display a waveform (amplitude) view in Titanium. Using https://github.com/massoudss/waveformSeekBar and https://github.com/lincollincol/Amplituda

## Methods

* <b>openFile(file)</b>

## Events

* <b>loading</b>: operation (downloading, decoding, processing), value (percentage)
* <b>ready</b>
* <b>progress</b>: percentage, progress

## Properties

* <b>progressColor</b>: color of bars behind current time
* <b>barColor</b>: color of bars before current time
* <b>barWidth</b>
* <b>barGap</b>
* <b>gravity</b>: String: top, bottom, center
* <b>progress</b>: get/set current progress
* <b>duration</b>: (read-only) time in milliseconds

## Example

Alloy
```xml
<Waveform id="waveform" module="ti.waveform" onReady="onReady" onProgress="onProgress" onLoading="onLoading"/>
```

Classic
```js
const Waveform = require("ti.waveform");
const win = Ti.UI.createWindow();
win.open();
const btn1 = Ti.UI.createButton({
	bottom: 0,
	title: "load"
});
const btn2 = Ti.UI.createButton({
	bottom: 50,
	title: "play"
});
const audioPlayer = Ti.Media.createAudioPlayer({});
const lbl = Ti.UI.createLabel({
	top: 10
});
const waveform = Waveform.createWaveform({
	top: 0,
	height: 200,
	backgroundColor: "#000",
	barColor: "#f00",
	progressColor: "#333",
	barWidth: 10
})
const file = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, "song.mp3");
var isPlaying = false;
var isReady = false;

btn1.addEventListener("click", e => {
	isReady = false;
	waveform.openFile(file);
})
btn2.addEventListener("click", e => {
	if (isReady) {
		if (!isPlaying) {
			audioPlayer.url = file.nativePath;
			audioPlayer.start();
			btn2.title = "stop";
		} else {
			btn2.title = "play";
			audioPlayer.stop();
		}
		isPlaying = !isPlaying;
	}
})

win.add([waveform, btn1, btn2, lbl]);
waveform.addEventListener("ready", onReady);
waveform.addEventListener("progress", onProgress);
waveform.addEventListener("loading", onLoading);

audioPlayer.addEventListener("progress", function(e) {
	waveform.progress = (e.progress / audioPlayer.duration) * 100;
})

function onProgress(e) {
	console.log(e.percentage);
	console.log(e.progress);
	audioPlayer.time = e.progress;
}

function onReady(e) {
	isReady = true;
	console.log("Duration: " + e.duration);
}

function onLoading(e) {
	lbl.text = e.operation + ": " + Math.round(e.value) + "%";
}
```

<span class="badge-buymeacoffee"><a href="https://www.buymeacoffee.com/miga" title="donate"><img src="https://img.shields.io/badge/buy%20me%20a%20coke-donate-orange.svg" alt="Buy Me A Coke donate button" /></a></span>
