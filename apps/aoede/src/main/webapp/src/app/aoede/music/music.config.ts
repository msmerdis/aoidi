import { InjectionToken } from '@angular/core';

export interface MusicConfig {
	apiUrl?    : string | null;
	cacheTime? : number;
}

export const DefaultMusicConfig : MusicConfig = {
	apiUrl    : null,
	cacheTime : 600000, // 10 minutes x 60 seconds / minute x 1000 milliseconds / second
}

export const MusicConfigToken = new InjectionToken<MusicConfig>("MusicConfig");
