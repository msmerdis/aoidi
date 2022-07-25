import { Injectable } from '@angular/core';

import { ArrayCanvasService } from './canvas.service';
import { BarService } from './bar.service';
import { StaveSignatureService } from './stave-signature.service';
import { SheetConfiguration } from '../model/sheet-configuration.model';
import { StaveConfiguration } from '../model/stave-configuration.model';

import { Track } from '../model/track.model';
import {
	MappedStave,
	mappedStaveInitializer,
	MappedStaveSignature,
	MappedBar,
	mappedBarInitializer
} from '../model/stave.model';

@Injectable({
	providedIn: 'root'
})
export class StaveService implements ArrayCanvasService<Track, MappedStave> {

	constructor(
		private barService : BarService,
		private staveSignatureService : StaveSignatureService
	) { }

	public map  (source : Track[], staveConfig : StaveConfiguration, sheetConfig : SheetConfiguration): MappedStave[] {
		let staves = this.barService
			.map (source, staveConfig, sheetConfig)
			.reduce ((staves : MappedStave[], bar : MappedBar) : MappedStave[] => {
				let bottom = staves[staves.length - 1];

				if (bottom.width + bar.width >= staveConfig.stavesWidth) {
					bottom = this.emptyStave(source, staveConfig, sheetConfig);
					staves.push(bottom);
				}

				bottom.bars.push (bar);
				bottom.width += bar.width;

				return staves;
			}, [this.emptyStave(source, staveConfig, sheetConfig, true)] as MappedStave[]);

		staves.forEach ((stave) => {
			let adjusted = this.barService.normalize(stave.bars, staveConfig.stavesWidth - stave.width - staveConfig.lineHeight);
			stave.width  = adjusted.width + stave.offset;
			stave.header = adjusted.header;
			stave.footer = adjusted.footer;
			stave.tracks = adjusted.tracks;
		});

		return staves;
	}

	private emptyStave (source : Track[], staveConfig : StaveConfiguration, sheetConfig : SheetConfiguration, first : boolean = false) : MappedStave {
		let signatures = this.staveSignatureService.map(source, staveConfig, sheetConfig, first);
		let offset     = 0;

		if (signatures.length > 0) {
			offset = staveConfig.stavesSpacing * 2
				+ signatures.reduce((max, sig) => sig.width > max ? sig.width : max, 0);
		}

		return {
			...mappedStaveInitializer(),
			header     : 0,
			signatures : signatures,
			offset     : offset,
			width      : offset,
			footer     : staveConfig.stavesLineHeight * 6
		};
	}

	public draw (stave : MappedStave, staveConfig : StaveConfiguration, context : CanvasRenderingContext2D, x : number, y : number) : void {
		x += staveConfig.stavesMargin;

		stave.tracks.forEach((track, i) => {
			this.setupStave(staveConfig, context, x, y + track);

			if (stave.offset > 0) {
				this.staveSignatureService.draw(stave.signatures[i], staveConfig, context, x + staveConfig.stavesSpacing, y + track);
			}
		});

		//this.finishStave(stave, staveConfig, context, x, y);

		this.barService.draw (stave.bars, staveConfig, context, x + stave.offset, y);
	}

	private setupStave (staveConfig : StaveConfiguration, context : CanvasRenderingContext2D, x : number, y : number) : void {
		[-2, -1, 0, 1, 2].forEach(i => {
			let yline = staveConfig.stavesLineHeight * i + y;
			context.fillRect(
				x,
				yline,
				staveConfig.stavesWidth,
				staveConfig.lineHeight
			);
		});
	}

	private finishStave (stave : MappedStave, staveConfig : StaveConfiguration, context : CanvasRenderingContext2D, x : number, y : number) : void {
		let len = stave.tracks.length - 1;
		let top = stave.tracks[ 0 ] - 2 * staveConfig.stavesLineHeight;
		let end = stave.tracks[len] + 2 * staveConfig.stavesLineHeight;

		context.fillRect(x + staveConfig.stavesWidth - staveConfig.lineHeight, y + top, staveConfig.lineHeight, end - top);
	}

}
