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
import com.ksetrasevakah.core.database.entity.SecurityBriefingEntity;
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
public final class SecurityBriefingDao_Impl implements SecurityBriefingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SecurityBriefingEntity> __insertionAdapterOfSecurityBriefingEntity;

  public SecurityBriefingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSecurityBriefingEntity = new EntityInsertionAdapter<SecurityBriefingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `security_briefings` (`id`,`generated_at`,`period_start`,`period_end`,`summary`,`total_events`,`critical_count`,`high_count`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SecurityBriefingEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGeneratedAt());
        statement.bindLong(3, entity.getPeriodStart());
        statement.bindLong(4, entity.getPeriodEnd());
        statement.bindString(5, entity.getSummary());
        statement.bindLong(6, entity.getTotalEvents());
        statement.bindLong(7, entity.getCriticalCount());
        statement.bindLong(8, entity.getHighCount());
      }
    };
  }

  @Override
  public Object insert(final SecurityBriefingEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSecurityBriefingEntity.insertAndReturnId(entity);
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
      final Continuation<? super List<SecurityBriefingEntity>> $completion) {
    final String _sql = "SELECT * FROM security_briefings ORDER BY generated_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecurityBriefingEntity>>() {
      @Override
      @NonNull
      public List<SecurityBriefingEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGeneratedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "generated_at");
          final int _cursorIndexOfPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "period_start");
          final int _cursorIndexOfPeriodEnd = CursorUtil.getColumnIndexOrThrow(_cursor, "period_end");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfTotalEvents = CursorUtil.getColumnIndexOrThrow(_cursor, "total_events");
          final int _cursorIndexOfCriticalCount = CursorUtil.getColumnIndexOrThrow(_cursor, "critical_count");
          final int _cursorIndexOfHighCount = CursorUtil.getColumnIndexOrThrow(_cursor, "high_count");
          final List<SecurityBriefingEntity> _result = new ArrayList<SecurityBriefingEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecurityBriefingEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGeneratedAt;
            _tmpGeneratedAt = _cursor.getLong(_cursorIndexOfGeneratedAt);
            final long _tmpPeriodStart;
            _tmpPeriodStart = _cursor.getLong(_cursorIndexOfPeriodStart);
            final long _tmpPeriodEnd;
            _tmpPeriodEnd = _cursor.getLong(_cursorIndexOfPeriodEnd);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final int _tmpTotalEvents;
            _tmpTotalEvents = _cursor.getInt(_cursorIndexOfTotalEvents);
            final int _tmpCriticalCount;
            _tmpCriticalCount = _cursor.getInt(_cursorIndexOfCriticalCount);
            final int _tmpHighCount;
            _tmpHighCount = _cursor.getInt(_cursorIndexOfHighCount);
            _item = new SecurityBriefingEntity(_tmpId,_tmpGeneratedAt,_tmpPeriodStart,_tmpPeriodEnd,_tmpSummary,_tmpTotalEvents,_tmpCriticalCount,_tmpHighCount);
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
