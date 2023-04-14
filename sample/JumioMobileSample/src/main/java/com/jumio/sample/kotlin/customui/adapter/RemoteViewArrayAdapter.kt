package com.jumio.sample.kotlin.customui.adapter

import android.content.Context
import android.widget.ArrayAdapter

abstract class RemoteViewArrayAdapter<T> : ArrayAdapter<T> {
	constructor(context: Context, resource: Int) : super(context, resource)
	constructor(context: Context, resource: Int, textViewResourceId: Int) : super(
		context,
		resource,
		textViewResourceId
	)

	constructor(context: Context, resource: Int, objects: Array<T>) : super(context, resource, objects)
	constructor(context: Context, resource: Int, textViewResourceId: Int, objects: Array<T>) : super(
		context,
		resource,
		textViewResourceId,
		objects
	)

	constructor(context: Context, resource: Int, objects: List<T>) : super(context, resource, objects)
	constructor(context: Context, resource: Int, textViewResourceId: Int, objects: List<T>) : super(
		context,
		resource,
		textViewResourceId,
		objects
	)

	abstract fun getValue(position: Int): String
	abstract fun getOptions(): Array<String>
}
