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
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity;
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
public final class WorkerActivityDao_Impl implements WorkerActivityDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkerActivityEntity> __insertionAdapterOfWorkerActivityEntity;

  public WorkerActivityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkerActivityEntity = new EntityInsertionAdapter<WorkerActivityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `worker_activity` (`id`,`date`,`on_time`,`off_time`,`duration_minutes`,`forgot_off`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkerActivityEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        if (entity.getOnTime() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getOnTime());
        }
        if (entity.getOffTime() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getOffTime());
        }
        if (entity.getDurationMinutes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDurationMinutes());
        }
        final int _tmp = entity.getForgotOff() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
  }

  @Override
  public Object upsert(final WorkerActivityEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkerActivityEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WorkerActivityEntity>> getLast14Days() {
    final String _sql = "SELECT * FROM worker_activity ORDER BY date DESC LIMIT 14";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"worker_activity"}, new Callable<List<WorkerActivityEntity>>() {
      @Override
      @NonNull
      public List<WorkerActivityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfOnTime = CursorUtil.getColumnIndexOrThrow(_cursor, "on_time");
          final int _cursorIndexOfOffTime = CursorUtil.getColumnIndexOrThrow(_cursor, "off_time");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_minutes");
          final int _cursorIndexOfForgotOff = CursorUtil.getColumnIndexOrThrow(_cursor, "forgot_off");
          final List<WorkerActivityEntity> _result = new ArrayList<WorkerActivityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkerActivityEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final Long _tmpOnTime;
            if (_cursor.isNull(_cursorIndexOfOnTime)) {
              _tmpOnTime = null;
            } else {
              _tmpOnTime = _cursor.getLong(_cursorIndexOfOnTime);
            }
            final Long _tmpOffTime;
            if (_cursor.isNull(_cursorIndexOfOffTime)) {
              _tmpOffTime = null;
            } else {
              _tmpOffTime = _cursor.getLong(_cursorIndexOfOffTime);
            }
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final boolean _tmpForgotOff;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfForgotOff);
            _tmpForgotOff = _tmp != 0;
            _item = new WorkerActivityEntity(_tmpId,_tmpDate,_tmpOnTime,_tmpOffTime,_tmpDurationMinutes,_tmpForgotOff);
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
  public Object getLastNDays(final int days,
      final Continuation<? super List<WorkerActivityEntity>> $completion) {
    final String _sql = "SELECT * FROM worker_activity ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WorkerActivityEntity>>() {
      @Override
      @NonNull
      public List<WorkerActivityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfOnTime = CursorUtil.getColumnIndexOrThrow(_cursor, "on_time");
          final int _cursorIndexOfOffTime = CursorUtil.getColumnIndexOrThrow(_cursor, "off_time");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_minutes");
          final int _cursorIndexOfForgotOff = CursorUtil.getColumnIndexOrThrow(_cursor, "forgot_off");
          final List<WorkerActivityEntity> _result = new ArrayList<WorkerActivityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkerActivityEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final Long _tmpOnTime;
            if (_cursor.isNull(_cursorIndexOfOnTime)) {
              _tmpOnTime = null;
            } else {
              _tmpOnTime = _cursor.getLong(_cursorIndexOfOnTime);
            }
            final Long _tmpOffTime;
            if (_cursor.isNull(_cursorIndexOfOffTime)) {
              _tmpOffTime = null;
            } else {
              _tmpOffTime = _cursor.getLong(_cursorIndexOfOffTime);
            }
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final boolean _tmpForgotOff;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfForgotOff);
            _tmpForgotOff = _tmp != 0;
            _item = new WorkerActivityEntity(_tmpId,_tmpDate,_tmpOnTime,_tmpOffTime,_tmpDurationMinutes,_tmpForgotOff);
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
  public Object getAvgOnTime(final int days, final Continuation<? super Long> $completion) {
    final String _sql = "SELECT AVG(on_time) FROM worker_activity WHERE on_time IS NOT NULL ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
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

  @Override
  public Object getForgotOffCount(final String since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM worker_activity WHERE forgot_off = 1 AND date >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, since);
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
  public Object getByDate(final String date,
      final Continuation<? super WorkerActivityEntity> $completion) {
    final String _sql = "SELECT * FROM worker_activity WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkerActivityEntity>() {
      @Override
      @Nullable
      public WorkerActivityEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfOnTime = CursorUtil.getColumnIndexOrThrow(_cursor, "on_time");
          final int _cursorIndexOfOffTime = CursorUtil.getColumnIndexOrThrow(_cursor, "off_time");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_minutes");
          final int _cursorIndexOfForgotOff = CursorUtil.getColumnIndexOrThrow(_cursor, "forgot_off");
          final WorkerActivityEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final Long _tmpOnTime;
            if (_cursor.isNull(_cursorIndexOfOnTime)) {
              _tmpOnTime = null;
            } else {
              _tmpOnTime = _cursor.getLong(_cursorIndexOfOnTime);
            }
            final Long _tmpOffTime;
            if (_cursor.isNull(_cursorIndexOfOffTime)) {
              _tmpOffTime = null;
            } else {
              _tmpOffTime = _cursor.getLong(_cursorIndexOfOffTime);
            }
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final boolean _tmpForgotOff;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfForgotOff);
            _tmpForgotOff = _tmp != 0;
            _result = new WorkerActivityEntity(_tmpId,_tmpDate,_tmpOnTime,_tmpOffTime,_tmpDurationMinutes,_tmpForgotOff);
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
