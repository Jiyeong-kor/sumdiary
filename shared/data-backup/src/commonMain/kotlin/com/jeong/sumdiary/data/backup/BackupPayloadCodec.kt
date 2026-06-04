package com.jeong.sumdiary.data.backup

import com.jeong.sumdiary.domain.backup.BackupSnapshot
import com.jeong.sumdiary.domain.backup.BackupSnapshotSettings
import com.jeong.sumdiary.domain.diary.DiaryEntry
import com.jeong.sumdiary.domain.summary.Summary
import com.jeong.sumdiary.domain.summary.SummaryType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class BackupPayloadCodec {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = false
    }

    fun encode(snapshot: BackupSnapshot): ByteArray =
        json.encodeToString(snapshot.toPayload()).encodeToByteArray()

    fun decode(bytes: ByteArray): BackupSnapshot =
        json.decodeFromString<BackupPayload>(bytes.decodeToString()).toSnapshot()

    private fun BackupSnapshot.toPayload(): BackupPayload = BackupPayload(
        version = version,
        createdAtEpochMillis = createdAtEpochMillis,
        entries = entries.map { entry ->
            DiaryEntryPayload(
                id = entry.id,
                date = entry.date.toString(),
                time = entry.time.toString(),
                content = entry.content
            )
        },
        summaries = summaries.map { summary ->
            SummaryPayload(
                type = summary.type.name,
                periodStart = summary.periodStart.toString(),
                periodEnd = summary.periodEnd.toString(),
                text = summary.text,
                emotions = summary.emotions
            )
        },
        settings = BackupSettingsPayload(
            aiSummaryEnabled = settings.aiSummaryEnabled,
            backupEnabled = settings.backupEnabled
        )
    )

    private fun BackupPayload.toSnapshot(): BackupSnapshot = BackupSnapshot(
        version = version,
        createdAtEpochMillis = createdAtEpochMillis,
        entries = entries.map { entry ->
            DiaryEntry(
                id = entry.id,
                date = LocalDate.parse(entry.date),
                time = LocalTime.parse(entry.time),
                content = entry.content
            )
        },
        summaries = summaries.map { summary ->
            Summary(
                type = SummaryType.valueOf(summary.type),
                periodStart = LocalDate.parse(summary.periodStart),
                periodEnd = LocalDate.parse(summary.periodEnd),
                text = summary.text,
                emotions = summary.emotions
            )
        },
        settings = BackupSnapshotSettings(
            aiSummaryEnabled = settings.aiSummaryEnabled,
            backupEnabled = settings.backupEnabled
        )
    )
}

@Serializable
private data class BackupPayload(
    val version: Int,
    val createdAtEpochMillis: Long,
    val entries: List<DiaryEntryPayload>,
    val summaries: List<SummaryPayload>,
    val settings: BackupSettingsPayload
)

@Serializable
private data class DiaryEntryPayload(
    val id: String,
    val date: String,
    val time: String,
    val content: String
)

@Serializable
private data class SummaryPayload(
    val type: String,
    val periodStart: String,
    val periodEnd: String,
    val text: String,
    val emotions: List<String>
)

@Serializable
private data class BackupSettingsPayload(
    val aiSummaryEnabled: Boolean,
    val backupEnabled: Boolean
)
