
package com.sohailcreations.happyequals.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// ==========================================
// HISTORY DATA MODEL
// ==========================================

data class HistoryEntry(
    val id: String,
    val expression: String,
    val result: String,
    val source: String,
    val timestamp: Long
)

// ==========================================
// LOCAL HISTORY REPOSITORY
// ==========================================

object HistoryRepository {

    private const val PREF_NAME = "happy_equals_history"
    private const val HISTORY_KEY = "saved_calculations"
    private const val MAX_ENTRIES = 100

    // ======================================
    // READ HISTORY
    // ======================================

    fun load(context: Context): List<HistoryEntry> {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val savedData = preferences.getString(
            HISTORY_KEY,
            "[]"
        ) ?: "[]"

        return runCatching {

            val array = JSONArray(savedData)

            val history = mutableListOf<HistoryEntry>()

            for (index in 0 until array.length()) {

                val item = array.optJSONObject(index)
                    ?: continue

                history.add(
                    HistoryEntry(
                        id = item.optString("id"),
                        expression = item.optString("expression"),
                        result = item.optString("result"),
                        source = item.optString(
                            "source",
                            "Calculator"
                        ),
                        timestamp = item.optLong(
                            "timestamp",
                            0L
                        )
                    )
                )
            }

            history

        }.getOrDefault(emptyList())
    }

    // ======================================
    // SAVE CALCULATION
    // ======================================

    fun add(
        context: Context,
        expression: String,
        result: String,
        source: String = "Calculator"
    ) {

        if (
            expression.isBlank() ||
            result.isBlank()
        ) {
            return
        }

        val currentHistory = load(context)
            .toMutableList()

        val newEntry = HistoryEntry(
            id = UUID.randomUUID().toString(),
            expression = expression,
            result = result,
            source = source,
            timestamp = System.currentTimeMillis()
        )

        currentHistory.add(0, newEntry)

        saveAll(
            context = context,
            entries = currentHistory.take(MAX_ENTRIES)
        )
    }

    // ======================================
    // DELETE SINGLE CALCULATION
    // ======================================

    fun delete(
        context: Context,
        id: String
    ) {

        val updated = load(context).filter {
            it.id != id
        }

        saveAll(context, updated)
    }

    // ======================================
    // CLEAR ALL HISTORY
    // ======================================

    fun clear(context: Context) {

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .remove(HISTORY_KEY)
            .apply()
    }

    // ======================================
    // WRITE HISTORY
    // ======================================

    private fun saveAll(
        context: Context,
        entries: List<HistoryEntry>
    ) {

        val array = JSONArray()

        entries.forEach { entry ->

            val item = JSONObject().apply {

                put("id", entry.id)
                put("expression", entry.expression)
                put("result", entry.result)
                put("source", entry.source)
                put("timestamp", entry.timestamp)
            }

            array.put(item)
        }

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                HISTORY_KEY,
                array.toString()
            )
            .apply()
    }
}