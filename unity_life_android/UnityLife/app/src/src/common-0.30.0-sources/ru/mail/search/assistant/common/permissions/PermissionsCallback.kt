package ru.mail.search.assistant.common.permissions

import androidx.lifecycle.LiveData

class PermissionsCallback {

    val request: LiveData<List<Request>> get() = _requests
    val results: LiveData<List<Result>> get() = _results

    private val _requests = PermissionRequestsLiveData()
    private val _results = PermissionResultsLiveData()

    fun handleResults(results: List<Result>) {
        _results.setValue(results)
    }

    fun requestPermissions(permissions: List<String>, requestCode: Int? = null) {
        requestPermissions(Request(permissions, requestCode))
    }

    fun requestPermissions(request: Request) {
        _requests.setValue(listOf(request))
    }

    data class Request(val permissions: List<String>, val requestCode: Int? = null)

    data class Result(val permission: String, val grantResult: Int, val requestCode: Int)
}