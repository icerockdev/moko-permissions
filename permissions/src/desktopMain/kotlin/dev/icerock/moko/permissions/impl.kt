package dev.icerock.moko.permissions

class PermissionsControllerImpl : PermissionsController {
    override suspend fun providePermission(permission: Permission) {
        //TODO("Not yet implemented")
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return true
       // TODO("Not yet implemented")
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return PermissionState.Granted
       // TODO("Not yet implemented")
    }

    override fun openAppSettings() {
        //TODO("Not yet implemented")
    }

    override fun bind() {
        //TODO("Not yet implemented")
    }
}