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
import com.ksetrasevakah.core.database.entity.CameraConfigEntity;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CameraConfigDao_Impl implements CameraConfigDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CameraConfigEntity> __insertionAdapterOfCameraConfigEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMode;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastSeen;

  public CameraConfigDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCameraConfigEntity = new EntityInsertionAdapter<CameraConfigEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `camera_config` (`id`,`camera_name`,`mode`,`last_seen`,`created_at`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CameraConfigEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCameraName());
        statement.bindString(3, entity.getMode());
        statement.bindLong(4, entity.getLastSeen());
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfUpdateMode = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE camera_config SET mode = ? WHERE camera_name = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastSeen = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE camera_config SET last_seen = ? WHERE camera_name = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final CameraConfigEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCameraConfigEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMode(final String cameraName, final String mode,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMode.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, mode);
        _argIndex = 2;
        _stmt.bindString(_argIndex, cameraName);
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
          __preparedStmtOfUpdateMode.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastSeen(final String cameraName, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastSeen.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, cameraName);
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
          __preparedStmtOfUpdateLastSeen.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CameraConfigEntity>> getAllCameras() {
    final String _sql = "SELECT * FROM camera_config ORDER BY camera_name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"camera_config"}, new Callable<List<CameraConfigEntity>>() {
      @Override
      @NonNull
      public List<CameraConfigEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCameraName = CursorUtil.getColumnIndexOrThrow(_cursor, "camera_name");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<CameraConfigEntity> _result = new ArrayList<CameraConfigEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CameraConfigEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCameraName;
            _tmpCameraName = _cursor.getString(_cursorIndexOfCameraName);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new CameraConfigEntity(_tmpId,_tmpCameraName,_tmpMode,_tmpLastSeen,_tmpCreatedAt);
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
  public Object getByName(final String name,
      final Continuation<? super CameraConfigEntity> $completion) {
    final String _sql = "SELECT * FROM camera_config WHERE camera_name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CameraConfigEntity>() {
      @Override
      @Nullable
      public CameraConfigEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCameraName = CursorUtil.getColumnIndexOrThrow(_cursor, "camera_name");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final CameraConfigEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCameraName;
            _tmpCameraName = _cursor.getString(_cursorIndexOfCameraName);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new CameraConfigEntity(_tmpId,_tmpCameraName,_tmpMode,_tmpLastSeen,_tmpCreatedAt);
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
