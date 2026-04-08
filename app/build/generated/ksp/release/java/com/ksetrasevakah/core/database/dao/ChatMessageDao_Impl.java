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
import com.ksetrasevakah.core.database.entity.ChatMessageEntity;
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
public final class ChatMessageDao_Impl implements ChatMessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatMessageEntity> __insertionAdapterOfChatMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForThread;

  public ChatMessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatMessageEntity = new EntityInsertionAdapter<ChatMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chat_messages` (`id`,`thread_id`,`role`,`text`,`created_at`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatMessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getThreadId());
        statement.bindString(3, entity.getRole());
        statement.bindString(4, entity.getText());
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteForThread = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM chat_messages WHERE thread_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ChatMessageEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfChatMessageEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForThread(final long threadId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForThread.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, threadId);
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
          __preparedStmtOfDeleteForThread.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatMessageEntity>> getMessagesForThread(final long threadId) {
    final String _sql = "SELECT * FROM chat_messages WHERE thread_id = ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, threadId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_messages"}, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "thread_id");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChatMessageEntity(_tmpId,_tmpThreadId,_tmpRole,_tmpText,_tmpCreatedAt);
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
  public Object getMessagesPaginated(final long threadId, final int limit, final int offset,
      final Continuation<? super List<ChatMessageEntity>> $completion) {
    final String _sql = "SELECT * FROM chat_messages WHERE thread_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, threadId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 3;
    _statement.bindLong(_argIndex, offset);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "thread_id");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChatMessageEntity(_tmpId,_tmpThreadId,_tmpRole,_tmpText,_tmpCreatedAt);
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
  public Object getRecentMessages(final long threadId, final int limit,
      final Continuation<? super List<ChatMessageEntity>> $completion) {
    final String _sql = "SELECT * FROM chat_messages WHERE thread_id = ? ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, threadId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "thread_id");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChatMessageEntity(_tmpId,_tmpThreadId,_tmpRole,_tmpText,_tmpCreatedAt);
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
