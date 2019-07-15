package com.belatrix;
import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * Clase para manejo de log de errores, advertencias
 *
 */
public class JobLogger implements Serializable{
	private static final long serialVersionUID = 8799656478674716638L;
	private static boolean logToFile;  
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	private static Map dbParams;
	private static Logger logger;
	
	
	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
			boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
		logger = Logger.getLogger("MyLog");  
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		dbParams = dbParamsMap;
	}

	public static void logMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
		//Validar si existe mensaje
		if(!validateMessage(messageText)) {
			return;
		}
		
		//Obtener identificador
		int t= validateErrorAndWarning(message, warning, error);

		//Validación si log de console, archivo, base de datos están en false
		if (!logToConsole && !logToFile && !logToDatabase) {
			throw new Exception(Constants.MESSAGE_INVALIDCONFIGURATION);
		}else {
			printSaveLog(t, messageText, message);
		}


	}
	
	/*
	 * Método que valida si mensaje es nulo o vacío
	 */
	public static boolean validateMessage(String messageText) {
		if (messageText == null || messageText.isEmpty()) {
			return false;
		}else {
			return true;
		}
	}
	
	/*
	 * Método para validar si están habilitados los logs de Error, Advertencia
	 * También devuelve el indicador de log para insertarlo en base de datos
	 */
	public static int validateErrorAndWarning(boolean message, boolean warning, boolean error) throws Exception{
		//Inicializar identificador
		int t= 0;
		//Validar que no estén deshabilitados los logs
		if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
			throw new Exception(Constants.MESSAGE_ERRORWARNING);
		}	

		if(warning && logWarning) {
			t = 3;
		}else if(error && logError) {
			t = 2;
		}else if(message && logMessage){
			t = 1;
		}
		
		return t;
	}
	
	/*
	 * Método para obtener conexión de base de datos
	 */
	public static Connection getDBConnection() throws Exception {
		Connection connection = null;
		Properties connectionProps = new Properties();
		

		try {
			connectionProps.put("user", dbParams.get("userName"));
			connectionProps.put("password", dbParams.get("password"));
			connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
					+ ":" + dbParams.get("portNumber") + "/", connectionProps);
		} catch (SQLException e) {
			logger.log(Level.INFO, Constants.ERROR_CREATECONNECTIONDB);
		}
		
		return connection;
	}
	
	
	/*
	 * Método para pintar log en archivo, en consola o guardar en base de datos
	 */
	public static void printSaveLog(int t, String messageText, boolean message) throws Exception {
		
		if(logToFile || logToConsole) {
			logger.log(Level.INFO, messageText);
			
			if(logToFile) {
				try {
					File logFile = new File(dbParams.get("logFileFolder") + Constants.RUTA_ARCHIVO_LOG);
					if (!logFile.exists()) {
						logFile.createNewFile();
					}
					
					FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + Constants.RUTA_ARCHIVO_LOG);
					logger.addHandler(fh);
				}
				catch (Exception e) {
					logger.log(Level.INFO, Constants.ERROR_CREATEFILE);
				}
			}
			
			if(logToConsole) {
				ConsoleHandler ch = new ConsoleHandler();
				logger.addHandler(ch);
			}	
		}

		
		if(logToDatabase) {
			//Insertar log
			Connection connection = getDBConnection();
			if(connection != null) {
				Statement stmt = connection.createStatement();
				stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) + ")");
			}
		}
	}
}
