package pt.uminho.sysbio.common.bioapis.externalAPI;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

public class TestDates {

	@Test
	public void test() {
		Locale.setDefault(Locale.US);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd");
		try {
			Date date1 = sdf.parse("2015/Apr/18");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

}
