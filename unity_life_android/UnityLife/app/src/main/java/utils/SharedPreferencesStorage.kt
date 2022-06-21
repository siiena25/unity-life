package utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Nullable
import utils.consts.PrefsKeys
import java.util.Collections.emptySet
import javax.inject.Inject

class SharedPreferencesStorage @Inject constructor(
    context: Context,
) {
    private val storage: SharedPreferences =
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun putIsUserRegistered(isUserRegistered: Boolean) {
        storage.edit()
            .putBoolean(PrefsKeys.KEY_IS_USER_REGISTERED, isUserRegistered)
            .apply()
    }

    fun getIsUserRegistered(): Boolean = storage.getBoolean(PrefsKeys.KEY_IS_USER_REGISTERED, false)

    fun putLatitude(lat: Double) {
        storage.edit()
            .putFloat("lat", lat.toFloat())
            .apply()
    }

    fun putLongitude(lng: Double) {
        storage.edit()
            .putFloat("lng", lng.toFloat())
            .apply()
    }

    fun putAuthToken(token: Int) {
        storage.edit()
            .putInt(PrefsKeys.KEY_TOKEN, token)
            .apply()
    }

    fun putProfileId(profileId: String) {
        storage.edit()
            .putString(PrefsKeys.KEY_PROFILE_ID, profileId)
            .apply()
    }

    fun putFlagFirstEnter(flag: Boolean) {
        storage.edit()
            .putBoolean(PrefsKeys.KEY_FIRST_ENTER, flag)
            .apply()
    }

    fun putFlagIsUserLoggedIn(flag: Boolean) {
        storage.edit()
            .putBoolean(PrefsKeys.KEY_IS_USER_LOGGED_IN, flag)
            .apply()
    }

    fun putCheckedCategories(categoryIds: List<String>) {
        storage.edit()
            .putStringSet(PrefsKeys.KEY_CATEGORIES, categoryIds.toSet())
            .apply()
    }

    fun getCheckedCategories(): List<String> = storage.getStringSet(
        PrefsKeys.KEY_CATEGORIES,
        emptySet()
    )!!.toList()

    fun getCoordinates(): String {
        val lat = getLatitude()
        val lng = getLongitude()
        return "$lat,$lng"
    }


    fun putPushStockID(stockID: String?) {
        storage.edit()
            .putString("pushStockID", stockID)
            .apply()
    }

    fun putPushCommentID(commentID: String?) {
        storage.edit()
            .putString("pushCommentID", commentID)
            .apply()
    }

    fun putIsClickOnUpdateWindow(isClick: Boolean) {
        storage.edit()
            .putBoolean(PrefsKeys.KEY_IS_CLICK_UPDATE_WINDOW, isClick)
            .apply()
    }

    fun putUserName(name: String) {
        storage.edit()
            .putString(PrefsKeys.KEY_USERNAME, name)
            .apply()
    }

    fun putLastSearchQuery(query: String?) {
        storage.edit()
            .putString(PrefsKeys.KEY_QUERY, query)
            .apply()
    }

    fun putIsOnMarkerPromoClick(flag: Boolean) {
        storage.edit()
            .putBoolean(PrefsKeys.KEY_IS_ON_MARKER_PROMO_CLICK, flag)
            .apply()
    }

    fun getIsOnMarkerPromoClick(): Boolean =
        storage.getBoolean(PrefsKeys.KEY_IS_ON_MARKER_PROMO_CLICK, false)

    fun getLastQuery(): String? = storage.getString(PrefsKeys.KEY_QUERY, "")

    fun getUserName(): String? = storage.getString(PrefsKeys.KEY_USERNAME, "")

    fun getPushStockID(): String? = storage.getString("pushStockID", null)

    fun getPushCommentID(): String? = storage.getString("pushCommentID", null)

    fun getLatitude(): Float = storage.getFloat("lat", 55.75222f)

    fun getLongitude(): Float = storage.getFloat("lng", 37.61556f)

    fun getAuthToken(): Int = storage.getInt(PrefsKeys.KEY_TOKEN, 0)

    @Nullable
    fun getProfileId(): String? = storage.getString(PrefsKeys.KEY_PROFILE_ID, null)

    fun getFlagFirstEnter(): Boolean = storage.getBoolean(PrefsKeys.KEY_FIRST_ENTER, true)

    fun getFlagIsUserLoggedIn(): Boolean =
        storage.getBoolean(PrefsKeys.KEY_IS_USER_LOGGED_IN, false)

    fun clear() {
        storage.edit().clear().apply()
    }

    fun putSelectCategoriesOnMap(list: MutableList<String>?) {
        storage.edit()
            .putStringSet(PrefsKeys.KEY_SELECT_CATEGORIES_LIST, list?.toMutableSet())
            .apply()
    }

    fun getSelectCategoriesOnMap(): Set<String> =
        storage.getStringSet(PrefsKeys.KEY_SELECT_CATEGORIES_LIST, emptySet()) as Set<String>

    fun putLastPromosSize(size: Int) {
        storage.edit()
            .putInt(PrefsKeys.KEY_LAST_PROMOS_SIZE, size)
            .apply()
    }

    fun putRecentlyRequests(list: Set<String>) {
        storage.edit()
            .putStringSet(PrefsKeys.KEY_RECENTLY_REQUESTS, list)
            .apply()
    }

    fun getRecentlyRequests(): Set<String>? =
        storage.getStringSet(PrefsKeys.KEY_RECENTLY_REQUESTS, emptySet())

    fun putLastTransitionFromMap(layoutName: String?) {
        storage.edit()
            .putString(PrefsKeys.KEY_LAST_TRANSITION_FROM_MAP, layoutName)
            .apply()
    }

    fun getLastTransitionFromMap(): String? =
        storage.getString(PrefsKeys.KEY_LAST_TRANSITION_FROM_MAP, null)

    fun putLastClickedMarkerId(tag: String?) {
        storage.edit()
            .putString(PrefsKeys.KEY_LAST_CLICKED_MARKER_ID, tag)
            .apply()
    }

    fun putUserBalance(data: String) {
        storage.edit()
            .putString(PrefsKeys.KEY_USER_BALANCE, data)
            .apply()
    }

    fun getUserBalance(data: String): String? = storage.getString(PrefsKeys.KEY_USER_BALANCE, null)
}