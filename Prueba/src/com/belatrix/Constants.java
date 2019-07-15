package com.belatrix;
import java.io.Serializable;

public class Constants implements Serializable{
	private static final long serialVersionUID = 8799656478674716638L;
	
	public static final String MESSAGE_ERRORWARNING= "Error or Warning or Message must be specified";
	public static final String MESSAGE_INVALIDCONFIGURATION = "Invalid configuration";
	
	public static final String RUTA_ARCHIVO_LOG = "/logFile1.txt";
	
	public static final String ERROR_CREATECONNECTIONDB = "Ocurrió un problema al crear conexión con la base de datos";
	public static final String ERROR_CREATEFILE = "Ocurrió un error al crear archivo";
	public static final String ERROR_DB = "Ocurrió un problema con la base de datos";
}
