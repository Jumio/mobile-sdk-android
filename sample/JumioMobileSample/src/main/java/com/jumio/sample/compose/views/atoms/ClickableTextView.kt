// (c) 2026 Jumio All rights reserved. US Patent App.
package com.jumio.sample.compose.views.atoms

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing

@Composable
fun ClickableTextView(text: String, url: String) {
	val context = LocalContext.current
	ClickableText(
		text = AnnotatedString(
			text = text,
			spanStyle = SpanStyle(
				color = MaterialTheme.colors.primary,
				fontWeight = FontWeight.Bold,
				textDecoration = TextDecoration.Underline,
				fontSize = 16.sp
			)
		),
		style = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center),
		onClick = {
			val intent = Intent(Intent.ACTION_VIEW).apply {
				data = url.toUri()
			}
			context.startActivity(intent)
		},
		modifier = Modifier
			.fillMaxWidth()
			.heightIn(min = 18.dp)
			.padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.medium)
	)
}
