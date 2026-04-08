package com.ksetrasevakah.core.data.repository;

import com.ksetrasevakah.core.database.dao.ChatMessageDao;
import com.ksetrasevakah.core.database.dao.ChatThreadDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ChatRepositoryImpl_Factory implements Factory<ChatRepositoryImpl> {
  private final Provider<ChatThreadDao> threadDaoProvider;

  private final Provider<ChatMessageDao> messageDaoProvider;

  public ChatRepositoryImpl_Factory(Provider<ChatThreadDao> threadDaoProvider,
      Provider<ChatMessageDao> messageDaoProvider) {
    this.threadDaoProvider = threadDaoProvider;
    this.messageDaoProvider = messageDaoProvider;
  }

  @Override
  public ChatRepositoryImpl get() {
    return newInstance(threadDaoProvider.get(), messageDaoProvider.get());
  }

  public static ChatRepositoryImpl_Factory create(Provider<ChatThreadDao> threadDaoProvider,
      Provider<ChatMessageDao> messageDaoProvider) {
    return new ChatRepositoryImpl_Factory(threadDaoProvider, messageDaoProvider);
  }

  public static ChatRepositoryImpl newInstance(ChatThreadDao threadDao, ChatMessageDao messageDao) {
    return new ChatRepositoryImpl(threadDao, messageDao);
  }
}
