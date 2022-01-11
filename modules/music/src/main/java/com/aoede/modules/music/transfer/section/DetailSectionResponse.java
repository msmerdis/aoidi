package com.aoede.modules.music.transfer.section;

import com.aoede.modules.music.transfer.track.AccessTrack;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DetailSectionResponse extends SimpleSectionResponse {
	private AccessTrack trackId;
	private Long sheetId;
}



