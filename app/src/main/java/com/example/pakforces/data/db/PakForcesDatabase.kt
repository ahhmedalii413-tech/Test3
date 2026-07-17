package com.example.pakforces.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        QuestionEntity::class,
        TestResultEntity::class,
        BookmarkEntity::class,
        WrongAnswerEntity::class,
        DailyChallengeEntity::class,
        UserStatsEntity::class,
        XpHistoryEntity::class,
        NoteEntity::class,
        BookmarkFolderEntity::class,
        BookmarkFolderLinkEntity::class,
        MissionEntity::class,
        FlashcardEntity::class,
        ExamDateEntity::class,
        PomodoroSessionEntity::class,
        SrsScheduleEntity::class,
        QuestionTimingEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class PakForcesDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun testResultDao(): TestResultDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun wrongAnswerDao(): WrongAnswerDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun xpHistoryDao(): XpHistoryDao
    abstract fun noteDao(): NoteDao
    abstract fun bookmarkFolderDao(): BookmarkFolderDao
    abstract fun missionDao(): MissionDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun examDateDao(): ExamDateDao
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun srsDao(): SrsDao
    abstract fun questionTimingDao(): QuestionTimingDao

    companion object {
        @Volatile private var INSTANCE: PakForcesDatabase? = null

        fun get(context: Context): PakForcesDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                PakForcesDatabase::class.java,
                "pak_forces.db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build().also { INSTANCE = it }
        }
    }
}
