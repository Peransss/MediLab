package com.example.medilab.util

import com.google.firebase.Timestamp

/**
 * Safely converts a Firestore-deserialized timestamp field (which may arrive as
 * [Long], [String], [Timestamp], or generic [Number]) to epoch milliseconds.
 *
 * Firestore's `toObject<T>()` can deserialize a server-timestamp field as any of
 * the above types depending on SDK version, field type, and whether the value was
 * set by the server or by a client.  Always access `createdAt` via this function
 * (or the `createdAtMillis` computed property on each model) rather than casting
 * the raw `Any` field directly.
 *
 * @receiver The raw value stored in the Firestore model's `createdAt: Any` field.
 * @return Epoch milliseconds, falling back to [System.currentTimeMillis] when the
 *         value cannot be parsed.
 */
fun Any.toFirestoreMillis(): Long = when (this) {
    is Timestamp -> this.toDate().time
    is Long      -> this
    is String    -> this.toLongOrNull() ?: System.currentTimeMillis()
    is Number    -> this.toLong()
    else         -> System.currentTimeMillis()
}
