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
import com.ksetrasevakah.core.database.entity.BackupLogEntity;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BackupLogDao_Impl implements BackupLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BackupLogEntity> __insertionAdapterOfBackupLogEntity;

  public BackupLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBackupLogEntity = new EntityInsertionAdapter<BackupLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `backup_log` (`id`,`timestamp`,`type`,`status`,`file_size_bytes`,`error_message`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BackupLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getStatus());
        if (entity.getFileSizeBytes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFileSizeBytes());
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getErrorMessage());
        }
      }
    };
  }

  @Override
  public Object insert(final BackupLogEntity entity, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBackupLogEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecent(final int limit,
      final Continuation<? super List<BackupLogEntity>> $completion) {
    final String _sql = "SELECT * FROM backup_log ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BackupLogEntity>>() {
      @Override
      @NonNull
      public List<BackupLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "error_message");
          final List<BackupLogEntity> _result = new ArrayList<BackupLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BackupLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpFileSizeBytes;
            if (_cursor.isNull(_cursorIndexOfFileSizeBytes)) {
              _tmpFileSizeBytes = null;
            } else {
              _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new BackupLogEntity(_tmpId,_tmpTimestamp,_tmpType,_tmpStatus,_tmpFileSizeBytes,_tmpErrorMessage);
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
