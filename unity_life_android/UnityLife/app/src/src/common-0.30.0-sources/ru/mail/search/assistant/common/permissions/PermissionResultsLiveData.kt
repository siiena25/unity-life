package ru.mail.search.assistant.common.permissions

internal class PermissionResultsLiveData :
    MergingSingleLiveDataEvent<PermissionsCallback.Result>() {

    override fun mergeResults(
        oldItems: List<PermissionsCallback.Result>,
        newItems: List<PermissionsCallback.Result>
    ): List<PermissionsCallback.Result> {
        val result = ArrayList<PermissionsCallback.Result>()
        oldItems.forEach { old ->
            if (newItems.none { new -> old.permission == new.permission }) {
                result.add(old)
            }
        }
        result.addAll(newItems)
        return result
    }
}