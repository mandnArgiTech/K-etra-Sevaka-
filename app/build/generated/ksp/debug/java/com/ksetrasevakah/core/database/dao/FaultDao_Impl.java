package com.ksetrasevakah.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ksetrasevakah.core.database.entity.FaultEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class FaultDao_Impl implements FaultDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FaultEntity> __insertionAdapterOfFaultEntity;

  public FaultDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFaultEntity = new EntityInsertionAdapter<FaultEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `fault_log` (`id`,`timestamp`,`fault_type`,`description`,`auto_recovered`,`recovery_time`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FaultEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindString(3, entity.getFaultType());
        statement.bindString(4, entity.getDescription());
        final int _tmp = entity.getAutoRecovered() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getRecoveryTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getRecoveryTime());
        }
      }
    };
  }

  @Override
  public Object insert(final FaultEntity entity, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFaultEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getFaultDistribution(final long since,
      final Continuation<? super List<FaultCount>> $completion) {
    final String _sql = "SELECT fault_type, COUNT(*) as count FROM fault_log WHERE timestamp >= ? GROUP BY fault_type";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FaultCount>>() {
      @Override
      @NonNull
      public List<FaultCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFaultType = 0;
          final int _cursorIndexOfCount = 1;
          final List<FaultCount> _result = new ArrayList<FaultCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FaultCount _item;
            final String _tmpFaultType;
            _tmpFaultType = _cursor.getString(_cursorIndexOfFaultType);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new FaultCount(_tmpFaultType,_tmpCount);
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
  public Flow<List<FaultEntity>> getRecent(final long since) {
    final String _sql = "SELECT * FROM fault_log WHERE timestamp >= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fault_log"}, new Callable<List<FaultEntity>>() {
      @Override
      @NonNull
      public List<FaultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfFaultType = CursorUtil.getColumnIndexOrThrow(_cursor, "fault_type");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAutoRecovered = CursorUtil.getColumnIndexOrThrow(_cursor, "auto_recovered");
          final int _cursorIndexOfRecoveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "recovery_time");
          final List<FaultEntity> _result = new ArrayList<FaultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FaultEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpFaultType;
            _tmpFaultType = _cursor.getString(_cursorIndexOfFaultType);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpAutoRecovered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoRecovered);
            _tmpAutoRecovered = _tmp != 0;
            final Long _tmpRecoveryTime;
            if (_cursor.isNull(_cursorIndexOfRecoveryTime)) {
              _tmpRecoveryTime = null;
            } else {
              _tmpRecoveryTime = _cursor.getLong(_cursorIndexOfRecoveryTime);
            }
            _item = new FaultEntity(_tmpId,_tmpTimestamp,_tmpFaultType,_tmpDescription,_tmpAutoRecovered,_tmpRecoveryTime);
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
  public Object getRecentList(final int limit,
      final Continuation<? super List<FaultEntity>> $completion) {
    final String _sql = "SELECT * FROM fault_log ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FaultEntity>>() {
      @Override
      @NonNull
      public List<FaultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfFaultType = CursorUtil.getColumnIndexOrThrow(_cursor, "fault_type");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAutoRecovered = CursorUtil.getColumnIndexOrThrow(_cursor, "auto_recovered");
          final int _cursorIndexOfRecoveryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "recovery_time");
          final List<FaultEntity> _result = new ArrayList<FaultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FaultEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpFaultType;
            _tmpFaultType = _cursor.getString(_cursorIndexOfFaultType);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpAutoRecovered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoRecovered);
            _tmpAutoRecovered = _tmp != 0;
            final Long _tmpRecoveryTime;
            if (_cursor.isNull(_cursorIndexOfRecoveryTime)) {
              _tmpRecoveryTime = null;
            } else {
              _tmpRecoveryTime = _cursor.getLong(_cursorIndexOfRecoveryTime);
            }
            _item = new FaultEntity(_tmpId,_tmpTimestamp,_tmpFaultType,_tmpDescription,_tmpAutoRecovered,_tmpRecoveryTime);
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
