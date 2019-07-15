package com.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import com.belatrix.JobLogger;

public class Test {

	private JobLogger jobLog;
	
	@org.junit.Test
	public final void testJobLogger() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("logFileFolder", "C:\\");
		data.put("userName", "ecordova");
		data.put("password", "pass");
		jobLog = new JobLogger(true, true, true, true, true, true, data);
		try {
			
			JobLogger.logMessage("mensaje", true, true, true);
			fail("Se esperaba Exception");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
