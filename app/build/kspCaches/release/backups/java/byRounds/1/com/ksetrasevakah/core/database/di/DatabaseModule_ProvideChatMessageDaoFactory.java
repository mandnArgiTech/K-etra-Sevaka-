package com.ksetrasevakah.core.database.di;

import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.ChatMessageDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideChatMessageDaoFactory implements Factory<ChatMessageDao> {
  private final Provider<KsetraDatabase> dbProvider;

  public DatabaseModule_ProvideChatMessageDaoFactory(Provider<KsetraDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ChatMessageDao get() {
    return provideChatMessageDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideChatMessageDaoFactory create(
      Provider<KsetraDatabase> dbProvider) {
    return new DatabaseModule_ProvideChatMessageDaoFactory(dbProvider);
  }

  public static ChatMessageDao provideChatMessageDao(KsetraDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideChatMessageDao(db));
  }
}
