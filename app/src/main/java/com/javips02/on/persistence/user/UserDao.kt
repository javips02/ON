package com.javips02.on.persistence.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Define the Data Access Object (DAO)
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getById(id: Int): Flow<User>

    @Query("SELECT * FROM users WHERE username = :username")
    fun getByUsername(username: String): Flow<User>

    @Insert
    suspend fun insert(user: User): Long // Changed return type to Long

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun countByUsername(username: String): Int

    // Added function to check for existing username during registration
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun isUsernameTaken(username: String): Boolean
}
