package com.ksetrasevakah.core.database.di;

import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.ChatThreadDao;
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
public final class DatabaseModule_ProvideChatThreadDaoFactory implements Factory<ChatThreadDao> {
  private final Provider<KsetraDatabase> dbProvider;

  public DatabaseModule_ProvideChatThreadDaoFactory(Provider<KsetraDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ChatThreadDao get() {
    return provideChatThreadDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideChatThreadDaoFactory create(
      Provider<KsetraDatabase> dbProvider) {
    return new DatabaseModule_ProvideChatThreadDaoFactory(dbProvider);
  }

  public static ChatThreadDao provideChatThreadDao(KsetraDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideChatThreadDao(db));
  }
}
