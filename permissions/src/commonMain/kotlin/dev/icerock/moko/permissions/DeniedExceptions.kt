package dev.icerock.moko.permissions

open class DeniedException(val permission: Permission, message: String?) : Exception(message) {
    constructor(permission: Permission) : this(permission, null)
}

class DeniedAlwaysException(permission: Permission, message: String?) : DeniedException(permission, message) {
    constructor(permission: Permission) : this(permission, null)
}
