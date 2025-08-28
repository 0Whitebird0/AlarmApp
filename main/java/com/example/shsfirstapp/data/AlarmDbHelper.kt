package com.example.shsfirstapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class AlarmDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AlarmDB"
        private const val DATABASE_VERSION = 3

        // 테이블 및 컬럼 상수
        const val TABLE_ALARMS = "alarms"
        const val COLUMN_ID = "_id"
        const val COLUMN_TIME = "time"
        const val COLUMN_DATE = "date"
        const val COLUMN_EVENT = "event"
        const val COLUMN_DAYS = "days"
        const val COLUMN_ALARM_MP3_PATH = "alarm_mp3_path"
        const val COLUMN_ENABLED = "enabled"

        const val TABLE_MP3 = "mp3"
        const val COLUMN_MP3_ID = "_id"
        const val COLUMN_MP3_NAME = "name"
        const val COLUMN_MP3_PATH = "mp3_path"

        @Volatile
        private var INSTANCE: AlarmDbHelper? = null

        fun getInstance(context: Context): AlarmDbHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AlarmDbHelper(context.applicationContext).also { INSTANCE = it }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_ALARMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_EVENT TEXT NOT NULL,
                $COLUMN_DAYS TEXT NOT NULL,
                $COLUMN_ALARM_MP3_PATH TEXT,
                $COLUMN_ENABLED INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_MP3 (
                $COLUMN_MP3_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MP3_NAME TEXT NOT NULL,
                $COLUMN_MP3_PATH TEXT NOT NULL UNIQUE
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                db.execSQL("ALTER TABLE $TABLE_ALARMS ADD COLUMN $COLUMN_DATE TEXT NOT NULL DEFAULT '${getCurrentDate()}'")
                db.execSQL("ALTER TABLE $TABLE_ALARMS ADD COLUMN $COLUMN_ENABLED INTEGER NOT NULL DEFAULT 1")
            }
            2 -> {
                db.execSQL("ALTER TABLE $TABLE_ALARMS ADD COLUMN $COLUMN_ENABLED INTEGER NOT NULL DEFAULT 1")
            }
        }
    }

    fun getAllMp3s(): List<Mp3> {
        val mp3List = mutableListOf<Mp3>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MP3,
            arrayOf(COLUMN_MP3_ID, COLUMN_MP3_NAME, COLUMN_MP3_PATH),
            null, null, null, null, null
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_MP3_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_MP3_NAME))
                val path = it.getString(it.getColumnIndexOrThrow(COLUMN_MP3_PATH))
                mp3List.add(Mp3(id, name, path))
            }
        }
        return mp3List
    }

    fun getAllAlarms(): List<Alarm> {
        val alarms = mutableListOf<Alarm>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ALARMS,
            null,
            null, null, null, null, null
        )
        cursor.use {
            while (it.moveToNext()) {
                alarms.add(
                    Alarm(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        time = it.getString(it.getColumnIndexOrThrow(COLUMN_TIME)),
                        date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                        event = it.getString(it.getColumnIndexOrThrow(COLUMN_EVENT)),
                        days = it.getString(it.getColumnIndexOrThrow(COLUMN_DAYS)),
                        mp3Path = it.getString(it.getColumnIndexOrThrow(COLUMN_ALARM_MP3_PATH)),
                        enabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_ENABLED)) == 1
                    )
                )
            }
        }
        return alarms
    }

    fun getAlarmById(alarmId: Long): Alarm? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ALARMS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(alarmId.toString()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return Alarm(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    time = it.getString(it.getColumnIndexOrThrow(COLUMN_TIME)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                    event = it.getString(it.getColumnIndexOrThrow(COLUMN_EVENT)),
                    days = it.getString(it.getColumnIndexOrThrow(COLUMN_DAYS)),
                    mp3Path = it.getString(it.getColumnIndexOrThrow(COLUMN_ALARM_MP3_PATH)),
                    enabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_ENABLED)) == 1
                )
            }
        }
        return null
    }

    fun insertAlarm(alarm: Alarm): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TIME, alarm.time)
            put(COLUMN_DATE, alarm.date)
            put(COLUMN_EVENT, alarm.event)
            put(COLUMN_DAYS, alarm.days)
            put(COLUMN_ALARM_MP3_PATH, alarm.mp3Path)
            put(COLUMN_ENABLED, if (alarm.enabled) 1 else 0)
        }
        val newId = db.insert(TABLE_ALARMS, null, values)
        db.close()
        return newId // 새로 생성된 ID 반환
    }

    fun deleteAlarm(alarmId: Long): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_ALARMS,
            "$COLUMN_ID = ?",
            arrayOf(alarmId.toString())
        )
    }

    fun updateAllAlarmsEnabled(enabled: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ENABLED, if (enabled) 1 else 0)
        }
        return db.update(
            TABLE_ALARMS,
            values,
            null,
            null
        )
    }
    fun updateAlarmEnabled(alarmId: Long, enabled: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ENABLED, if (enabled) 1 else 0)
        }
        return db.update(
            TABLE_ALARMS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(alarmId.toString())
        )
    }


    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
