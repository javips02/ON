package com.javips02.on.persistence.user;

import androidx.room.Entity
import androidx.room.PrimaryKey

// Define the User entity (table)
@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val username: String,
        val email: String,
        val password: String // WARNING: NEVER store passwords in plain text! Use hashing (e.g., BCrypt, Argon2) in production.
)