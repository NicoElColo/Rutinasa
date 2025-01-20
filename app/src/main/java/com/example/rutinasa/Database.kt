package com.example.rutinasa

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

/// TODO: Resolver el pasaje entre activitys y la logica de visualizacion (Youtube)

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tablas aquí
        val createRoutineQuery = """
            CREATE TABLE IF NOT EXISTS Routine (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT not null,
                descripcion TEXT,
                CONSTRAINT unique_nombre UNIQUE (nombre)
            )
        """

        db.execSQL(createRoutineQuery)

        val createDayQuery = """
            CREATE TABLE IF NOT EXISTS Day (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_routine INTEGER not null,
                nombre TEXT not null,
                descripcion TEXT,
                CONSTRAINT unique_nombre UNIQUE (nombre, id_routine),
                CONSTRAINT fk_routine FOREIGN KEY (id_routine) REFERENCES Routine(id) ON DELETE CASCADE
            )
        """

        db.execSQL(createDayQuery)

        val createExerciseQuery = """
            CREATE TABLE IF NOT EXISTS Exercise (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_day INTEGER not null,
                nombre TEXT not null,
                series INTEGER not null,
                repeticiones INTEGER not null,
                descripcion TEXT,
                CONSTRAINT fk_day FOREIGN KEY (id_day) REFERENCES Day(id) ON DELETE CASCADE
            )
        """

        db.execSQL(createExerciseQuery)

        val createHistoryQuery = """
            CREATE TABLE IF NOT EXISTS History (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_exercise INTEGER not null,
                fecha DATE not null,
                peso FLOAT not null,
                CONSTRAINT unique_fecha UNIQUE (id_exercise, fecha),
                CONSTRAINT fk_exercise FOREIGN KEY (id_exercise) REFERENCES Exercise(id) ON DELETE CASCADE
            )
        """

        db.execSQL(createHistoryQuery)

        // Habilitamos las foreigns keys
        db.execSQL("PRAGMA foreign_keys = ON")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Actualizar la base de datos si es necesario
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Routine")
            db.execSQL("DROP TABLE IF EXISTS Day")
            db.execSQL("DROP TABLE IF EXISTS Exercise")
            db.execSQL("DROP TABLE IF EXISTS History")
            onCreate(db)
        }
    }

    /// Funciones para insertar datos

    fun insertRoutine(nombre: String, descripcion: String = "") : Int {
        val datos = ContentValues()
        datos.put("nombre", nombre)
        datos.put("descripcion", descripcion)

        val db = this.writableDatabase
        val id = db.insert("Routine", null, datos)
        db.close()
        return id.toInt()
    }

    fun insertDay(id_routine: Int, nombre: String, descripcion: String = "") : Int{
        val datos = ContentValues()
        datos.put("id_routine", id_routine)
        datos.put("nombre", nombre)
        datos.put("descripcion", descripcion)

        val db = this.writableDatabase
        val id = db.insert("Day", null, datos)
        db.close()
        return id.toInt()
    }

    fun insertExercise(id_day: Int, nombre: String, series: Int, repeticiones: Int, descripcion: String = "") : Int {
        val datos = ContentValues()
        datos.put("id_day", id_day)
        datos.put("nombre", nombre)
        datos.put("series", series)
        datos.put("repeticiones", repeticiones)
        datos.put("descripcion", descripcion)

        val db = this.writableDatabase
        val id = db.insert("Exercise", null, datos)
        db.close()
        return id.toInt()
    }

    fun insertHistory(id_exercise: Int, fecha: Long, peso: Float) : Int{
        val datos = ContentValues()
        datos.put("id_exercise", id_exercise)
        datos.put("fecha", fecha)
        datos.put("peso", peso)

        val db = this.writableDatabase
        val id = db.insert("History", null, datos)
        db.close()
        return id.toInt()
    }


    /// Funciones para actualizar datos

    fun updateRoutine(id: Int, nombre: String, descripcion: String = "") {
        val args = arrayOf(id.toString())

        val datos = ContentValues()
        datos.put("nombre", nombre)
        datos.put("descripcion", descripcion)

        val db = this.writableDatabase
        val rowCount = db.update("Routine", datos, "id = ?", args)
        db.close()
    }


    /// Funciones para borrar datos

    fun deleteRoutine(nombre: String) {
        val args = arrayOf(nombre)

        val db = this.writableDatabase
        val rowCount: Int = db.delete("Routine", "nombre = ?", args)
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "rutinasaDatabase.db"
        private const val DATABASE_VERSION = 1
    }
}

object DatabaseManager {
    private var dbHelper: MyDatabaseHelper? = null
    private var database: SQLiteDatabase? = null

    fun initialize(context: Context) {
        if (dbHelper == null) {
            dbHelper = MyDatabaseHelper(context.applicationContext)
        }
    }

    fun openDatabase() {
        if (database == null || !database!!.isOpen) {
            database = dbHelper?.writableDatabase
        }
    }

    fun closeDatabase() {
        database?.close()
        database = null
    }

    fun getDatabase(): SQLiteDatabase {
        if (database == null || !database!!.isOpen) {
            throw IllegalStateException("Database is not open. Call openDatabase() first.")
        }
        return database!!
    }

    fun insertRoutine(nombre: String, descripcion: String = "") : Int {
        return dbHelper!!.insertRoutine(nombre, descripcion)
    }

    fun insertDay(idRoutine: Int, nombre: String, descripcion: String = "")  : Int {
        return dbHelper!!.insertDay(idRoutine, nombre, descripcion)
    }

    fun insertExercise(id_day: Int, nombre: String, series: Int, repeticiones: Int, descripcion: String = "")  : Int? {
        return dbHelper?.insertExercise(id_day, nombre, series, repeticiones, descripcion)
    }

    fun insertHistory(id_exercise: Int, fecha: Long, peso: Float) {
        dbHelper?.insertHistory(id_exercise, fecha, peso)
    }
}
