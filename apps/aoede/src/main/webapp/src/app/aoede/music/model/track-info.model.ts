import { Clef, clefInitializer } from './clef.model';
import { KeySignature, keySignatureInitializer } from './key-signature.model';
import { Fraction, fractionInitializer } from './fraction.model';

export interface TrackInfo {
	title         : string;
	name          : string;
	clef          : Clef;
	tempo         : number;
	keySignature  : KeySignature;
	timeSignature : Fraction;
};

export const trackInfoInitializer : TrackInfo = {
	title         : "",
	name          : "",
	clef          : clefInitializer,
	tempo         : 0,
	keySignature  : keySignatureInitializer,
	timeSignature : fractionInitializer
};
