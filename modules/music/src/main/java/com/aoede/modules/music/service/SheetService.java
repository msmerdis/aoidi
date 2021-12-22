package com.aoede.modules.music.service;

import com.aoede.commons.base.service.AbstractServiceDomain;
import com.aoede.modules.music.domain.Sheet;
import com.aoede.modules.music.entity.SheetEntity;
import com.aoede.modules.music.entity.TrackEntity;

public interface SheetService extends AbstractServiceDomain <Long, Sheet, SheetEntity> {

	void updateTrackEntity(TrackEntity entity, Long id);

}



