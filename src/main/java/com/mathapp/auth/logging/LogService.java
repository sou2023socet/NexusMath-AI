package com.mathapp.auth.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger log =
            LoggerFactory.getLogger(LogService.class);


    public void info(String message) {
        log.info(message);
    }


    public void info(String message, Object... args) {
        log.info(message, args);
    }


    public void debug(String message) {
        log.debug(message);
    }


    public void debug(String message, Object... args) {
        log.debug(message, args);
    }


    public void warn(String message) {
        log.warn(message);
    }


    public void warn(String message, Object... args) {
        log.warn(message, args);
    }


    public void error(String message) {
        log.error(message);
    }


    public void error(String message, Exception ex) {
        log.error(message, ex);
    }


	public void api(String method, String uri, int status, long time) {

		log.info("""

				================= API LOG =================
				Method : {}
				URI    : {}
				Status : {}
				Time   : {} ms
				===========================================
				""", method, uri, status, time);
	}

	public void databaseSuccess(String url, String database, String user) {

		log.info("""

				=============== DATABASE =================
				Status   : CONNECTED
				URL      : {}
				Database : {}
				User     : {}
				==========================================
				""", url, database, user);
	}

	public void databaseFailed(Exception ex) {

		log.error("""

				=============== DATABASE =================
				Status : FAILED
				Message: {}
				==========================================
				""", ex.getMessage(), ex);
	}
}