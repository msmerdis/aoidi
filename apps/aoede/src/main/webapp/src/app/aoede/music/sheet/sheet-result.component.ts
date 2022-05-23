import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';

import { Sheet } from '../model/sheet.model';
import { MusicState } from '../store/music.reducer';
import { getSheetList } from '../store/music.selectors';

@Component({
	selector: 'aoede-music-sheet-result',
	templateUrl: './sheet-result.component.html',
	styleUrls: ['./sheet-result.component.scss']
})
export class SheetResultComponent {

	sheetList$ : Observable<Sheet[] | null>;

	constructor(
		private store : Store<MusicState>
	) {
		this.sheetList$ = this.store.select (getSheetList);
	}

}
