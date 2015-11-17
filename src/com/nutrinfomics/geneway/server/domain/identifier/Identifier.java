package com.nutrinfomics.geneway.server.domain.identifier;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Table;
import javax.persistence.Index;
import javax.validation.constraints.Size;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
//@Table(indexes={
//		  @Index(name="IDENT_INDEX", unique=true, columnList="identifierCode"),
//		  @Index(name="UUID_INDEX", unique=true, columnList="uuid")
//			}
//		)
public class Identifier extends EntityBase {
//	@Column(nullable=false, unique=true)
	private String uuid;

//	@Column(nullable=false, unique=true)
	@Size(min=4, max=4, message="{identifier.identifierCode.size.message}")
	private String identifierCode;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date evaluationTermsAcceptanceTime;
	
	private String evaluationTermsAcceptanceIP;
	
	private String userName;
	
	public String getEvaluationTermsAcceptanceIP() {
		return evaluationTermsAcceptanceIP;
	}

	public void setEvaluationTermsAcceptanceIP(String evaluationTermsAcceptanceIP) {
		this.evaluationTermsAcceptanceIP = evaluationTermsAcceptanceIP;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getEvaluationTermsAcceptanceTime() {
		return evaluationTermsAcceptanceTime;
	}

	public void setEvaluationTermsAcceptanceTime(Date evaluationTermsAcceptanceTime) {
		this.evaluationTermsAcceptanceTime = evaluationTermsAcceptanceTime;
	}

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
