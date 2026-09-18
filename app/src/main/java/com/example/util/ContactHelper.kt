package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.example.data.local.AppDatabase

object ContactHelper {

    /**
     * Resolves the caller's display name by:
     * 1. Querying the system ContactsContract using PhoneLookup (if READ_CONTACTS permission is granted)
     * 2. Querying the local Room database to match repeat business customers by phone
     * 3. Defaulting to "New Caller" or "Unknown Caller"
     */
    suspend fun resolveCallerName(context: Context, phoneNumber: String?): String {
        if (phoneNumber.isNullOrBlank()) {
            return "Caller"
        }

        val trimmedPhone = phoneNumber.trim()

        // 1. Try querying Android ContactsContract
        val contactName = getContactNameFromSystem(context, trimmedPhone)
        if (!contactName.isNullOrBlank()) {
            return contactName
        }

        // 2. Try querying App's internal appointments database for repeat clients
        try {
            val db = AppDatabase.getDatabase(context)
            val customerName = db.appointmentDao().getCustomerNameByPhone(trimmedPhone)
            if (!customerName.isNullOrBlank()) {
                return customerName
            }
        } catch (_: Exception) {}

        return "New Caller"
    }

    /**
     * Looks up contact display name in ContactsContract.
     */
    fun getContactNameFromSystem(context: Context, phoneNumber: String): String? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        val name = cursor.getString(nameIndex)
                        if (!name.isNullOrBlank()) {
                            return name
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        return null
    }
}
