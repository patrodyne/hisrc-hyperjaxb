package org.patrodyne.jvnet.hyperjaxb.ex001.orm;

import java.time.OffsetDateTime;

import org.patrodyne.jvnet.hyperjaxb.ex001.model.Stage;
import org.patrodyne.jvnet.hyperjaxb.ex001.model.Stageable;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * A JPA Entity Listener to process Stageable events.
 *
 * @author Rick O'Sullivan
 */
public class StageableListener
{
	@PrePersist
	public void prePersist(Stageable stageable)
	{
		OffsetDateTime now = OffsetDateTime.now();
		if ( stageable.getUpdatedItem() == null )
			stageable.setUpdatedItem(now);
		if ( stageable.getCreatedItem() == null )
			stageable.setCreatedItem(stageable.getUpdatedItem());
		if ( stageable.getStage() == null )
			stageable.setStage(Stage.OPEN);
	}

	@PreUpdate
	public void preUpdate(Stageable stageable)
	{
		OffsetDateTime now = OffsetDateTime.now();
		stageable.setUpdatedItem(now);
	}
}
