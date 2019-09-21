package com.example.mycoroutines

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.coroutines.*

//class SecondActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
class SecondActivity : AppCompatActivity(){
	val Tag = "Test"
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_second)

		btn_first_0.setOnClickListener {

			var progressDialog = ProgressDialog(this)
			progressDialog.show()

			// 在示例中启动了 10 个协程，且每个都工作了不同的时长
			/*repeat(10) { i ->
				launch(Dispatchers.Main){
					var deferred = async(Dispatchers.Main) {
						delay((i + 1) * 1000L) // 延迟 200 毫秒、400 毫秒、600 毫秒等等不同的时间
						Log.e(Tag, "Coroutine $i is done----${Thread.currentThread().name}")// --main
						i
					}
					delay(5000)
					btn_first_0.text = "协程${deferred.await()}" //--- 主线程才能 更新UI
					if (i == 9) {
						progressDialog.dismiss()
					}
				}
			}*/

			GlobalScope.launch(Dispatchers.Main){
				Log.e(Tag, "干活000----${Thread.currentThread().name}")// --
				var deferred = async(Dispatchers.Main) {
					//delay(  5000L)
					var a = 0
					repeat(100000_000){
						++a
					}
					Log.e(Tag, "Coroutine $a is done----${Thread.currentThread().name}")// --
					a
				}

				Log.e(Tag, "干活0----${Thread.currentThread().name}")// --
				//delay(5000)
				Log.e(Tag, "干活1----${Thread.currentThread().name}")// --
				btn_first_0.text = "协程${deferred.await()}" //--- 主线程才能 更新UI
				Log.e(Tag, "更新UI----${deferred.await()}")// --
				progressDialog.dismiss()
				Log.e(Tag, "干活2----${Thread.currentThread().name}")// --
			}

			Log.e(Tag, "干活3----${Thread.currentThread().name}")// --




		}
	}

	override fun onDestroy() {
		super.onDestroy()
		//cancel() //-- 取消协程
	}
}
