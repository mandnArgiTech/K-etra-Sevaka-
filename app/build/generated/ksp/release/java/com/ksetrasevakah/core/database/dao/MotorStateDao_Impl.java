package com.ksetrasevakah.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ksetrasevakah.core.database.entity.MotorStateEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MotorStateDao_Impl implements MotorStateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MotorStateEntity> __insertionAdapterOfMotorStateEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateState;

  private final SharedSQLiteStatement __preparedStmtOfSetPendingCommand;

  private final SharedSQLiteStatement __preparedStmtOfSetSessionStart;

  private final SharedSQLiteStatement __preparedStmtOfSetSessionEnd;

  public MotorStateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMotorStateEntity = new EntityInsertionAdapter<MotorStateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `motor_state` (`id`,`state`,`last_on_time`,`last_off_time`,`current_session_start`,`pending_command`,`pending_since`,`updated_at`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MotorStateEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getState());
        if (entity.getLastOnTime() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getLastOnTime());
        }
        if (entity.getLastOffTime() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getLastOffTime());
        }
        if (entity.getCurrentSessionStart() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCurrentSessionStart());
        }
        if (entity.getPendingCommand() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPendingCommand());
        }
        if (entity.getPendingSince() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getPendingSince());
        }
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfUpdateState = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE motor_state SET state = ?, updated_at = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfSetPendingCommand = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE motor_state SET pending_command = ?, pending_since = ?, updated_at = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfSetSessionStart = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE motor_state SET current_session_start = ?, last_on_time = ?, updated_at = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfSetSessionEnd = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE motor_state SET current_session_start = null, last_off_time = ?, updated_at = ? WHERE id = 1";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final MotorStateEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMotorStateEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateState(final String state, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateState.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, state);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
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
          __preparedStmtOfUpdateState.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setPendingCommand(final String command, final Long since, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetPendingCommand.acquire();
        int _argIndex = 1;
        if (command == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, command);
        }
        _argIndex = 2;
        if (since == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, since);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, now);
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
          __preparedStmtOfSetPendingCommand.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setSessionStart(final long start, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetSessionStart.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, start);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, start);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, now);
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
          __preparedStmtOfSetSessionStart.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setSessionEnd(final long offTime, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetSessionEnd.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, offTime);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
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
          __preparedStmtOfSetSessionEnd.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<MotorStateEntity> observe() {
    final String _sql = "SELECT * FROM motor_state WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"motor_state"}, new Callable<MotorStateEntity>() {
      @Override
      @Nullable
      public MotorStateEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfLastOnTime = CursorUtil.getColumnIndexOrThrow(_cursor, "last_on_time");
          final int _cursorIndexOfLastOffTime = CursorUtil.getColumnIndexOrThrow(_cursor, "last_off_time");
          final int _cursorIndexOfCurrentSessionStart = CursorUtil.getColumnIndexOrThrow(_cursor, "current_session_start");
          final int _cursorIndexOfPendingCommand = CursorUtil.getColumnIndexOrThrow(_cursor, "pending_command");
          final int _cursorIndexOfPendingSince = CursorUtil.getColumnIndexOrThrow(_cursor, "pending_since");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final MotorStateEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpState;
            _tmpState = _cursor.getString(_cursorIndexOfState);
            final Long _tmpLastOnTime;
            if (_cursor.isNull(_cursorIndexOfLastOnTime)) {
              _tmpLastOnTime = null;
            } else {
              _tmpLastOnTime = _cursor.getLong(_cursorIndexOfLastOnTime);
            }
            final Long _tmpLastOffTime;
            if (_cursor.isNull(_cursorIndexOfLastOffTime)) {
              _tmpLastOffTime = null;
            } else {
              _tmpLastOffTime = _cursor.getLong(_cursorIndexOfLastOffTime);
            }
            final Long _tmpCurrentSessionStart;
            if (_cursor.isNull(_cursorIndexOfCurrentSessionStart)) {
              _tmpCurrentSessionStart = null;
            } else {
              _tmpCurrentSessionStart = _cursor.getLong(_cursorIndexOfCurrentSessionStart);
            }
            final String _tmpPendingCommand;
            if (_cursor.isNull(_cursorIndexOfPendingCommand)) {
              _tmpPendingCommand = null;
            } else {
              _tmpPendingCommand = _cursor.getString(_cursorIndexOfPendingCommand);
            }
            final Long _tmpPendingSince;
            if (_cursor.isNull(_cursorIndexOfPendingSince)) {
              _tmpPendingSince = null;
            } else {
              _tmpPendingSince = _cursor.getLong(_cursorIndexOfPendingSince);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MotorStateEntity(_tmpId,_tmpState,_tmpLastOnTime,_tmpLastOffTime,_tmpCurrentSessionStart,_tmpPendingCommand,_tmpPendingSince,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Object get(final Continuation<? super MotorStateEntity> $completion) {
    final String _sql = "SELECT * FROM motor_state WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MotorStateEntity>() {
      @Override
      @Nullable
      public MotorStateEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfLastOnTime = CursorUtil.getColumnIndexOrThrow(_cursor, "last_on_time");
          final int _cursorIndexOfLastOffTime = CursorUtil.getColumnIndexOrThrow(_cursor, "last_off_time");
          final int _cursorIndexOfCurrentSessionStart = CursorUtil.getColumnIndexOrThrow(_cursor, "current_session_start");
          final int _cursorIndexOfPendingCommand = CursorUtil.getColumnIndexOrThrow(_cursor, "pending_command");
          final int _cursorIndexOfPendingSince = CursorUtil.getColumnIndexOrThrow(_cursor, "pending_since");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final MotorStateEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpState;
            _tmpState = _cursor.getString(_cursorIndexOfState);
            final Long _tmpLastOnTime;
            if (_cursor.isNull(_cursorIndexOfLastOnTime)) {
              _tmpLastOnTime = null;
            } else {
              _tmpLastOnTime = _cursor.getLong(_cursorIndexOfLastOnTime);
            }
            final Long _tmpLastOffTime;
            if (_cursor.isNull(_cursorIndexOfLastOffTime)) {
              _tmpLastOffTime = null;
            } else {
              _tmpLastOffTime = _cursor.getLong(_cursorIndexOfLastOffTime);
            }
            final Long _tmpCurrentSessionStart;
            if (_cursor.isNull(_cursorIndexOfCurrentSessionStart)) {
              _tmpCurrentSessionStart = null;
            } else {
              _tmpCurrentSessionStart = _cursor.getLong(_cursorIndexOfCurrentSessionStart);
            }
            final String _tmpPendingCommand;
            if (_cursor.isNull(_cursorIndexOfPendingCommand)) {
              _tmpPendingCommand = null;
            } else {
              _tmpPendingCommand = _cursor.getString(_cursorIndexOfPendingCommand);
            }
            final Long _tmpPendingSince;
            if (_cursor.isNull(_cursorIndexOfPendingSince)) {
              _tmpPendingSince = null;
            } else {
              _tmpPendingSince = _cursor.getLong(_cursorIndexOfPendingSince);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MotorStateEntity(_tmpId,_tmpState,_tmpLastOnTime,_tmpLastOffTime,_tmpCurrentSessionStart,_tmpPendingCommand,_tmpPendingSince,_tmpUpdatedAt);
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
