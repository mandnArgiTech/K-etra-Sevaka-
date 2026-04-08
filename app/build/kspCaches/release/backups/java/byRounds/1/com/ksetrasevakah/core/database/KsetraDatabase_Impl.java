package com.ksetrasevakah.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.ksetrasevakah.core.database.dao.BackupLogDao;
import com.ksetrasevakah.core.database.dao.BackupLogDao_Impl;
import com.ksetrasevakah.core.database.dao.CameraConfigDao;
import com.ksetrasevakah.core.database.dao.CameraConfigDao_Impl;
import com.ksetrasevakah.core.database.dao.ChatMessageDao;
import com.ksetrasevakah.core.database.dao.ChatMessageDao_Impl;
import com.ksetrasevakah.core.database.dao.ChatThreadDao;
import com.ksetrasevakah.core.database.dao.ChatThreadDao_Impl;
import com.ksetrasevakah.core.database.dao.FaultDao;
import com.ksetrasevakah.core.database.dao.FaultDao_Impl;
import com.ksetrasevakah.core.database.dao.MotorStateDao;
import com.ksetrasevakah.core.database.dao.MotorStateDao_Impl;
import com.ksetrasevakah.core.database.dao.PredictionCacheDao;
import com.ksetrasevakah.core.database.dao.PredictionCacheDao_Impl;
import com.ksetrasevakah.core.database.dao.SecurityBriefingDao;
import com.ksetrasevakah.core.database.dao.SecurityBriefingDao_Impl;
import com.ksetrasevakah.core.database.dao.SecurityEventDao;
import com.ksetrasevakah.core.database.dao.SecurityEventDao_Impl;
import com.ksetrasevakah.core.database.dao.TelemetryDao;
import com.ksetrasevakah.core.database.dao.TelemetryDao_Impl;
import com.ksetrasevakah.core.database.dao.WorkerActivityDao;
import com.ksetrasevakah.core.database.dao.WorkerActivityDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class KsetraDatabase_Impl extends KsetraDatabase {
  private volatile MotorStateDao _motorStateDao;

  private volatile TelemetryDao _telemetryDao;

  private volatile FaultDao _faultDao;

  private volatile WorkerActivityDao _workerActivityDao;

  private volatile PredictionCacheDao _predictionCacheDao;

  private volatile ChatThreadDao _chatThreadDao;

  private volatile ChatMessageDao _chatMessageDao;

  private volatile BackupLogDao _backupLogDao;

  private volatile SecurityEventDao _securityEventDao;

  private volatile CameraConfigDao _cameraConfigDao;

  private volatile SecurityBriefingDao _securityBriefingDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `motor_state` (`id` INTEGER NOT NULL, `state` TEXT NOT NULL, `last_on_time` INTEGER, `last_off_time` INTEGER, `current_session_start` INTEGER, `pending_command` TEXT, `pending_since` INTEGER, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `telemetry_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `raw_sms` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `motor_on` INTEGER NOT NULL, `phase_r` REAL, `phase_y` REAL, `phase_b` REAL, `voltage` REAL, `temperature` REAL, `runtime_minutes` INTEGER, `narrative` TEXT)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_telemetry_log_timestamp` ON `telemetry_log` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fault_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `fault_type` TEXT NOT NULL, `description` TEXT NOT NULL, `auto_recovered` INTEGER NOT NULL, `recovery_time` INTEGER)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_fault_log_timestamp` ON `fault_log` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `worker_activity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `on_time` INTEGER, `off_time` INTEGER, `duration_minutes` INTEGER, `forgot_off` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_worker_activity_date` ON `worker_activity` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `prediction_cache` (`type` TEXT NOT NULL, `result_json` TEXT NOT NULL, `confidence` REAL NOT NULL, `computed_at` INTEGER NOT NULL, `valid_until` INTEGER NOT NULL, PRIMARY KEY(`type`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_threads` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `preview` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `last_message_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `thread_id` INTEGER NOT NULL, `role` TEXT NOT NULL, `text` TEXT NOT NULL, `created_at` INTEGER NOT NULL, FOREIGN KEY(`thread_id`) REFERENCES `chat_threads`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_thread_id` ON `chat_messages` (`thread_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `backup_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `type` TEXT NOT NULL, `status` TEXT NOT NULL, `file_size_bytes` INTEGER, `error_message` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `security_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `camera_name` TEXT NOT NULL, `event_type` TEXT NOT NULL, `threat_level` TEXT NOT NULL, `confidence` REAL NOT NULL, `origin_timestamp` INTEGER NOT NULL, `received_timestamp` INTEGER NOT NULL, `hour_of_day` INTEGER NOT NULL, `summary` TEXT, `acknowledged` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_security_events_camera_name` ON `security_events` (`camera_name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_security_events_origin_timestamp` ON `security_events` (`origin_timestamp`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_security_events_threat_level` ON `security_events` (`threat_level`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_security_events_hour_of_day` ON `security_events` (`hour_of_day`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `camera_config` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `camera_name` TEXT NOT NULL, `mode` TEXT NOT NULL, `last_seen` INTEGER NOT NULL, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_camera_config_camera_name` ON `camera_config` (`camera_name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `security_briefings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `generated_at` INTEGER NOT NULL, `period_start` INTEGER NOT NULL, `period_end` INTEGER NOT NULL, `summary` TEXT NOT NULL, `total_events` INTEGER NOT NULL, `critical_count` INTEGER NOT NULL, `high_count` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e26a4aaac0122a11e28bb431a3b5286a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `motor_state`");
        db.execSQL("DROP TABLE IF EXISTS `telemetry_log`");
        db.execSQL("DROP TABLE IF EXISTS `fault_log`");
        db.execSQL("DROP TABLE IF EXISTS `worker_activity`");
        db.execSQL("DROP TABLE IF EXISTS `prediction_cache`");
        db.execSQL("DROP TABLE IF EXISTS `chat_threads`");
        db.execSQL("DROP TABLE IF EXISTS `chat_messages`");
        db.execSQL("DROP TABLE IF EXISTS `backup_log`");
        db.execSQL("DROP TABLE IF EXISTS `security_events`");
        db.execSQL("DROP TABLE IF EXISTS `camera_config`");
        db.execSQL("DROP TABLE IF EXISTS `security_briefings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMotorState = new HashMap<String, TableInfo.Column>(8);
        _columnsMotorState.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotorState.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotorState.put("last_on_time", new TableInfo.Column("last_on_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotorState.put("last_off_time", new TableInfo.Column("last_off_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotorState.put("current_session_start", new TableInfo.Column("current_session_start", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotorState.put("pending_command", new TableInfo.Column("pending_command", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotorState.put("pending_since", new TableInfo.Column("pending_since", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotorState.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMotorState = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMotorState = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMotorState = new TableInfo("motor_state", _columnsMotorState, _foreignKeysMotorState, _indicesMotorState);
        final TableInfo _existingMotorState = TableInfo.read(db, "motor_state");
        if (!_infoMotorState.equals(_existingMotorState)) {
          return new RoomOpenHelper.ValidationResult(false, "motor_state(com.ksetrasevakah.core.database.entity.MotorStateEntity).\n"
                  + " Expected:\n" + _infoMotorState + "\n"
                  + " Found:\n" + _existingMotorState);
        }
        final HashMap<String, TableInfo.Column> _columnsTelemetryLog = new HashMap<String, TableInfo.Column>(11);
        _columnsTelemetryLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("raw_sms", new TableInfo.Column("raw_sms", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("motor_on", new TableInfo.Column("motor_on", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("phase_r", new TableInfo.Column("phase_r", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("phase_y", new TableInfo.Column("phase_y", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("phase_b", new TableInfo.Column("phase_b", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("voltage", new TableInfo.Column("voltage", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("temperature", new TableInfo.Column("temperature", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("runtime_minutes", new TableInfo.Column("runtime_minutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLog.put("narrative", new TableInfo.Column("narrative", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTelemetryLog = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTelemetryLog = new HashSet<TableInfo.Index>(1);
        _indicesTelemetryLog.add(new TableInfo.Index("index_telemetry_log_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoTelemetryLog = new TableInfo("telemetry_log", _columnsTelemetryLog, _foreignKeysTelemetryLog, _indicesTelemetryLog);
        final TableInfo _existingTelemetryLog = TableInfo.read(db, "telemetry_log");
        if (!_infoTelemetryLog.equals(_existingTelemetryLog)) {
          return new RoomOpenHelper.ValidationResult(false, "telemetry_log(com.ksetrasevakah.core.database.entity.TelemetryEntity).\n"
                  + " Expected:\n" + _infoTelemetryLog + "\n"
                  + " Found:\n" + _existingTelemetryLog);
        }
        final HashMap<String, TableInfo.Column> _columnsFaultLog = new HashMap<String, TableInfo.Column>(6);
        _columnsFaultLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaultLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaultLog.put("fault_type", new TableInfo.Column("fault_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaultLog.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaultLog.put("auto_recovered", new TableInfo.Column("auto_recovered", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaultLog.put("recovery_time", new TableInfo.Column("recovery_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFaultLog = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFaultLog = new HashSet<TableInfo.Index>(1);
        _indicesFaultLog.add(new TableInfo.Index("index_fault_log_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoFaultLog = new TableInfo("fault_log", _columnsFaultLog, _foreignKeysFaultLog, _indicesFaultLog);
        final TableInfo _existingFaultLog = TableInfo.read(db, "fault_log");
        if (!_infoFaultLog.equals(_existingFaultLog)) {
          return new RoomOpenHelper.ValidationResult(false, "fault_log(com.ksetrasevakah.core.database.entity.FaultEntity).\n"
                  + " Expected:\n" + _infoFaultLog + "\n"
                  + " Found:\n" + _existingFaultLog);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkerActivity = new HashMap<String, TableInfo.Column>(6);
        _columnsWorkerActivity.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkerActivity.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkerActivity.put("on_time", new TableInfo.Column("on_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkerActivity.put("off_time", new TableInfo.Column("off_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkerActivity.put("duration_minutes", new TableInfo.Column("duration_minutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkerActivity.put("forgot_off", new TableInfo.Column("forgot_off", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkerActivity = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkerActivity = new HashSet<TableInfo.Index>(1);
        _indicesWorkerActivity.add(new TableInfo.Index("index_worker_activity_date", true, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoWorkerActivity = new TableInfo("worker_activity", _columnsWorkerActivity, _foreignKeysWorkerActivity, _indicesWorkerActivity);
        final TableInfo _existingWorkerActivity = TableInfo.read(db, "worker_activity");
        if (!_infoWorkerActivity.equals(_existingWorkerActivity)) {
          return new RoomOpenHelper.ValidationResult(false, "worker_activity(com.ksetrasevakah.core.database.entity.WorkerActivityEntity).\n"
                  + " Expected:\n" + _infoWorkerActivity + "\n"
                  + " Found:\n" + _existingWorkerActivity);
        }
        final HashMap<String, TableInfo.Column> _columnsPredictionCache = new HashMap<String, TableInfo.Column>(5);
        _columnsPredictionCache.put("type", new TableInfo.Column("type", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPredictionCache.put("result_json", new TableInfo.Column("result_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPredictionCache.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPredictionCache.put("computed_at", new TableInfo.Column("computed_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPredictionCache.put("valid_until", new TableInfo.Column("valid_until", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPredictionCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPredictionCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPredictionCache = new TableInfo("prediction_cache", _columnsPredictionCache, _foreignKeysPredictionCache, _indicesPredictionCache);
        final TableInfo _existingPredictionCache = TableInfo.read(db, "prediction_cache");
        if (!_infoPredictionCache.equals(_existingPredictionCache)) {
          return new RoomOpenHelper.ValidationResult(false, "prediction_cache(com.ksetrasevakah.core.database.entity.PredictionCacheEntity).\n"
                  + " Expected:\n" + _infoPredictionCache + "\n"
                  + " Found:\n" + _existingPredictionCache);
        }
        final HashMap<String, TableInfo.Column> _columnsChatThreads = new HashMap<String, TableInfo.Column>(5);
        _columnsChatThreads.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("preview", new TableInfo.Column("preview", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("last_message_at", new TableInfo.Column("last_message_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatThreads = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChatThreads = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChatThreads = new TableInfo("chat_threads", _columnsChatThreads, _foreignKeysChatThreads, _indicesChatThreads);
        final TableInfo _existingChatThreads = TableInfo.read(db, "chat_threads");
        if (!_infoChatThreads.equals(_existingChatThreads)) {
          return new RoomOpenHelper.ValidationResult(false, "chat_threads(com.ksetrasevakah.core.database.entity.ChatThreadEntity).\n"
                  + " Expected:\n" + _infoChatThreads + "\n"
                  + " Found:\n" + _existingChatThreads);
        }
        final HashMap<String, TableInfo.Column> _columnsChatMessages = new HashMap<String, TableInfo.Column>(5);
        _columnsChatMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("thread_id", new TableInfo.Column("thread_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatMessages = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysChatMessages.add(new TableInfo.ForeignKey("chat_threads", "CASCADE", "NO ACTION", Arrays.asList("thread_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesChatMessages = new HashSet<TableInfo.Index>(1);
        _indicesChatMessages.add(new TableInfo.Index("index_chat_messages_thread_id", false, Arrays.asList("thread_id"), Arrays.asList("ASC")));
        final TableInfo _infoChatMessages = new TableInfo("chat_messages", _columnsChatMessages, _foreignKeysChatMessages, _indicesChatMessages);
        final TableInfo _existingChatMessages = TableInfo.read(db, "chat_messages");
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "chat_messages(com.ksetrasevakah.core.database.entity.ChatMessageEntity).\n"
                  + " Expected:\n" + _infoChatMessages + "\n"
                  + " Found:\n" + _existingChatMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsBackupLog = new HashMap<String, TableInfo.Column>(6);
        _columnsBackupLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBackupLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBackupLog.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBackupLog.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBackupLog.put("file_size_bytes", new TableInfo.Column("file_size_bytes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBackupLog.put("error_message", new TableInfo.Column("error_message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBackupLog = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBackupLog = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBackupLog = new TableInfo("backup_log", _columnsBackupLog, _foreignKeysBackupLog, _indicesBackupLog);
        final TableInfo _existingBackupLog = TableInfo.read(db, "backup_log");
        if (!_infoBackupLog.equals(_existingBackupLog)) {
          return new RoomOpenHelper.ValidationResult(false, "backup_log(com.ksetrasevakah.core.database.entity.BackupLogEntity).\n"
                  + " Expected:\n" + _infoBackupLog + "\n"
                  + " Found:\n" + _existingBackupLog);
        }
        final HashMap<String, TableInfo.Column> _columnsSecurityEvents = new HashMap<String, TableInfo.Column>(10);
        _columnsSecurityEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("camera_name", new TableInfo.Column("camera_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("event_type", new TableInfo.Column("event_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("threat_level", new TableInfo.Column("threat_level", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("origin_timestamp", new TableInfo.Column("origin_timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("received_timestamp", new TableInfo.Column("received_timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("hour_of_day", new TableInfo.Column("hour_of_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("summary", new TableInfo.Column("summary", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityEvents.put("acknowledged", new TableInfo.Column("acknowledged", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSecurityEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSecurityEvents = new HashSet<TableInfo.Index>(4);
        _indicesSecurityEvents.add(new TableInfo.Index("index_security_events_camera_name", false, Arrays.asList("camera_name"), Arrays.asList("ASC")));
        _indicesSecurityEvents.add(new TableInfo.Index("index_security_events_origin_timestamp", false, Arrays.asList("origin_timestamp"), Arrays.asList("ASC")));
        _indicesSecurityEvents.add(new TableInfo.Index("index_security_events_threat_level", false, Arrays.asList("threat_level"), Arrays.asList("ASC")));
        _indicesSecurityEvents.add(new TableInfo.Index("index_security_events_hour_of_day", false, Arrays.asList("hour_of_day"), Arrays.asList("ASC")));
        final TableInfo _infoSecurityEvents = new TableInfo("security_events", _columnsSecurityEvents, _foreignKeysSecurityEvents, _indicesSecurityEvents);
        final TableInfo _existingSecurityEvents = TableInfo.read(db, "security_events");
        if (!_infoSecurityEvents.equals(_existingSecurityEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "security_events(com.ksetrasevakah.core.database.entity.SecurityEventEntity).\n"
                  + " Expected:\n" + _infoSecurityEvents + "\n"
                  + " Found:\n" + _existingSecurityEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsCameraConfig = new HashMap<String, TableInfo.Column>(5);
        _columnsCameraConfig.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCameraConfig.put("camera_name", new TableInfo.Column("camera_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCameraConfig.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCameraConfig.put("last_seen", new TableInfo.Column("last_seen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCameraConfig.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCameraConfig = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCameraConfig = new HashSet<TableInfo.Index>(1);
        _indicesCameraConfig.add(new TableInfo.Index("index_camera_config_camera_name", true, Arrays.asList("camera_name"), Arrays.asList("ASC")));
        final TableInfo _infoCameraConfig = new TableInfo("camera_config", _columnsCameraConfig, _foreignKeysCameraConfig, _indicesCameraConfig);
        final TableInfo _existingCameraConfig = TableInfo.read(db, "camera_config");
        if (!_infoCameraConfig.equals(_existingCameraConfig)) {
          return new RoomOpenHelper.ValidationResult(false, "camera_config(com.ksetrasevakah.core.database.entity.CameraConfigEntity).\n"
                  + " Expected:\n" + _infoCameraConfig + "\n"
                  + " Found:\n" + _existingCameraConfig);
        }
        final HashMap<String, TableInfo.Column> _columnsSecurityBriefings = new HashMap<String, TableInfo.Column>(8);
        _columnsSecurityBriefings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityBriefings.put("generated_at", new TableInfo.Column("generated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityBriefings.put("period_start", new TableInfo.Column("period_start", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityBriefings.put("period_end", new TableInfo.Column("period_end", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityBriefings.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityBriefings.put("total_events", new TableInfo.Column("total_events", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityBriefings.put("critical_count", new TableInfo.Column("critical_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecurityBriefings.put("high_count", new TableInfo.Column("high_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSecurityBriefings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSecurityBriefings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSecurityBriefings = new TableInfo("security_briefings", _columnsSecurityBriefings, _foreignKeysSecurityBriefings, _indicesSecurityBriefings);
        final TableInfo _existingSecurityBriefings = TableInfo.read(db, "security_briefings");
        if (!_infoSecurityBriefings.equals(_existingSecurityBriefings)) {
          return new RoomOpenHelper.ValidationResult(false, "security_briefings(com.ksetrasevakah.core.database.entity.SecurityBriefingEntity).\n"
                  + " Expected:\n" + _infoSecurityBriefings + "\n"
                  + " Found:\n" + _existingSecurityBriefings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e26a4aaac0122a11e28bb431a3b5286a", "e1472dc6f0346a3d45a53f283a5c30d7");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "motor_state","telemetry_log","fault_log","worker_activity","prediction_cache","chat_threads","chat_messages","backup_log","security_events","camera_config","security_briefings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `motor_state`");
      _db.execSQL("DELETE FROM `telemetry_log`");
      _db.execSQL("DELETE FROM `fault_log`");
      _db.execSQL("DELETE FROM `worker_activity`");
      _db.execSQL("DELETE FROM `prediction_cache`");
      _db.execSQL("DELETE FROM `chat_threads`");
      _db.execSQL("DELETE FROM `chat_messages`");
      _db.execSQL("DELETE FROM `backup_log`");
      _db.execSQL("DELETE FROM `security_events`");
      _db.execSQL("DELETE FROM `camera_config`");
      _db.execSQL("DELETE FROM `security_briefings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MotorStateDao.class, MotorStateDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TelemetryDao.class, TelemetryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FaultDao.class, FaultDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkerActivityDao.class, WorkerActivityDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PredictionCacheDao.class, PredictionCacheDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatThreadDao.class, ChatThreadDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatMessageDao.class, ChatMessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BackupLogDao.class, BackupLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SecurityEventDao.class, SecurityEventDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CameraConfigDao.class, CameraConfigDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SecurityBriefingDao.class, SecurityBriefingDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MotorStateDao motorStateDao() {
    if (_motorStateDao != null) {
      return _motorStateDao;
    } else {
      synchronized(this) {
        if(_motorStateDao == null) {
          _motorStateDao = new MotorStateDao_Impl(this);
        }
        return _motorStateDao;
      }
    }
  }

  @Override
  public TelemetryDao telemetryDao() {
    if (_telemetryDao != null) {
      return _telemetryDao;
    } else {
      synchronized(this) {
        if(_telemetryDao == null) {
          _telemetryDao = new TelemetryDao_Impl(this);
        }
        return _telemetryDao;
      }
    }
  }

  @Override
  public FaultDao faultDao() {
    if (_faultDao != null) {
      return _faultDao;
    } else {
      synchronized(this) {
        if(_faultDao == null) {
          _faultDao = new FaultDao_Impl(this);
        }
        return _faultDao;
      }
    }
  }

  @Override
  public WorkerActivityDao workerActivityDao() {
    if (_workerActivityDao != null) {
      return _workerActivityDao;
    } else {
      synchronized(this) {
        if(_workerActivityDao == null) {
          _workerActivityDao = new WorkerActivityDao_Impl(this);
        }
        return _workerActivityDao;
      }
    }
  }

  @Override
  public PredictionCacheDao predictionCacheDao() {
    if (_predictionCacheDao != null) {
      return _predictionCacheDao;
    } else {
      synchronized(this) {
        if(_predictionCacheDao == null) {
          _predictionCacheDao = new PredictionCacheDao_Impl(this);
        }
        return _predictionCacheDao;
      }
    }
  }

  @Override
  public ChatThreadDao chatThreadDao() {
    if (_chatThreadDao != null) {
      return _chatThreadDao;
    } else {
      synchronized(this) {
        if(_chatThreadDao == null) {
          _chatThreadDao = new ChatThreadDao_Impl(this);
        }
        return _chatThreadDao;
      }
    }
  }

  @Override
  public ChatMessageDao chatMessageDao() {
    if (_chatMessageDao != null) {
      return _chatMessageDao;
    } else {
      synchronized(this) {
        if(_chatMessageDao == null) {
          _chatMessageDao = new ChatMessageDao_Impl(this);
        }
        return _chatMessageDao;
      }
    }
  }

  @Override
  public BackupLogDao backupLogDao() {
    if (_backupLogDao != null) {
      return _backupLogDao;
    } else {
      synchronized(this) {
        if(_backupLogDao == null) {
          _backupLogDao = new BackupLogDao_Impl(this);
        }
        return _backupLogDao;
      }
    }
  }

  @Override
  public SecurityEventDao securityEventDao() {
    if (_securityEventDao != null) {
      return _securityEventDao;
    } else {
      synchronized(this) {
        if(_securityEventDao == null) {
          _securityEventDao = new SecurityEventDao_Impl(this);
        }
        return _securityEventDao;
      }
    }
  }

  @Override
  public CameraConfigDao cameraConfigDao() {
    if (_cameraConfigDao != null) {
      return _cameraConfigDao;
    } else {
      synchronized(this) {
        if(_cameraConfigDao == null) {
          _cameraConfigDao = new CameraConfigDao_Impl(this);
        }
        return _cameraConfigDao;
      }
    }
  }

  @Override
  public SecurityBriefingDao securityBriefingDao() {
    if (_securityBriefingDao != null) {
      return _securityBriefingDao;
    } else {
      synchronized(this) {
        if(_securityBriefingDao == null) {
          _securityBriefingDao = new SecurityBriefingDao_Impl(this);
        }
        return _securityBriefingDao;
      }
    }
  }
}
