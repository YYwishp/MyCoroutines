package com.example.mycoroutines

import android.util.Log
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

/**
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~         
 * Created by gaoyuxiang on 2019-09-24.
 * ==============================
 * 功能描述：自定义拦截器
 *
 *
 */
class MyContinuationInterceptor:ContinuationInterceptor {
	override val key: CoroutineContext.Key<*>
		get() = ContinuationInterceptor

	override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
		return MyContinuation(continuation)
	}
}



class MyContinuation<T>(val continuation: Continuation<T>):Continuation<T>{
	override val context: CoroutineContext
		get() =continuation.context

	override fun resumeWith(result: Result<T>) {

		Log.e("Test","-----------恢复----------")

		continuation.resumeWith(result)


	}
}