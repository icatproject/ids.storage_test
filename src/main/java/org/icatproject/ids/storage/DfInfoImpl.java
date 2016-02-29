package org.icatproject.ids.storage;

import java.nio.file.Path;

import org.icatproject.ids.plugin.DfInfo;

public class DfInfoImpl implements DfInfo {

	private String createId;
	private long dfId;
	private String dfLocation;
	private String dfName;
	private String modId;

	public DfInfoImpl(String dfName) {
		this.dfName = dfName;
	}

	public DfInfoImpl(Path path) {
		this.dfLocation = path.toString();
	}

	@Override
	public String getCreateId() {
		return createId;
	}

	@Override
	public Long getDfId() {
		return dfId;
	}

	@Override
	public String getDfLocation() {
		return dfLocation;
	}

	@Override
	public String getDfName() {
		return dfName;
	}

	@Override
	public String getModId() {
		return modId;
	}

	@Override
	public String toString() {
		return dfLocation;
	}

}
