package com.joystick.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joystick.app.data.local.dao.GameDao
import com.joystick.app.data.local.entity.GameEntity

@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
abstract class JoystickDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
