package com.aoede.modules.music.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aoede.commons.base.exceptions.GenericException;
import com.aoede.commons.base.service.AbstractServiceDomainImpl;
import com.aoede.modules.music.domain.Measure;
import com.aoede.modules.music.domain.MeasureKey;
import com.aoede.modules.music.domain.SectionKey;
import com.aoede.modules.music.entity.MeasureEntity;
import com.aoede.modules.music.entity.MeasureId;
import com.aoede.modules.music.entity.NoteEntity;
import com.aoede.modules.music.entity.SectionId;
import com.aoede.modules.music.repository.MeasureRepository;

@Service
public class MeasureServiceImpl extends AbstractServiceDomainImpl <MeasureKey, Measure, MeasureId, MeasureEntity, MeasureRepository> implements MeasureService {

	@Autowired
	private SectionService sectionService;

	@Autowired
	private NoteService noteService;

	public MeasureServiceImpl(MeasureRepository repository, EntityManagerFactory entityManagerFactory) {
		super(repository, entityManagerFactory);
	}

	@Override
	public boolean supportsFreeTextSearch() {
		return false;
	}

	@Override
	public String domainName() {
		return "Measure";
	}

	@Override
	public MeasureEntity createEntity(Measure domain, boolean includeParent, boolean cascade) throws GenericException {
		MeasureEntity entity = new MeasureEntity ();
		SectionKey sectionId = domain.getSection().getId();

		updateEntity(domain, entity, includeParent, cascade);

		sectionService.updateMeasureEntity(entity, sectionId);
		entity.setId(new MeasureId(sectionId.getSheetId(), sectionId.getTrackId(), sectionId.getSectionId(), repository.countBySectionId(
			sectionService.createEntityKey(sectionId)
		)));

		return entity;
	}

	@Override
	public void updateEntity(Measure domain, MeasureEntity entity, boolean includeParent, boolean cascade) throws GenericException {
	}

	@Override
	public Measure createDomain(MeasureEntity entity, boolean includeParent, boolean cascade) {
		Measure measure = new Measure ();

		updateDomain (entity, measure, includeParent, cascade);

		return measure;
	}

	@Override
	public void updateDomain(MeasureEntity entity, Measure domain, boolean includeParent, boolean cascade) {
		domain.setId(createDomainKey(entity.getId()));

		if (includeParent) {
			domain.setSection(
				sectionService.createDomain(entity.getSection(), true, false)
			);
		}

		if (cascade) {
			domain.setNotes(
				entity.getNotes().stream()
					.map(e -> noteService.createDomain(e, false, true))
					.peek(d -> d.setMeasure(domain))
					.collect(Collectors.toList())
			);
		}
	}

	@Override
	public void updateNoteEntity(NoteEntity entity, MeasureKey key) {
		entity.setMeasure(repository.getById(createEntityKey(key)));
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public List<Measure> findBySectionId(SectionKey id) {
		SectionId key = sectionService.createEntityKey(id);

		return repository.findBySectionId(key).stream().map(e -> createDomain(e, true, true)).collect(Collectors.toList());
	}

	@Override
	public MeasureId createEntityKey(MeasureKey key) {
		return new MeasureId(key.getSheetId(), key.getTrackId(), key.getSectionId(), key.getMeasureId());
	}

	public MeasureKey createDomainKey(MeasureId id) {
		MeasureKey key = new MeasureKey ();

		key.setSheetId(id.getSheetId());
		key.setTrackId(id.getTrackId());

		key.setSectionId(id.getSectionId());
		key.setMeasureId(id.getMeasureId());

		return key;
	}

}



