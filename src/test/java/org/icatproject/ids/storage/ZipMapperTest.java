package org.icatproject.ids.storage;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.icatproject.ids.plugin.DfInfo;
import org.icatproject.ids.plugin.DsInfo;
import org.junit.BeforeClass;
import org.junit.Test;

public class ZipMapperTest {

	private static ZipMapper mapper;

	@BeforeClass
	public static void beforeClass() {
		mapper = new ZipMapper();
	}

	@Test
	public void testOnly() throws Exception {

		check("FacilityName", "InvName", "VisitId", "DsName", "DfName",
				"ids/FacilityName/InvName/VisitId/DsName/DfName");

		check("FacilityName", "InvName", "VisitId", "DsName", "a/b/c",
				"ids/FacilityName/InvName/VisitId/DsName/a/b/c");

		check("FacilityName", "InvName", "N/A", "DsName", "DfName",
				"ids/FacilityName/InvName/N%2FA/DsName/DfName");

		check("Facility Name", "Inv Name", "Visit Id", "Ds Name", "Df Name",
				"ids/Facility+Name/Inv+Name/Visit+Id/Ds+Name/Df Name");

		check("FacilityName",
				"InvName",
				"VisitId",
				"+áé¿русский",
				"+áé¿русский",
				"ids/FacilityName/InvName/VisitId/%2B%C3%A1%C3%A9%C2%BF%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9/+áé¿русский");
	}

	private void check(String facilityName, String invName, String visitId, String dsName,
			String dfName, String fullName) throws IOException {
		DsInfo dsInfo = new DsInfoImpl(facilityName, invName, visitId, dsName);
		DfInfo dfInfo = new DfInfoImpl(dfName);
		String mFullName = mapper.getFullEntryName(dsInfo, dfInfo);
		assertEquals(fullName, mFullName);
		assertEquals(dfName, mapper.getFileName(mFullName));
	}

}