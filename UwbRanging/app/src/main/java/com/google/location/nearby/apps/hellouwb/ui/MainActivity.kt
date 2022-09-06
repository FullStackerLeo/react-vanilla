package com.google.location.nearby.apps.hellouwb.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.location.nearby.apps.hellouwb.HelloUwbApplication


private const val PERMISSION_REQUEST_CODE = 1234

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestPermissions()
    (application as HelloUwbApplication).initContainer {
      runOnUiThread { setContent { HelloUwbApp((application as HelloUwbApplication).container) } }
    }

    /**
     * Check if device supports Ultra-wideband
     */
    val packageManager: PackageManager = applicationContext.packageManager
    val deviceSupportsUwb = packageManager.hasSystemFeature("android.hardware.uwb")

    if (!deviceSupportsUwb ) {
      Log.e("UWB Sample", "Device does not support Ultra-wideband")
      Toast.makeText(applicationContext, "Device does not support UWB", Toast.LENGTH_SHORT).show()
      finishAndRemoveTask();
    }
    else {
      Toast.makeText(applicationContext, "Device supports UWB", Toast.LENGTH_SHORT).show()
    }
  }

  private fun requestPermissions() {
    if (!arePermissionsGranted()) {
      requestPermissions(PERMISSIONS_REQUIRED, PERMISSION_REQUEST_CODE)
    }
  }

  private fun arePermissionsGranted(): Boolean {
    for (permission in PERMISSIONS_REQUIRED) {
      if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        return false
      }
    }
    return true
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    for (result in grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        requestPermissions()
      }
    }
  }

  companion object {

    private val PERMISSIONS_REQUIRED_BEFORE_T =
      listOf(
        // Permissions needed by Nearby Connection
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,

        // permission required by UWB API
        Manifest.permission.UWB_RANGING
      )

    private val PERMISSIONS_REQUIRED_T =
      arrayOf(
        Manifest.permission.NEARBY_WIFI_DEVICES,
      )

    private val PERMISSIONS_REQUIRED =
      PERMISSIONS_REQUIRED_BEFORE_T.toMutableList()
        .apply {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addAll(PERMISSIONS_REQUIRED_T)
          }
        }
        .toTypedArray()
  }
}
