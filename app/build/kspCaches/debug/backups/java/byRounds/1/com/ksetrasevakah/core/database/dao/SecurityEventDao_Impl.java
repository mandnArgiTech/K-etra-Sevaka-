package com.ksetrasevakah.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ksetrasevakah.core.database.entity.SecurityEventEntity;
import com.ksetrasevakah.core.database.model.CameraCount;
import com.ksetrasevakah.core.database.model.HourlyCount;
import com.ksetrasevakah.core.database.model.ThreatCount;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SecurityEventDao_Impl implements SecurityEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SecurityEventEntity> __insertionAdapterOfSecurityEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfAcknowledge;

  public SecurityEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSecurityEventEntity = new EntityInsertionAdapter<SecurityEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `security_events` (`id`,`camera_name`,`event_type`,`threat_level`,`confidence`,`origin_timestamp`,`received_timestamp`,`hour_of_day`,`summary`,`acknowledged`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SecurityEventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCameraName());
        statement.bindString(3, entity.getEventType());
        statement.bindString(4, entity.getThreatLevel());
        statement.bindDouble(5, entity.getConfidence());
        statement.bindLong(6, entity.getOriginTimestamp());
        statement.bindLong(7, entity.getReceivedTimestamp());
        statement.bindLong(8, entity.getHourOfDay());
        if (entity.getSummary() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getSummary());
        }
        final int _tmp = entity.getAcknowledged() ? 1 : 0;
        statement.bindLong(10, _tmp);
      }
    };
    this.__preparedStmtOfAcknowledge = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE security_events SET acknowledged = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SecurityEventEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSecurityEventEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object acknowledge(final long eventId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAcknowledge.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, eventId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfAcknowledge.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SecurityEventEntity>> getRecentEvents(final long since, final int limit) {
    final String _sql = "SELECT * FROM security_events WHERE origin_timestamp >= ? ORDER BY origin_timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"security_events"}, new Callable<List<SecurityEventEntity>>() {
      @Override
      @NonNull
      public List<SecurityEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCameraName = CursorUtil.getColumnIndexOrThrow(_cursor, "camera_name");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "event_type");
          final int _cursorIndexOfThreatLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "threat_level");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfOriginTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "origin_timestamp");
          final int _cursorIndexOfReceivedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "received_timestamp");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hour_of_day");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfAcknowledged = CursorUtil.getColumnIndexOrThrow(_cursor, "acknowledged");
          final List<SecurityEventEntity> _result = new ArrayList<SecurityEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecurityEventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCameraName;
            _tmpCameraName = _cursor.getString(_cursorIndexOfCameraName);
            final String _tmpEventType;
            _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
            final String _tmpThreatLevel;
            _tmpThreatLevel = _cursor.getString(_cursorIndexOfThreatLevel);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpOriginTimestamp;
            _tmpOriginTimestamp = _cursor.getLong(_cursorIndexOfOriginTimestamp);
            final long _tmpReceivedTimestamp;
            _tmpReceivedTimestamp = _cursor.getLong(_cursorIndexOfReceivedTimestamp);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final String _tmpSummary;
            if (_cursor.isNull(_cursorIndexOfSummary)) {
              _tmpSummary = null;
            } else {
              _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            }
            final boolean _tmpAcknowledged;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAcknowledged);
            _tmpAcknowledged = _tmp != 0;
            _item = new SecurityEventEntity(_tmpId,_tmpCameraName,_tmpEventType,_tmpThreatLevel,_tmpConfidence,_tmpOriginTimestamp,_tmpReceivedTimestamp,_tmpHourOfDay,_tmpSummary,_tmpAcknowledged);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getThreatDistribution(final long since,
      final Continuation<? super List<ThreatCount>> $completion) {
    final String _sql = "SELECT threat_level, COUNT(*) as count FROM security_events WHERE origin_timestamp >= ? GROUP BY threat_level";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ThreatCount>>() {
      @Override
      @NonNull
      public List<ThreatCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfThreatLevel = 0;
          final int _cursorIndexOfCount = 1;
          final List<ThreatCount> _result = new ArrayList<ThreatCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ThreatCount _item;
            final String _tmpThreatLevel;
            _tmpThreatLevel = _cursor.getString(_cursorIndexOfThreatLevel);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new ThreatCount(_tmpThreatLevel,_tmpCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getHourlyHeatmap(final long since,
      final Continuation<? super List<HourlyCount>> $completion) {
    final String _sql = "SELECT hour_of_day, COUNT(*) as count FROM security_events WHERE origin_timestamp >= ? GROUP BY hour_of_day ORDER BY hour_of_day";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HourlyCount>>() {
      @Override
      @NonNull
      public List<HourlyCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHourOfDay = 0;
          final int _cursorIndexOfCount = 1;
          final List<HourlyCount> _result = new ArrayList<HourlyCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HourlyCount _item;
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new HourlyCount(_tmpHourOfDay,_tmpCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActivitySpikes(final long windowStart, final long windowEnd,
      final Continuation<? super List<CameraCount>> $completion) {
    final String _sql = "SELECT camera_name, COUNT(*) as count FROM security_events WHERE origin_timestamp >= ? AND origin_timestamp <= ? GROUP BY camera_name ORDER BY count DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, windowStart);
    _argIndex = 2;
    _statement.bindLong(_argIndex, windowEnd);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CameraCount>>() {
      @Override
      @NonNull
      public List<CameraCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCameraName = 0;
          final int _cursorIndexOfCount = 1;
          final List<CameraCount> _result = new ArrayList<CameraCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CameraCount _item;
            final String _tmpCameraName;
            _tmpCameraName = _cursor.getString(_cursorIndexOfCameraName);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new CameraCount(_tmpCameraName,_tmpCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getEventsForCamera(final String cameraName, final int limit,
      final Continuation<? super List<SecurityEventEntity>> $completion) {
    final String _sql = "SELECT * FROM security_events WHERE camera_name = ? ORDER BY origin_timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cameraName);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecurityEventEntity>>() {
      @Override
      @NonNull
      public List<SecurityEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCameraName = CursorUtil.getColumnIndexOrThrow(_cursor, "camera_name");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "event_type");
          final int _cursorIndexOfThreatLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "threat_level");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfOriginTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "origin_timestamp");
          final int _cursorIndexOfReceivedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "received_timestamp");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hour_of_day");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfAcknowledged = CursorUtil.getColumnIndexOrThrow(_cursor, "acknowledged");
          final List<SecurityEventEntity> _result = new ArrayList<SecurityEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecurityEventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCameraName;
            _tmpCameraName = _cursor.getString(_cursorIndexOfCameraName);
            final String _tmpEventType;
            _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
            final String _tmpThreatLevel;
            _tmpThreatLevel = _cursor.getString(_cursorIndexOfThreatLevel);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpOriginTimestamp;
            _tmpOriginTimestamp = _cursor.getLong(_cursorIndexOfOriginTimestamp);
            final long _tmpReceivedTimestamp;
            _tmpReceivedTimestamp = _cursor.getLong(_cursorIndexOfReceivedTimestamp);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final String _tmpSummary;
            if (_cursor.isNull(_cursorIndexOfSummary)) {
              _tmpSummary = null;
            } else {
              _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            }
            final boolean _tmpAcknowledged;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAcknowledged);
            _tmpAcknowledged = _tmp != 0;
            _item = new SecurityEventEntity(_tmpId,_tmpCameraName,_tmpEventType,_tmpThreatLevel,_tmpConfidence,_tmpOriginTimestamp,_tmpReceivedTimestamp,_tmpHourOfDay,_tmpSummary,_tmpAcknowledged);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnacknowledgedHighCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM security_events WHERE threat_level IN ('HIGH', 'CRITICAL') AND acknowledged = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecentEventsList(final long since,
      final Continuation<? super List<SecurityEventEntity>> $completion) {
    final String _sql = "SELECT * FROM security_events WHERE origin_timestamp >= ? ORDER BY origin_timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecurityEventEntity>>() {
      @Override
      @NonNull
      public List<SecurityEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCameraName = CursorUtil.getColumnIndexOrThrow(_cursor, "camera_name");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "event_type");
          final int _cursorIndexOfThreatLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "threat_level");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfOriginTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "origin_timestamp");
          final int _cursorIndexOfReceivedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "received_timestamp");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hour_of_day");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfAcknowledged = CursorUtil.getColumnIndexOrThrow(_cursor, "acknowledged");
          final List<SecurityEventEntity> _result = new ArrayList<SecurityEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecurityEventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCameraName;
            _tmpCameraName = _cursor.getString(_cursorIndexOfCameraName);
            final String _tmpEventType;
            _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
            final String _tmpThreatLevel;
            _tmpThreatLevel = _cursor.getString(_cursorIndexOfThreatLevel);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpOriginTimestamp;
            _tmpOriginTimestamp = _cursor.getLong(_cursorIndexOfOriginTimestamp);
            final long _tmpReceivedTimestamp;
            _tmpReceivedTimestamp = _cursor.getLong(_cursorIndexOfReceivedTimestamp);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final String _tmpSummary;
            if (_cursor.isNull(_cursorIndexOfSummary)) {
              _tmpSummary = null;
            } else {
              _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            }
            final boolean _tmpAcknowledged;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAcknowledged);
            _tmpAcknowledged = _tmp != 0;
            _item = new SecurityEventEntity(_tmpId,_tmpCameraName,_tmpEventType,_tmpThreatLevel,_tmpConfidence,_tmpOriginTimestamp,_tmpReceivedTimestamp,_tmpHourOfDay,_tmpSummary,_tmpAcknowledged);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
