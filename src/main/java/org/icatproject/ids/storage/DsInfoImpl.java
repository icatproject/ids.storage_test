package org.icatproject.ids.storage;

import org.icatproject.ids.plugin.DsInfo;

public class DsInfoImpl implements DsInfo {

	private long dsId;
	private String dsLocation;
	private String dsName;
	private long facilityId;
	private String facilityName;
	private long invId;
	private String invName;
	private String visitId;

	public DsInfoImpl(String facilityName, String invName, String visitId, String dsName) {
		this.dsName = dsName;
		this.facilityName = facilityName;
		this.invName = invName;
		this.visitId = visitId;
	}

	public DsInfoImpl(long invId, long dsId) {
		this.invId = invId;
		this.dsId = dsId;
	}

	@Override
	public Long getDsId() {
		return dsId;
	}

	@Override
	public String getDsName() {
		return dsName;
	}

	@Override
	public String getDsLocation() {
		return dsLocation;
	}

	@Override
	public Long getFacilityId() {
		return facilityId;
	}

	@Override
	public String getFacilityName() {
		return facilityName;
	}

	@Override
	public Long getInvId() {
		return invId;
	}

	@Override
	public String getInvName() {
		return invName;
	}

	@Override
	public String getVisitId() {
		return visitId;
	}

	@Override
	public String toString() {
		return invId + "/" + dsId;
	}

}
