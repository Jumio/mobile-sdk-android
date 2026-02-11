/*
 * (c) 2026 Jumio All rights reserved. US Patent App.
 */
package com.jumio.sample.compose.views.nfcanimation

import android.view.View
import javax.security.auth.Destroyable

internal interface NfcHelpAnimationInterface : Destroyable {
	fun configure(rootView: View)
	fun start()
	fun resume()
	fun pause()
}
