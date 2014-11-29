package com.asimov.healthwalk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.util.Log;

public class RepositorioLocalizaciones {
	private Context mContext;
	private final int TAM_BUFFER = 1024;
	private String nombre_bd;
	private final String TAG;
	private SQLiteDatabase bd;

	RepositorioLocalizaciones(Context contexto, String base_datos, String log_tag){
		TAG = log_tag;
		mContext = contexto;
		setBaseDatos(base_datos);
		String path = getPathBD();
		bd = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Log.d(TAG, "Base de datos abierta desde " + path);
	}

	public void close(){
		bd.close();
	}
	
	public void setBaseDatos(String base_datos){
		String path_bd = getPathBD(base_datos);
		if(!existeBaseDatos(path_bd)){
			Log.d(TAG, "Creando base de datos");
			// TODO: gestionar excepciones propias
			copiarBaseDatos();
		}else{
			Log.d(TAG, "La base de datos ya existe");
			nombre_bd = base_datos;
		}
	}
	
	public String getPathBD(String base_datos){
		// TODO: usar getFilesDir
		return "/data/data/com.asimov.healthwalk/databases/" + base_datos;
	}
	
	public String getPathBD(){
		return getPathBD(nombre_bd);
	}

	private void copiarBaseDatos(){
		InputStream input = null;
		try{
			input = mContext.getAssets().open(nombre_bd);
		} catch(IOException e){
			// TODO: deberiamos informar al usuario
			Log.e(TAG, "No se ha encontrado a " + nombre_bd + " en el directorio assets.");
			return;
		}

		String path_bd = getPathBD();
		File databases = new File(path_bd).getParentFile();
		if(!databases.exists()){
			// TODO: deberiamos informar al usuario
			Log.d(TAG, "Creando directorio databases en " + databases.getAbsolutePath());
			databases.mkdir();
		}
		OutputStream output;
		try {
			output = new FileOutputStream(path_bd);
		} catch (FileNotFoundException e) {
			// TODO: deberiamos informar al usuario
			Log.e(TAG, "No se ha encontrado la base de datos " + nombre_bd + " en " + path_bd);
			try {
				input.close();
			} catch (IOException e1) {
			}
			return;
		}
		
		byte[] buffer = new byte[TAM_BUFFER];
		int length;
		try{
			while((length = input.read(buffer)) > 0){
				output.write(buffer, 0, length);
			}
		} catch(IOException e){
		}
		Log.d(TAG, "Base de datos copiada");
		
		// TODO: todo esto es traumatico
		try {
			output.flush();
		} catch (IOException e) {
		}

		try {
			output.close();
		} catch (IOException e) {
		}

		try {
			input.close();
		} catch (IOException e) {
		}
	}
	
	private boolean existeBaseDatos(String path_bd){
		SQLiteDatabase checkDB = null;
		try{
			checkDB = SQLiteDatabase.openDatabase(path_bd, null, SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} catch (SQLiteException e){
			return false;
		}
		
		return checkDB != null ? true : false;
	}
}
