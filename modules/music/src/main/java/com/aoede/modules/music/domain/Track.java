package com.aoede.modules.music.domain;

import java.util.List;

import com.aoede.commons.base.domain.AbstractDomain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Track implements AbstractDomain<TrackKey> {
	private TrackKey id;
	private Clef clef;
	private Sheet sheet;
	private List<Section> sections;
}



