package ru.mail.search.assistant.common.permissions

internal class PermissionRequestsLiveData : MergingSingleLiveDataEvent<PermissionsCallback.Request>() {

    override fun mergeResults(
        oldItems: List<PermissionsCallback.Request>,
        newItems: List<PermissionsCallback.Request>
    ): List<PermissionsCallback.Request> {
        val result = ArrayList<PermissionsCallback.Request>()
        oldItems.forEach { old ->
            if (newItems.none { new -> old.permissions == new.permissions }) {
                result.add(old)
            }
        }
        result.addAll(newItems)
        return result
    }
}