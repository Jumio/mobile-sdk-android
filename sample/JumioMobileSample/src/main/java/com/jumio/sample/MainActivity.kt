package com.jumio.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.jumio.sample.compose.theme.JumioTheme
import com.jumio.sample.compose.theme.Primary
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sample.xml.MainActivity

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			SetUpUI()
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	private fun SetUpUI() {
		JumioTheme {
			Scaffold(
				modifier = Modifier.fillMaxSize(),
				snackbarHost = {},
				topBar = {
					TopAppBar(
						title = {
							Text(text = stringResource(id = R.string.app_bar_title), style = MaterialTheme.typography.titleLarge)
						},
						colors = TopAppBarDefaults.topAppBarColors(
							containerColor = Primary,
							titleContentColor = Color.White
						)
					)
				},
				content = { innerPadding ->
					MainPage(
						modifier = Modifier.padding(innerPadding),
						onComposeClick = {
							startActivity(Intent(this, com.jumio.sample.compose.activities.MainActivity::class.java))
						},
						onXmlClick = {
							startActivity(Intent(this, MainActivity::class.java))
						}
					)
				}
			)
		}
	}

	@Composable
	private fun MainPage(modifier: Modifier, onComposeClick: () -> Unit, onXmlClick: () -> Unit) {
		Column(
			modifier = modifier
				.padding(MaterialTheme.spacing.medium)
				.fillMaxHeight(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			PrimaryButton(
				title = stringResource(id = R.string.compose).uppercase(),
				modifier = Modifier.fillMaxWidth()
			) {
				onComposeClick()
			}
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
			PrimaryButton(
				title = stringResource(id = R.string.xml).uppercase(),
				modifier = Modifier.fillMaxWidth()
			) {
				onXmlClick()
			}
		}
	}
}
