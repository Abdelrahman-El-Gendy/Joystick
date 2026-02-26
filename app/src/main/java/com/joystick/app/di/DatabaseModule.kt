package com.joystick.app.di

import android.content.Context
import androidx.room.Room
import com.joystick.app.data.local.JoystickDatabase
import com.joystick.app.data.local.dao.GameDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JoystickDatabase =
        Room.databaseBuilder(context, JoystickDatabase::class.java, "joystick_db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    @Singleton
    fun provideGameDao(db: JoystickDatabase): GameDao = db.gameDao()
}
