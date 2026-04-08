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
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PredictionCacheDao_Impl implements PredictionCacheDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PredictionCacheEntity> __insertionAdapterOfPredictionCacheEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByType;

  public PredictionCacheDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPredictionCacheEntity = new EntityInsertionAdapter<PredictionCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `prediction_cache` (`type`,`result_json`,`confidence`,`computed_at`,`valid_until`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PredictionCacheEntity entity) {
        statement.bindString(1, entity.getType());
        statement.bindString(2, entity.getResultJson());
        statement.bindDouble(3, entity.getConfidence());
        statement.bindLong(4, entity.getComputedAt());
        statement.bindLong(5, entity.getValidUntil());
      }
    };
    this.__preparedStmtOfDeleteByType = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM prediction_cache WHERE type = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final PredictionCacheEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPredictionCacheEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByType(final String type, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByType.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, type);
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
          __preparedStmtOfDeleteByType.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getByType(final String type,
      final Continuation<? super PredictionCacheEntity> $completion) {
    final String _sql = "SELECT * FROM prediction_cache WHERE type = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PredictionCacheEntity>() {
      @Override
      @Nullable
      public PredictionCacheEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfResultJson = CursorUtil.getColumnIndexOrThrow(_cursor, "result_json");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfComputedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "computed_at");
          final int _cursorIndexOfValidUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "valid_until");
          final PredictionCacheEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpResultJson;
            _tmpResultJson = _cursor.getString(_cursorIndexOfResultJson);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpComputedAt;
            _tmpComputedAt = _cursor.getLong(_cursorIndexOfComputedAt);
            final long _tmpValidUntil;
            _tmpValidUntil = _cursor.getLong(_cursorIndexOfValidUntil);
            _result = new PredictionCacheEntity(_tmpType,_tmpResultJson,_tmpConfidence,_tmpComputedAt,_tmpValidUntil);
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
