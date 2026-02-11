/*
 * (c) 2026 Jumio All rights reserved. US Patent App.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.content.Context
import android.nfc.NfcAdapter
import android.os.Build
import com.jumio.commons.log.Log

internal enum class NfcChipLocation {
	TOP,
	CENTER,
	BOTTOM,
	UNKNOWN,
}

private const val TAG = "NfcLocationHelper"

internal object NfcLocationHelper {

	fun getNFCLocation(context: Context): NfcChipLocation {
		Log.d(TAG, "DeviceInfo: ${Build.MANUFACTURER}: ${Build.MODEL}: ${Build.VERSION.SDK_INT}")

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
			return NfcChipLocation.UNKNOWN
		}

		val nfcAdapter = NfcAdapter.getDefaultAdapter(context)?.takeIf { it.isEnabled } ?: return NfcChipLocation.UNKNOWN

		val antennaInfo = runCatching { nfcAdapter.getNfcAntennaInfo() }.getOrNull()
			?.takeIf { it.deviceWidth > 0 && it.deviceHeight > 0 }
			?: return NfcChipLocation.UNKNOWN

		Log.d(TAG, "Device width: ${antennaInfo.deviceWidth}, height: ${antennaInfo.deviceHeight}")

		val antenna = antennaInfo.availableNfcAntennas.firstOrNull { it.locationX != 0 && it.locationY != 0 }
			?: return NfcChipLocation.UNKNOWN

		Log.d(TAG, "Antenna found at: (${antenna.locationX},${antenna.locationY})")
		val third = antennaInfo.deviceHeight.div(3f)
		return when {
			antenna.locationY < third -> NfcChipLocation.TOP
			antenna.locationY > 2 * third -> NfcChipLocation.BOTTOM
			else -> NfcChipLocation.CENTER
		}
	}
}
