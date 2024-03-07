/*
 * Copyright 2022 Jumio Corporation, all rights reserved.
 */
package com.jumio.sample.kotlin.customui.adapter

import android.content.Context

abstract class JumioArrayAdapter<T> : RemoteViewArrayAdapter<T> {

	constructor(context: Context, resource: Int) : super(context, resource)
	constructor(context: Context, resource: Int, objects: Array<T>) : super(context, resource, objects)
	constructor(context: Context, resource: Int, objects: List<T>) : super(context, resource, objects)

	override fun getValue(position: Int): String {
		return getItem(position).toString()
	}

	override fun getOptions(): Array<String> {
		val options = Array(count) { "" }
		for (i in 0 until count) {
			options[i] = getItem(i).toString()
		}
		return options
	}
}
