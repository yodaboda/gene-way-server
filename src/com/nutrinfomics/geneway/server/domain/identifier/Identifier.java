package com.nutrinfomics.geneway.server.domain.identifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class Identifier extends EntityBase {
	@Column(nullable=false, unique=true)
	private String uuid;

	@Column(nullable=false, unique=true)
	@Size(min=4, max=4, message="{identifier.identifierCode.size.message}")
	private String identifierCode;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIdentifierCode() {
		return identifierCode;
	}

	public void setIdentifierCode(String identifierCode) {
		this.identifierCode = identifierCode;
	}

}
