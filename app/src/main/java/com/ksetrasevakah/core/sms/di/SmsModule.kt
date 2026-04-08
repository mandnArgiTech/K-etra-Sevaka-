package com.ksetrasevakah.core.sms.di

import android.content.Context
import android.telephony.SmsManager
import com.ksetrasevakah.core.sms.DefaultSmsCommandSender
import com.ksetrasevakah.core.sms.SmsCommandSender
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SmsModule {

    @Binds
    @Singleton
    abstract fun bindSmsCommandSender(impl: DefaultSmsCommandSender): SmsCommandSender

    companion object {
        @Provides
        @Singleton
        fun provideSmsManager(@ApplicationContext context: Context): SmsManager =
            context.getSystemService(SmsManager::class.java)
    }
}
