package com.islam.composepermisssiondemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultiplePermissions()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SinglePermission() {

    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifeCycleOwner,
        effect = {
            val eventObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        permissionState.launchPermissionRequest()
                    }
                }
            }
            lifeCycleOwner.lifecycle.addObserver(eventObserver)
            onDispose { lifeCycleOwner.lifecycle.removeObserver(eventObserver) }
        })
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when {
            permissionState.hasPermission -> {
                Text(text = "Reading external permission is granted")
            }
            permissionState.shouldShowRationale -> {
                Text(text = "Reading external permission is required by this app")
            }
            !permissionState.hasPermission && !permissionState.shouldShowRationale -> {
                Text(text = "Reading external permission is required by this app")
            }
        }

    }


}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissions() {
    val context = LocalContext.current
    val activity = context as Activity
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        permissionsState.permissions.forEach {

            when (it.permission) {

                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    when {
                        it.hasPermission -> {
                            Text(text = "Reading external permission is required by this app")
                        }
                        it.shouldShowRationale -> {
                            Text(text = "Read Ext Storage permission is needed")
                        }
                        !it.hasPermission && !it.shouldShowRationale -> {
                            Text(text = "Navigate to settings and enable the Storage permission")
                        }
                    }
                }

                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    when {
                        it.hasPermission -> {
                            Text(text = " WRITE_EXTERNAL_STORAGE is work")
                        }
                        it.shouldShowRationale -> {
                            Text(text = "Navigate to settings and enable the Storage permission")
                        }
                        !it.hasPermission && !it.shouldShowRationale -> {
                            Text(text = "Navigate to settings and enable the Location permission")
                        }
                    }
                }
                Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                    when {
                        it.hasPermission -> {
                            Text(text = "Permission is done")
                        }
                        it.shouldShowRationale -> {
                            val intent =
                                Intent(
                                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                    Uri.parse("package:${activity.packageName}")
                                )
                            context.startActivity(intent)
                        }
                        !it.hasPermission && !it.shouldShowRationale -> {
                            val intent =
                                Intent(
                                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                    Uri.parse("package:${activity.packageName}")
                                )
                            context.startActivity(intent)
                            Toast.makeText(context, "the permission denied", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
}



















