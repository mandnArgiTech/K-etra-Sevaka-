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
import com.ksetrasevakah.core.database.entity.ChatThreadEntity;
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
public final class ChatThreadDao_Impl implements ChatThreadDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatThreadEntity> __insertionAdapterOfChatThreadEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdate;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public ChatThreadDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatThreadEntity = new EntityInsertionAdapter<ChatThreadEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chat_threads` (`id`,`title`,`preview`,`created_at`,`last_message_at`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatThreadEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getPreview());
        statement.bindLong(4, entity.getCreatedAt());
        statement.bindLong(5, entity.getLastMessageAt());
      }
    };
    this.__preparedStmtOfUpdate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE chat_threads SET title = ?, preview = ?, last_message_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM chat_threads WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ChatThreadEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfChatThreadEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final long id, final String title, final String preview,
      final long lastMessageAt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdate.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, title);
        _argIndex = 2;
        _stmt.bindString(_argIndex, preview);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, lastMessageAt);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUpdate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatThreadEntity>> getAll() {
    final String _sql = "SELECT * FROM chat_threads ORDER BY last_message_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_threads"}, new Callable<List<ChatThreadEntity>>() {
      @Override
      @NonNull
      public List<ChatThreadEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPreview = CursorUtil.getColumnIndexOrThrow(_cursor, "preview");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastMessageAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_message_at");
          final List<ChatThreadEntity> _result = new ArrayList<ChatThreadEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatThreadEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpPreview;
            _tmpPreview = _cursor.getString(_cursorIndexOfPreview);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastMessageAt;
            _tmpLastMessageAt = _cursor.getLong(_cursorIndexOfLastMessageAt);
            _item = new ChatThreadEntity(_tmpId,_tmpTitle,_tmpPreview,_tmpCreatedAt,_tmpLastMessageAt);
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
  public Object getById(final long id, final Continuation<? super ChatThreadEntity> $completion) {
    final String _sql = "SELECT * FROM chat_threads WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ChatThreadEntity>() {
      @Override
      @Nullable
      public ChatThreadEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPreview = CursorUtil.getColumnIndexOrThrow(_cursor, "preview");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastMessageAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_message_at");
          final ChatThreadEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpPreview;
            _tmpPreview = _cursor.getString(_cursorIndexOfPreview);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastMessageAt;
            _tmpLastMessageAt = _cursor.getLong(_cursorIndexOfLastMessageAt);
            _result = new ChatThreadEntity(_tmpId,_tmpTitle,_tmpPreview,_tmpCreatedAt,_tmpLastMessageAt);
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
