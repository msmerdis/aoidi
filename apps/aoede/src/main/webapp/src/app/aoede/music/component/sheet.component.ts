import { Component, OnDestroy, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { tap, map, filter, switchMap } from 'rxjs/operators';

import { Sheet } from '../model/sheet.model';
import { Bar, Line } from '../model/line.model';
import { MusicState } from '../store/music.reducer';
import { fetchSheetRequest } from '../store/music.actions';
import { getSheetValueSafe } from '../store/music.selectors';
import { getRequestPayload } from '../../generic/generic-store.model';
import { MusicCanvasService } from '../music-canvas.service';

@Component({
	selector: 'aoede-music-sheet',
	templateUrl: './sheet.component.html',
	styleUrls: ['./sheet.component.scss']
})
export class SheetComponent implements OnDestroy {

	public id : number = 0;
	public sheet$ : Subscription;

	public lines  : number = 0;
	public width  : number;
	public height : number;
	public header : number;
	public footer : number;

	private context : CanvasRenderingContext2D | null = null;

	@ViewChild('sCanvas') set content (content: ElementRef) {
		if (content != null) {
			this.context = content.nativeElement.getContext('2d');
			this.drawCanvas ();
		}
	}

	private modified : boolean = false;
	private lineBars : Line[] = [];
	public title : string = "";

	constructor(
		private store : Store<MusicState>,
		private route : ActivatedRoute,
		private paint : MusicCanvasService,
		private cdref : ChangeDetectorRef
	) {
		this.sheet$ = this.route.params.pipe (
			map(params => +params['id']),
			tap(id => this.dispatch(id)),
			switchMap(id => this.store.select (getSheetValueSafe, id))
		).subscribe ((sheet) => {
			if (this.modified)
				return;

			if (sheet === null) {
				this.lineBars = [];
				this.lines    = 0;
				return;
			}

			this.lineBars = this.paint.splitLines(sheet);
			this.lines = this.lineBars.length;
			this.title = sheet.name;
		});

		this.width  = this.paint.lineWidth;
		this.height = this.paint.lineHeight;
		this.header = this.paint.headerHeight;
		this.footer = this.paint.footerHeight;
	}

	ngOnDestroy (): void {
		this.sheet$.unsubscribe ();
	}

	private dispatch(id : number) {
		if (this.id == id)
			return;

		this.id = id;
		this.store.dispatch (fetchSheetRequest(
			getRequestPayload<number>(id)
		));
	}

	private drawCanvas () {
		this.cdref.detectChanges();
		if (this.context !== null) {
			this.context.save();
			this.paint.clearArea(this.context, 0, 0, this.width, this.header + this.height * this.lines + this.footer);

			this.paint.drawText(this.context, 0, 0, this.width, this.header, this.title);
			for (var i = 0; i < this.lines; i += 1) {
				this.paint.setupLine(this.context, 0, this.header + this.height * i, this.width, this.height, i);
			}
			this.paint.drawText(this.context, 0, this.header + this.height * i, this.width, this.footer, "- " + this.id + " -");
			this.context.restore();
		}
	}

	public prev () {
		if (this.lines > 1)
			this.lines -= 1;
		this.drawCanvas ();
	}

	public next () {
		this.lines += 1;
		this.drawCanvas ();
	}
}
