// (c) 2026 Jumio All rights reserved. US Patent App.
package com.jumio.sample.compose.views.navigation

import kotlinx.serialization.Serializable

sealed class AppNavigation {

	@Serializable
	data object SelectCountryAndDocument : AppNavigation()

	@Serializable
	data object Consent : AppNavigation()

	@Serializable
	data object Scan : AppNavigation()

	@Serializable
	data object AcquireMode : AppNavigation()

	@Serializable
	data object Confirmation : AppNavigation()

	@Serializable
	data object Rejection : AppNavigation()

	@Serializable
	data class Loader(val title: String) : AppNavigation()

	@Serializable
	data class Error(val message: String, val isRetryable: Boolean) : AppNavigation()

	@Serializable
	data object DigitalIdentity : AppNavigation()

	@Serializable
	data object UploadFileHelp : AppNavigation()

	@Serializable
	data object NfcScan : AppNavigation()

	@Serializable
	data object IDFound : AppNavigation()
}
