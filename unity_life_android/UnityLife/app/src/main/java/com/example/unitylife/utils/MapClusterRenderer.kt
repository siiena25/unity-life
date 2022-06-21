package com.llc.aceplace_ru.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.unitylife.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.llc.aceplace_ru.data.models.MarkerModel


class MapClusterRenderer(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<MarkerModel>?,
    private var layoutInflater: LayoutInflater
) : DefaultClusterRenderer<MarkerModel>(context, map, clusterManager) {

    override fun onBeforeClusterRendered(
        cluster: Cluster<MarkerModel>,
        markerOptions: MarkerOptions
    ) {
        super.onBeforeClusterRendered(cluster, markerOptions)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                createClusterLayout(layoutInflater, cluster.items.size)
            )
        )
    }

    @SuppressLint("InflateParams")
    private fun createClusterLayout(layoutInflater: LayoutInflater, count: Int): Bitmap {
        val clusterLayout: View = layoutInflater.inflate(R.layout.cluster_layout, null)
        val markerText = clusterLayout.findViewById(R.id.cluster_text) as TextView
        markerText.text = count.toString()
        clusterLayout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        clusterLayout.layout(0, 0, clusterLayout.measuredWidth, clusterLayout.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            clusterLayout.measuredWidth,
            clusterLayout.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        clusterLayout.draw(canvas)
        return bitmap
    }

    override fun onBeforeClusterItemRendered(
        item: MarkerModel,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(item.getIcon())
    }

    override fun getColor(clusterSize: Int): Int {
        return Color.parseColor(COLOR_VIOLET)
    }

    companion object {
        const val COLOR_VIOLET = "#796BFA"
    }
}