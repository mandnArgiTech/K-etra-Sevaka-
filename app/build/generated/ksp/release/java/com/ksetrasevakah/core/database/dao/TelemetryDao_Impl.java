package com.ksetrasevakah.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ksetrasevakah.core.database.entity.TelemetryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TelemetryDao_Impl implements TelemetryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TelemetryEntity> __insertionAdapterOfTelemetryEntity;

  public TelemetryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTelemetryEntity = new EntityInsertionAdapter<TelemetryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `telemetry_log` (`id`,`raw_sms`,`timestamp`,`motor_on`,`phase_r`,`phase_y`,`phase_b`,`voltage`,`temperature`,`runtime_minutes`,`narrative`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TelemetryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getRawSms());
        statement.bindLong(3, entity.getTimestamp());
        final int _tmp = entity.getMotorOn() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getPhaseR() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getPhaseR());
        }
        if (entity.getPhaseY() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getPhaseY());
        }
        if (entity.getPhaseB() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getPhaseB());
        }
        if (entity.getVoltage() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getVoltage());
        }
        if (entity.getTemperature() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getTemperature());
        }
        if (entity.getRuntimeMinutes() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getRuntimeMinutes());
        }
        if (entity.getNarrative() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getNarrative());
        }
      }
    };
  }

  @Override
  public Object insert(final TelemetryEntity entity, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTelemetryEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TelemetryEntity>> getRecent(final long since) {
    final String _sql = "SELECT * FROM telemetry_log WHERE timestamp >= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry_log"}, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRawSms = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMotorOn = CursorUtil.getColumnIndexOrThrow(_cursor, "motor_on");
          final int _cursorIndexOfPhaseR = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_r");
          final int _cursorIndexOfPhaseY = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_y");
          final int _cursorIndexOfPhaseB = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_b");
          final int _cursorIndexOfVoltage = CursorUtil.getColumnIndexOrThrow(_cursor, "voltage");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfRuntimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "runtime_minutes");
          final int _cursorIndexOfNarrative = CursorUtil.getColumnIndexOrThrow(_cursor, "narrative");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRawSms;
            _tmpRawSms = _cursor.getString(_cursorIndexOfRawSms);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpMotorOn;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfMotorOn);
            _tmpMotorOn = _tmp != 0;
            final Float _tmpPhaseR;
            if (_cursor.isNull(_cursorIndexOfPhaseR)) {
              _tmpPhaseR = null;
            } else {
              _tmpPhaseR = _cursor.getFloat(_cursorIndexOfPhaseR);
            }
            final Float _tmpPhaseY;
            if (_cursor.isNull(_cursorIndexOfPhaseY)) {
              _tmpPhaseY = null;
            } else {
              _tmpPhaseY = _cursor.getFloat(_cursorIndexOfPhaseY);
            }
            final Float _tmpPhaseB;
            if (_cursor.isNull(_cursorIndexOfPhaseB)) {
              _tmpPhaseB = null;
            } else {
              _tmpPhaseB = _cursor.getFloat(_cursorIndexOfPhaseB);
            }
            final Float _tmpVoltage;
            if (_cursor.isNull(_cursorIndexOfVoltage)) {
              _tmpVoltage = null;
            } else {
              _tmpVoltage = _cursor.getFloat(_cursorIndexOfVoltage);
            }
            final Float _tmpTemperature;
            if (_cursor.isNull(_cursorIndexOfTemperature)) {
              _tmpTemperature = null;
            } else {
              _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            }
            final Integer _tmpRuntimeMinutes;
            if (_cursor.isNull(_cursorIndexOfRuntimeMinutes)) {
              _tmpRuntimeMinutes = null;
            } else {
              _tmpRuntimeMinutes = _cursor.getInt(_cursorIndexOfRuntimeMinutes);
            }
            final String _tmpNarrative;
            if (_cursor.isNull(_cursorIndexOfNarrative)) {
              _tmpNarrative = null;
            } else {
              _tmpNarrative = _cursor.getString(_cursorIndexOfNarrative);
            }
            _item = new TelemetryEntity(_tmpId,_tmpRawSms,_tmpTimestamp,_tmpMotorOn,_tmpPhaseR,_tmpPhaseY,_tmpPhaseB,_tmpVoltage,_tmpTemperature,_tmpRuntimeMinutes,_tmpNarrative);
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
  public Object getRecentList(final long since,
      final Continuation<? super List<TelemetryEntity>> $completion) {
    final String _sql = "SELECT * FROM telemetry_log WHERE timestamp >= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRawSms = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMotorOn = CursorUtil.getColumnIndexOrThrow(_cursor, "motor_on");
          final int _cursorIndexOfPhaseR = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_r");
          final int _cursorIndexOfPhaseY = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_y");
          final int _cursorIndexOfPhaseB = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_b");
          final int _cursorIndexOfVoltage = CursorUtil.getColumnIndexOrThrow(_cursor, "voltage");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfRuntimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "runtime_minutes");
          final int _cursorIndexOfNarrative = CursorUtil.getColumnIndexOrThrow(_cursor, "narrative");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRawSms;
            _tmpRawSms = _cursor.getString(_cursorIndexOfRawSms);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpMotorOn;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfMotorOn);
            _tmpMotorOn = _tmp != 0;
            final Float _tmpPhaseR;
            if (_cursor.isNull(_cursorIndexOfPhaseR)) {
              _tmpPhaseR = null;
            } else {
              _tmpPhaseR = _cursor.getFloat(_cursorIndexOfPhaseR);
            }
            final Float _tmpPhaseY;
            if (_cursor.isNull(_cursorIndexOfPhaseY)) {
              _tmpPhaseY = null;
            } else {
              _tmpPhaseY = _cursor.getFloat(_cursorIndexOfPhaseY);
            }
            final Float _tmpPhaseB;
            if (_cursor.isNull(_cursorIndexOfPhaseB)) {
              _tmpPhaseB = null;
            } else {
              _tmpPhaseB = _cursor.getFloat(_cursorIndexOfPhaseB);
            }
            final Float _tmpVoltage;
            if (_cursor.isNull(_cursorIndexOfVoltage)) {
              _tmpVoltage = null;
            } else {
              _tmpVoltage = _cursor.getFloat(_cursorIndexOfVoltage);
            }
            final Float _tmpTemperature;
            if (_cursor.isNull(_cursorIndexOfTemperature)) {
              _tmpTemperature = null;
            } else {
              _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            }
            final Integer _tmpRuntimeMinutes;
            if (_cursor.isNull(_cursorIndexOfRuntimeMinutes)) {
              _tmpRuntimeMinutes = null;
            } else {
              _tmpRuntimeMinutes = _cursor.getInt(_cursorIndexOfRuntimeMinutes);
            }
            final String _tmpNarrative;
            if (_cursor.isNull(_cursorIndexOfNarrative)) {
              _tmpNarrative = null;
            } else {
              _tmpNarrative = _cursor.getString(_cursorIndexOfNarrative);
            }
            _item = new TelemetryEntity(_tmpId,_tmpRawSms,_tmpTimestamp,_tmpMotorOn,_tmpPhaseR,_tmpPhaseY,_tmpPhaseB,_tmpVoltage,_tmpTemperature,_tmpRuntimeMinutes,_tmpNarrative);
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
  public Object getAvgVoltage(final long since, final long until,
      final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(voltage) FROM telemetry_log WHERE timestamp >= ? AND timestamp < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    _argIndex = 2;
    _statement.bindLong(_argIndex, until);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
