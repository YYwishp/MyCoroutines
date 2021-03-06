package com.example.mycoroutines

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

	val Tag = "Test"




	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		btn_first_0.setOnClickListener {
			startActivity(Intent(this, SecondActivity::class.java))
		}




		btn_first.setOnClickListener {
			//GlobalScope.launch 执行在子线程
			/*GlobalScope.launch {
				// 在后台启动一个新的协程并继续
				delay(5000L) // 非阻塞的等待 5 秒钟（默认时间单位是毫秒）

				btn_first.text = "5秒后"
	            Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程4
				Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程

				Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程1
				Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程2
				Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程3
				Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程5-dev1
				Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程6-dev1
				Log.e(Tag, "World! ----${Thread.currentThread().name}") // 在延迟后打印输出    子线程7-dev1



			}
			Log.e(Tag, "Hello,----${Thread.currentThread().name}") // 协程已在等待时主线程还在继续 主线程*/


			GlobalScope.launch(Dispatchers.Main) {
				var progressDialog = ProgressDialog(this@MainActivity)
				progressDialog.show()

				launch {

				}

				Log.e(Tag, "World! ----${Thread.currentThread().name}")
				Log.e(Tag, "World! ----${Thread.currentThread().name}")
				Log.e(Tag, "Wor ----${Thread.currentThread().name}")

				withContext(Dispatchers.IO){
					Log.e(Tag, "aaaaaaa ----${Thread.currentThread().name}")
					Log.e(Tag, "aaaaaa ----${Thread.currentThread().name}")
					Log.e(Tag, "aaaa ----${Thread.currentThread().name}")
					Log.e(Tag, "aa ----${Thread.currentThread().name}    开始时间 = ${System.currentTimeMillis()}")
					//delay(4000)


					//=== 模拟耗时操作
					var a = 0
					repeat(1000000_00){
						++a
					}
					Log.e(Tag, "aa   耗时操作结束 ----${Thread.currentThread().name}   结束时间 =  ${System.currentTimeMillis()}")


				}

				Log.e(Tag, "vvvvvvvvvv ----${Thread.currentThread().name}")
				Log.e(Tag, "vvvvvvvvv ----${Thread.currentThread().name}")
				Log.e(Tag, "vvvvvvvv ----${Thread.currentThread().name}")
				Log.e(Tag, "vvvvvvv ----${Thread.currentThread().name}")
				Log.e(Tag, "vvvvvv ----${Thread.currentThread().name}")


				progressDialog.dismiss()

			}




		}



		btn_2.setOnClickListener {
			/**
			 * 注意：GlobalScope.launch 是非阻塞 在内部执行 delay 是不阻塞UI线程；
			 * 但是，runBlocking 是阻塞线程的，内部 delay 阻塞 UI线程，导致 progressDialog 不显示
			 */
			var progressDialog = ProgressDialog(this@MainActivity)
			progressDialog.show()
			// 开始执行主协程 非阻塞主线程
			GlobalScope.launch {
				// 在后台启动一个新的协程并继续
				delay(1000L)   //
				Log.e(Tag, "World!----${Thread.currentThread().name}")//子线程
			}
			Log.e(Tag, "Hello0,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
			Log.e(Tag, "Hello1,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
			Log.e(Tag, "Hello2,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
			Log.e(Tag, "Hello3,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
			Log.e(Tag, "Hello4,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
			Log.e(Tag, "Hello5,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程

			// 但是这个表达式阻塞了主线程
			runBlocking {
				Log.e(Tag, "主线程 开始 延迟----${Thread.currentThread().name}")//子线程
				delay(2000L)
				Log.e(Tag, "主线程 结束 延迟----${Thread.currentThread().name}")//子线程

				progressDialog.dismiss()
			}








		}


		btn_3.setOnClickListener {
			/**
			 *  先执行 Hello
			 *  然后 job.join()执行 等待3秒 执行 World!
			 *  再然后执行 join 结束
			 *
			 *
			 */
			//todo: runBlocking 内部的 delay 方法是阻塞主线程的
			runBlocking {
				val job = GlobalScope.launch {// 启动一个新协程并保持对这个作业的引用
					//主线程阻塞 3秒
					delay(3000L)
					Log.e(Tag,"World!----${Thread.currentThread().name}") //子线程 DefaultDispatcher-worker-2
				}

				Log.e(Tag,"Hello,----${Thread.currentThread().name}") //主线程
				job.join() // 等待直到子协程执行结束


				Log.e(Tag,"join 结束,----${Thread.currentThread().name}")
			}
		}


		btn_4.setOnClickListener {
			/**
			 *  先执行 Hello1，Hello2，Hello3，Hello4
			 *  然后等待3秒 执行 World!  （塞主线程）
			 *  再然后执行 Hello44444
			 */

			/*runBlocking {
				launch {
					delay(3000L)
					Log.e(Tag, "World!----${Thread.currentThread().name}")
				}
				Log.e(Tag, "Hello1,----${Thread.currentThread().name}")
				Log.e(Tag, "Hello2,----${Thread.currentThread().name}")
				Log.e(Tag, "Hello3,----${Thread.currentThread().name}")
				Log.e(Tag, "Hello4,----${Thread.currentThread().name}")
			}

			Log.e(Tag, "Hello44444,----${Thread.currentThread().name}")*/

			/**
			 * 先执行 Hello44444
			 * 然后 Hello1，Hello2，Hello3，Hello4
			 * 然后等 3秒  （不阻塞主线程）
			 * 然后执行 World!
			 */
			/*GlobalScope.launch(Dispatchers.Main) {
				launch(){
					delay(3000L)
					Log.e(Tag, "World!----${Thread.currentThread().name}")
				}
				Log.e(Tag, "Hello1,----${Thread.currentThread().name}")
				Log.e(Tag, "Hello2,----${Thread.currentThread().name}")
				Log.e(Tag, "Hello3,----${Thread.currentThread().name}")
				Log.e(Tag, "Hello4,----${Thread.currentThread().name}")
			}

			Log.e(Tag, "Hello44444,----${Thread.currentThread().name}")*/
			/**
			 * 4者并发执行
			 */
			/*GlobalScope.launch(Dispatchers.Main){
				launch(){
					delay(3000L)
					Log.e(Tag, "111111----${Thread.currentThread().name}")
				}

				launch(){
					delay(1000L)
					Log.e(Tag, "222222----${Thread.currentThread().name}")
				}

				launch(){
					delay(500L)
					Log.e(Tag, "333333----${Thread.currentThread().name}")
				}

				Log.e(Tag, "44444,----${Thread.currentThread().name}")

			}*/
			/**
			 * withContext 是 顺序执行的，并且执行完了才能执行下面代码
			 *
			 * 3个launch 方法，在withContext 执行结束后，并发执行，
			 *
			 * 下面一个withContext 在第一个 withContext执行结束后，与3个launch一起并发执行
			 *
			 */
			var progressDialog = ProgressDialog(this@MainActivity)
			progressDialog.show()

			GlobalScope.launch(Dispatchers.Main){
				//withContext(Dispatchers.IO){
				//	Log.e(Tag, "withContext start----${Thread.currentThread().name}  ${System.currentTimeMillis()}")
				//	delay(5000L)
				//	Log.e(Tag, "withContext 11111----${Thread.currentThread().name}  ${System.currentTimeMillis()}")
				//}


				launch(Dispatchers.Main){
					delay(8000L)
					Log.e(Tag, "111111----${Thread.currentThread().name}   ${System.currentTimeMillis()}")
				}

				launch(Dispatchers.Main){
					delay(1000L)
					Log.e(Tag, "222222----${Thread.currentThread().name}   ${System.currentTimeMillis()}")
				}

				launch(Dispatchers.Main){
					delay(500L)
					Log.e(Tag, "333333----${Thread.currentThread().name}   ${System.currentTimeMillis()}")
				}


				//withContext(Dispatchers.IO){
				//	delay(8000L)
				//	Log.e(Tag, "withContext 44444----${Thread.currentThread().name}  ${System.currentTimeMillis()}")
				//}

				Log.e(Tag, "44444,----${Thread.currentThread().name}   ${System.currentTimeMillis()}")


				progressDialog.dismiss()

			}











		}

		//todo：作用域构建器
		btn_5.setOnClickListener {

			/**
			 * runBlocking 是阻塞式，卡主了UI
			 * 1，launch 启动，嵌套的launch 启动，开始时间；三者同时执行
			 * 2，过3秒 执行 “结束时间”，“scope0，scope1，scope2，scope3”
			 * 3，在过1秒之后，执行 “Task from runBlocking”
			 * 4，再过4秒后：也就是时间到了8秒，执行“Task from 嵌套 launch”
			 * 5，最后执行 “Coroutine scope is 结束”
			 *
			 */
			runBlocking(Dispatchers.IO) {
				launch(Dispatchers.IO) {
					Log.e(Tag, "launch 启动---${System.currentTimeMillis()}--${Thread.currentThread().name}")
					delay(4000)
					Log.e(Tag, "Task from runBlocking----${Thread.currentThread().name}")
				}
				// 挂起函数。
				coroutineScope {
					launch {
						Log.e(Tag, "嵌套的launch 启动---${System.currentTimeMillis()}--${Thread.currentThread().name}")

						delay(8000)
						Log.e(Tag, "Task from 嵌套 launch----${Thread.currentThread().name}")
					}
					Log.e(Tag, "开始时间----${System.currentTimeMillis()}----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					delay(3000L)
					Log.e(Tag, "结束时间----${System.currentTimeMillis()}----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope0----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope1----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope2----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope3----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
				}
				Log.e(Tag, "Coroutine scope is 结束----${Thread.currentThread().name}") // 这一行在内嵌 launch 执行完毕后才输出
			}

			/**
			 * GlobalScope.launch(Dispatchers.Main) 是 非 阻塞式，不卡UI
			 *
			 * 1，launch 启动，嵌套的launch 启动，开始时间；三者同时执行
			 * 2，过3秒 执行 “结束时间”，“scope0，scope1，scope2，scope3”
			 * 3，在过1秒之后，执行 “Task from runBlocking”
			 * 4，再过4秒后：也就是时间到了8秒，执行“Task from 嵌套 launch”
			 * 5，最后执行 “Coroutine scope is 结束”
			 *
			 */
			GlobalScope.launch(Dispatchers.Main) {
				launch(Dispatchers.IO) {
					Log.e(Tag, "launch 启动---${System.currentTimeMillis()}--${Thread.currentThread().name}")

					delay(4000)
					Log.e(Tag, "Task from runBlocking----${Thread.currentThread().name}")
				}
				// 创建一个协程作用域
				coroutineScope {
					launch {
						Log.e(Tag, "嵌套的launch 启动---${System.currentTimeMillis()}--${Thread.currentThread().name}")

						delay(8000)
						Log.e(Tag, "Task from 嵌套 launch----${Thread.currentThread().name}")
					}
					Log.e(Tag, "开始时间----${System.currentTimeMillis()}----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					delay(3000L)
					Log.e(Tag, "结束时间----${System.currentTimeMillis()}----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope0----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope1----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope2----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					Log.e(Tag, "Task from coroutine scope3----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
				}
				Log.e(Tag, "Coroutine scope is 结束----${Thread.currentThread().name}") // 这一行在内嵌 launch 执行完毕后才输出
			}
		}

		//todo 提取函数重构
		btn_6.setOnClickListener {
			suspend fun doWorld() {
				delay(1000)
				Log.e(Tag, "World!----${Thread.currentThread().name}")
			}

			runBlocking {
				launch {
					doWorld()
				}
				Log.e(Tag, "Hello,----${Thread.currentThread().name}")
			}
		}
		//todo 协程很轻量
		btn_7.setOnClickListener {
			/**
			 * runBlocking 是阻塞 主线程的，运行时会被 delay卡住 主线程
			 * 注意：运行的时候，两个 launch{...} 同时启动，同时执行100个，不会占用太大内存，假如换成 线程的话，一口气100*2条线程，内存暴涨
			 *
			 *
			 */
			//runBlocking {
			//	repeat(10000) {
			//		launch {
			//			delay(100)
			//			Log.e(Tag, "$it---${System.currentTimeMillis()}---${Thread.currentThread().name}")
			//		}
			//
			//		launch {
			//			delay(100)
			//			Log.e(Tag, "$it---${System.currentTimeMillis()}---${Thread.currentThread().name}")
			//		}
			//
			//	}
			//}
			/**
			 * 尝试使用线程来实现。对比 协程方法
			 * 注意：这里看似是启动 十万个线程 不会卡主线程，但是实际情况确实，一直卡在创建线程的过程中，也就是卡住了主线程，可能会导致 ANR
			 * 并且通过 Profiler 内存分析，可以看见，一直在 GC 内存回收，说明消耗大量内存，可有可能会导致 OOM
			 *
			 *
			 *
			 */

			//repeat(1000) {
			//	thread {
			//		Log.e(Tag, "$it---${System.currentTimeMillis()}---${Thread.currentThread().name}")
			//	}
			//
			//}
			/**
			 * GlobalScope.launch(Dispatchers.Main) 非阻塞线程，运行时不会被 delay 阻塞
			 *
			 *
			 */
			GlobalScope.launch(Dispatchers.Main) {
				repeat(1000) {
					launch(Dispatchers.Unconfined) {
						delay(10)
						Log.e(Tag, "$it---${System.currentTimeMillis()}---${Thread.currentThread().name}")
					}
				}
			}


		}




		//todo 全局协程像守护线程
		btn_8.setOnClickListener {
			runBlocking {
				GlobalScope.launch {
					repeat(100) {
						Log.e(Tag, "I'm sleeping $it ...----${Thread.currentThread().name} ")
						delay(500L)
					}
				}
				Log.e(Tag, "Hello,----${Thread.currentThread().name}")
				delay(1300)
				Log.e(Tag, "World!----${Thread.currentThread().name}")
			}
		}





		//todo 取消协程的执行
		btn_9.setOnClickListener {
			/**
			 *  这里还是 阻塞主线程的 按钮点击后，会卡住1.3秒
			 */
			//runBlocking {
			//	var job = launch {
			//		repeat(1000) {
			//			Log.e(Tag, "job: I'm sleeping $it ...----${Thread.currentThread().name} ")  //主线程
			//			//delay(500L)
			//
			//			Thread.sleep(500)
			//
			//		}
			//	}
			//
			//
			//	Log.e(Tag, "main: 开始延迟----${Thread.currentThread().name}!")
			//	delay(1300L) // 延迟一段时间
			//	Log.e(Tag, "main: I'm tired of waiting----${Thread.currentThread().name}!")
			//	job.cancel() // 取消该作业
			//	job.join() // 等待作业执行结束
			//	Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name}")
			//}


			//GlobalScope.launch(Dispatchers.Main) {
			//	var job = launch(Dispatchers.IO) {
			//		repeat(100) {
			//			Log.e(Tag, "job: I'm sleeping $it ...----${Thread.currentThread().name} ")  //主线程
			//			//delay(500L)/
			//			Thread.sleep(500)
			//		}
			//	}
			//
			//
			//	Log.e(Tag, "main: 开始延迟----${Thread.currentThread().name}!")
			//	delay(1300L) // 延迟一段时间
			//	Log.e(Tag, "main: I'm tired of waiting----${Thread.currentThread().name}!")
			//	job.cancel() // 取消该作业
			//	//job.join() // 等待作业执行结束
			//	Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name}")
			//}


			GlobalScope.launch(Dispatchers.Main) {
				var job = launch(Dispatchers.Main) {
					try {
						repeat(100) {
							Log.e(Tag, "job: I'm sleeping $it ...----${Thread.currentThread().name} ")  //主线程
							delay(500L)
							//Thread.sleep(500)
						}
					} finally {
						println("job: I'm running finally")
					}
					//var i = 0
					//while (true) {
					//	Log.e(Tag, "job: I'm sleeping ${i++} ...----${Thread.currentThread().name} ")  //主线程
					//}
				}


				Log.e(Tag, "main: 开始延迟----${Thread.currentThread().name}!")
				delay(1300L) // 延迟一段时间
				Log.e(Tag, "main: I'm tired of waiting----${Thread.currentThread().name}!")
				job.cancel() // 取消该作业
				//job.join() // 等待作业执行结束
				Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name}")
			}




		}
		//todo 取消是协作的
		btn_10.setOnClickListener {
			/*runBlocking {
				val startTime = System.currentTimeMillis()
				val job = launch(Dispatchers.Default) {
					try {
						var nextPrintTime = startTime
						var i = 0
						while (i < 10) { // 一个执行计算的循环，只是为了占用 CPU
							// 每秒打印消息两次
							if (System.currentTimeMillis() >= nextPrintTime) {
								Log.e(Tag, "job: I'm sleeping ${i++} ...----${Thread.currentThread().name}")//子线程
								nextPrintTime += 500L
							}
						}
					} finally {
						Log.e(Tag, "job: I'm running finally ----${Thread.currentThread().name} ")
					}
				}


				delay(1300L) // 等待一段时间
				Log.e(Tag, "main: I'm tired of waiting!----${Thread.currentThread().name}")
				job.cancelAndJoin() // 取消一个作业并且等待它结束  《如果协程正在执行计算任务，并且没有检查取消的话，那么它是不能被取消的》

				Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name}")
			}*/

			GlobalScope.launch(Dispatchers.Main) {
				val startTime = System.currentTimeMillis()
				val job = launch(Dispatchers.Main) {
					try {
						var nextPrintTime = startTime
						var i = 0
						while (i < 10) { // 一个执行计算的循环，只是为了占用 CPU
							// 每秒打印消息两次
							if (System.currentTimeMillis() >= nextPrintTime) {
								Log.e(Tag, "job: I'm sleeping ${i++} ...----${Thread.currentThread().name}")//子线程
								nextPrintTime += 500L
							}
						}
					} finally {
						Log.e(Tag, "job: I'm running finally ----${Thread.currentThread().name} ")
					}
				}

				launch(Dispatchers.Main){
					//delay(500L)
					Log.e(Tag, "333333----${Thread.currentThread().name}   ${System.currentTimeMillis()}")
				}

				delay(1300L) // 等待一段时间
				Log.e(Tag, "main: I'm tired of waiting!----${Thread.currentThread().name}")
				job.cancelAndJoin() // 取消一个作业并且等待它结束  《如果协程正在执行计算任务，并且没有检查取消的话，那么它是不能被取消的》

				Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name}")
			}








		}






		//todo 使计算代码可取消 isActive 是一个可以被使用在 CoroutineScope 中的扩展属性。
		btn_11.setOnClickListener {
			/**
			 * job.cancelAndJoin() 方法执行后，isActive 就会返回 false，不活跃状态
			 */
			runBlocking {
				val startTime = System.currentTimeMillis()
				val job = launch(Dispatchers.Default) {
					var nextPrintTime = startTime
					var i = 0
					Log.e(Tag, "isActive!----${isActive} ")
					while (isActive) { // 可以被取消的计算循环 当1300L时间过了，isActive就返回false
						// 每秒打印消息两次
						if (System.currentTimeMillis() >= nextPrintTime) {
							Log.e(Tag, "job: I'm sleeping ${i++} ...----${Thread.currentThread().name} ") // DefaultDispatcher-worker-1
							nextPrintTime += 500L
							Log.e(Tag, "isActive!内----${isActive} ")
						}


					}
				}
				delay(1800L) // 等待一段时间
				Log.e(Tag, "main: I'm tired of waiting!----${Thread.currentThread().name} ")
				job.cancelAndJoin() // 取消该作业并等待它结束
				Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name}")
			}
		}
		//todo 在 finally 中释放资源
		btn_12.setOnClickListener {
			runBlocking {
				val job = launch {
					/*try {
						repeat(1000) { i ->
							Log.e(Tag, "job: I'm sleeping $i ...----${Thread.currentThread().name} ") //主线程
							//delay(500L)
						}
					} finally {
						//delay(500L)
						Log.e(Tag, "job: I'm running finally ----${Thread.currentThread().name} ")
					}*/


					try {
						repeat(1000) { i ->
							Log.e(Tag, "job: I'm sleeping $i ...----${Thread.currentThread().name} ") //主线程
							delay(500L)
						}
					} catch (e: Exception) {
						Log.e(Tag, "异常 ${e.message} ")
					} finally {
						Log.e(Tag, "job: I'm running finally ----${Thread.currentThread().name} ")
					}
				}
				delay(1300L) // 延迟一段时间
				Log.e(Tag, "main: I'm tired of waiting!----${Thread.currentThread().name} ")
				//job.cancelAndJoin() // 取消该作业并且等待它结束
				job.cancel()

				Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name} ")
			}


			/*runBlocking {
				val startTime = System.currentTimeMillis()

				val job = launch {
					try {
						var nextPrintTime = startTime
						var i = 0
						while (i < 10) { // 一个执行计算的循环，只是为了占用 CPU
							// 每秒打印消息两次
							if (System.currentTimeMillis() >= nextPrintTime) {
								Log.e(Tag, "job: I'm sleeping ${i++} ...----${Thread.currentThread().name}")//子线程
								nextPrintTime += 500L
							}
						}
					}


					finally {
						//delay(500L)
						Log.e(Tag, "job: I'm running finally ----${Thread.currentThread().name} ")
					}
				}


				delay(100L) // 延迟一段时间
				Log.e(Tag, "main: I'm tired of waiting!----${Thread.currentThread().name} ")
				job.cancelAndJoin() // 取消该作业并且等待它结束
				Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name} ")
			}*/







		}





		//=== 运行不能取消的代码块
		btn_13.setOnClickListener {
			runBlocking {
				val job = launch {
					try {
						repeat(1000) { i ->
							Log.e(Tag, "job: I'm sleeping $i ...----${Thread.currentThread().name}")
							delay(500L)
						}
					} finally {
						withContext(NonCancellable) {
							Log.e(Tag, "job: I'm running finally----${Thread.currentThread().name}")
							delay(1000L)
							Log.e(Tag, "job: And I've just delayed for 1 sec because I'm non-cancellable----${Thread.currentThread().name}")
						}
					}
				}
				delay(1300L) // 延迟一段时间
				Log.e(Tag, "main: I'm tired of waiting!----${Thread.currentThread().name}")
				job.cancelAndJoin() // 取消该作业并等待它结束
				Log.e(Tag, "main: Now I can quit.----${Thread.currentThread().name}")
			}
		}
		//=== 超时  withTimeout
		btn_14.setOnClickListener {
			runBlocking {
				withTimeout(1300L) {
					repeat(1000) { i ->
						Log.e(Tag, "I'm sleeping $i ...----${Thread.currentThread().name} ")
						delay(500L)
					}
				}
			}
		}
		//todo:超时，withTimeoutOrNull
		btn_15.setOnClickListener {
			runBlocking {
				val result = withTimeoutOrNull(1300L) {
					repeat(1000) { i ->
						Log.e(Tag, "I'm sleeping $i ....----${Thread.currentThread().name} ")
						delay(500L)
					}
					"Done" // 在它运行得到结果之前取消它
				}
				Log.e(Tag, "Result is $result.----${Thread.currentThread().name} ")
			}
		}
		//todo:默认顺序调用
		btn_16.setOnClickListener {
			runBlocking {
				var time = measureTimeMillis {
					val one = doSomethingUsefulOne()
					val two = doSomethingUsefulTwo()
					Log.e(Tag, "The answer is ${one + two}----${Thread.currentThread().name} ")//main
				}

				Log.e(Tag, "Completed in $time ms----${Thread.currentThread().name} ")//main
			}
		}
		//todo; 使用 async 并发
		btn_17.setOnClickListener {
			GlobalScope.launch(Dispatchers.Main) {
				var progressDialog = ProgressDialog(this@MainActivity)
				progressDialog.show()

				val time = measureTimeMillis {

					log("111111111")
					val one = async(){
						Log.e(Tag, "one 该线程：----${Thread.currentThread().name}")
						doSomethingUsefulOne()
					}
					val two = async() {
						Log.e(Tag, "Two 该线程：----${Thread.currentThread().name}")
						doSomethingUsefulTwo()
					}

					log("dddddddddddddd")
					//delay(2000)
					var a = 0
					//repeat(1000000_000){
					//	++a
					//}
					log("ssssssssss")
					Log.e(Tag, "The answer is ${one.await() + two.await()}----${Thread.currentThread().name}")

					log("sssssssss")

					progressDialog.dismiss()
				}
				Log.e(Tag, "Completed in $time ms----${Thread.currentThread().name}")
			}




		}
		//todo: 惰性启动的 async
		btn_18.setOnClickListener {
			/*runBlocking<Unit> {
				val time = measureTimeMillis {
					val one = async(start = CoroutineStart.LAZY) {
						Log.e(Tag, "one 该线程：----${Thread.currentThread().name}")
						doSomethingUsefulOne()
					}
					val two = async(start = CoroutineStart.LAZY) {
						Log.e(Tag, "Two 该线程：----${Thread.currentThread().name}")

						doSomethingUsefulTwo()
					}
					// 执行一些计算
					one.start() // 启动第一个
					two.start() // 启动第二个
					Log.e(Tag, "The answer is ${one.await() + two.await()}----${Thread.currentThread().name} ")
				}
				Log.e(Tag, "Completed in $time ms----${Thread.currentThread().name} ")
			}*/



			//不阻塞主线程
			/*GlobalScope.launch(Dispatchers.Main) {
				var progressDialog = ProgressDialog(this@MainActivity)
				progressDialog.show()
				val time = measureTimeMillis {


					log("1111111")
					val one = GlobalScope.async(start = CoroutineStart.LAZY) {
						Log.e(Tag, "one 该线程：----${Thread.currentThread().name}")
						delay(4000)
						btn_18.text = "111111111"
						doSomethingUsefulOne()

					}
					val two = async(start = CoroutineStart.LAZY) {
						Log.e(Tag, "Two 该线程：----${Thread.currentThread().name}")
						delay(2000)
						btn_18.text = "222222"
						doSomethingUsefulTwo()
					}
					// 执行一些计算
					one.start() // 启动第一个
					two.start() // 启动第二个

					log("ssssssss")
					//Log.e(Tag, "The answer is ${one.await() + two.await()}----${Thread.currentThread().name} ")


					log("dddddddddddddd")
				}
				Log.e(Tag, "Completed in $time ms----${Thread.currentThread().name} ")

				progressDialog.dismiss()
			}*/




			GlobalScope.launch(Dispatchers.Main) {
				var progressDialog = ProgressDialog(this@MainActivity)
				progressDialog.show()
				val time = measureTimeMillis {


					log("1111111")
					val one = async(start = CoroutineStart.DEFAULT,context = Dispatchers.Default) {
						Log.e(Tag, "one 该线程：----${Thread.currentThread().name}")
						//delay(4000)



						withContext(Dispatchers.Main){
							btn_18.text = "111111111"
						}
						doSomethingUsefulOne()

					}
					val two = async(start = CoroutineStart.DEFAULT,context = Dispatchers.Default) {
						Log.e(Tag, "Two 该线程：----${Thread.currentThread().name}")
						//delay(2000)



						withContext(Dispatchers.Main){
							btn_18.text = "222222"
						}
						doSomethingUsefulTwo()
					}




					log("dddddddddddddd")
					delay(8000)
					//var a = 0
					//repeat(1000000_000){
					//	++a
					//}

					log("ssssssss---------------------------------------")
					Log.e(Tag, "The answer is ${one.await() + two.await()}----${Thread.currentThread().name} ")


					log("dddddddddddddd")
				}
				Log.e(Tag, "Completed in $time ms----${Thread.currentThread().name} ")

				progressDialog.dismiss()
			}






		}
		/**
		 * todo：async 风格的函数
		 */
		btn_19.setOnClickListener {
			var progressDialog = ProgressDialog(this)
			progressDialog.show()
			val time = measureTimeMillis {
				// 我们可以在协程外面启动异步执行
				Log.e(Tag, "计算开始  ----${Thread.currentThread().name} ")
				val one = somethingUsefulOneAsync()
				val two = somethingUsefulTwoAsync()

				Log.e(Tag, "正在执行别的代码----0-----${Thread.currentThread().name}")
				// 但是等待结果  必须调用其它的挂起或者阻塞
				// 当我们等待结果的时候，这里我们使用 `runBlocking { …… }` 来阻塞主线程
				//var result = runBlocking {
				//	Log.e(Tag, "The answer is ${one.await() + two.await()}----${Thread.currentThread().name}")
				//	one.await() + two.await()
				//}
				GlobalScope.launch(Dispatchers.Main) {
					//Log.e(Tag, "The answer is ${one.await() + two.await()}----${Thread.currentThread().name}")
					val time = measureTimeMillis{
						Log.e(Tag, "正在执行别的代码----1-----${Thread.currentThread().name}")
						//var result	 = one.await() + two.await()
						var result	 = one.await()


						Log.e(Tag, "sssssssssssssss")
						Log.e(Tag, "sssssssssssssss")
						Log.e(Tag, result.toString())
						btn_19.text = result.toString()
						Log.e(Tag, "tttttttt")
						Log.e(Tag, "tttttttt")
						Log.e(Tag, two.await().toString())

						progressDialog.dismiss()
					}
					Log.e(Tag, "Completed **** in $time ms ----${Thread.currentThread().name}  ")



				}
			}
			Log.e(Tag, "Completed in $time ms ----${Thread.currentThread().name}  ")

			//progressDialog.dismiss()







		}
		/**
		 * todo:使用 async 的结构化并发
		 */
		btn_20.setOnClickListener {
			var progressDialog = ProgressDialog(this)
			progressDialog.show()


			runBlocking {
				var time = measureTimeMillis {
					try {

						Log.e(Tag, "结构下并发结果 = ${concurrentSum()}----${Thread.currentThread().name}  ")
					} catch (e: Exception) {
						Log.e(Tag, "出现异常：${e.toString()}")
					}
				}
				Log.e(Tag, "完成时间  $time ms----${Thread.currentThread().name}")
				progressDialog.dismiss()
			}



			GlobalScope.launch(Dispatchers.Main){
				Log.e(Tag, "正在执行别的代码----0-----${Thread.currentThread().name}")
				var time = measureTimeMillis {
					try {

						Log.e(Tag, "结构下并发结果 = ${concurrentSum()}----${Thread.currentThread().name}  ")
					} catch (e: Exception) {
						Log.e(Tag, "出现异常：${e.toString()}")
					}
				}

				Log.e(Tag, "正在执行别的代码----1-----${Thread.currentThread().name}")


				Log.e(Tag, "完成时间  $time ms----${Thread.currentThread().name}")


				Log.e(Tag, "正在执行别的代码----2-----${Thread.currentThread().name}")
				progressDialog.dismiss()
			}




		}
		//todo:使用 async 的结构化并发 异常情况
		btn_21.setOnClickListener {
			runBlocking<Unit> {
				try {
					failedConcurrentSum()
				} catch (e: ArithmeticException) {
					Log.e(Tag, "Computation failed with ArithmeticException")
				}
			}
		}


		///////////////////////////////////////////////////////////////////////////
		// ============================= 下文与调度器 =================================
		///////////////////////////////////////////////////////////////////////////
		//todo:调度器与线程
		btn_22.setOnClickListener {
			/*runBlocking {
				//todo:运行在父协程的上下文中，即 runBlocking 主协程 === main
				launch {
					Log.e(Tag, "main runBlocking      : I'm working in thread ${Thread.currentThread().name}")//=== main
				}
				//todo: 不受限的——将工作在主线程中 === main
				launch(context = Dispatchers.Unconfined) {
					Log.e(Tag, "Unconfined            : I'm working in thread ${Thread.currentThread().name}")//=== main
				}
				//todo:将会获取默认调度器 === DefaultDispatcher-worker-2
				launch(context = Dispatchers.Default){
					Log.e(Tag, "Default               : I'm working in thread ${Thread.currentThread().name}")//=== DefaultDispatcher-worker-2

				}

				//todo:将使它获得一个新的线程 === MyOwnThread
				launch(context = newSingleThreadContext("MyOwnThread")){
					Log.e(Tag, "newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")//=== MyOwnThread
				}
			}*/


			/*GlobalScope.launch(Dispatchers.Main) {

				//todo:运行在父协程的上下文中，即 runBlocking 主协程 === main
				launch {
					Log.e(Tag, "main runBlocking      : I'm working in thread ${Thread.currentThread().name}")//=== main
				}
				//todo: 不受限的——将工作在主线程中 === main
				launch(context = Dispatchers.Unconfined) {
					Log.e(Tag, "Unconfined            : I'm working in thread ${Thread.currentThread().name}")//=== main
				}
				//todo:将会获取默认调度器 === DefaultDispatcher-worker-2
				launch(context = Dispatchers.Default){
					Log.e(Tag, "Default               : I'm working in thread ${Thread.currentThread().name}")//=== DefaultDispatcher-worker-2

				}

				//todo:将使它获得一个新的线程 === MyOwnThread
				launch(context = newSingleThreadContext("MyOwnThread")){
					Log.e(Tag, "newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")//=== MyOwnThread
				}

			}*/




			GlobalScope.launch(Dispatchers.IO) {

				//todo:运行在父协程的上下文中，即 runBlocking 主协程 === main
				launch {
					Log.e(Tag, "main runBlocking      : I'm working in thread ${Thread.currentThread().name}")//=== DefaultDispatcher-worker-3
				}
				//todo: 不受限的——将工作在主线程中 === main
				launch(context = Dispatchers.Unconfined) {
					Log.e(Tag, "Unconfined            : I'm working in thread ${Thread.currentThread().name}")//=== DefaultDispatcher-worker-1
				}
				//todo:将会获取默认调度器 === DefaultDispatcher-worker-2
				launch(context = Dispatchers.Default){
					Log.e(Tag, "Default               : I'm working in thread ${Thread.currentThread().name}")//=== DefaultDispatcher-worker-2

				}

				//todo:将使它获得一个新的线程 === MyOwnThread
				launch(context = newSingleThreadContext("MyOwnThread")){
					Log.e(Tag, "newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")//=== MyOwnThread
				}

			}











		}
		//todo: Unconfined 非受限调度器 vs 受限调度器
		btn_23.setOnClickListener {

			/*runBlocking {
				//todo: 非受限的 ——将和主线程一起工作
				launch(Dispatchers.Unconfined){
					Log.e(Tag, "Unconfined 非受限的    : I'm working in thread ${Thread.currentThread().name}")  //main
					delay(500)
					Log.e(Tag, "Unconfined 非受限的    : After delay in thread ${Thread.currentThread().name}")  //kotlinx.coroutines.DefaultExecutor
				}

				//todo:  父协程的上下文，主 runBlocking 协程
				launch {
					Log.e(Tag, "main runBlocking 受限的 : I'm working in thread ${Thread.currentThread().name}") //main
					delay(1000)
					Log.e(Tag, "main runBlocking 受限的 : After delay in thread ${Thread.currentThread().name}") //main
				}
			}*/

			GlobalScope.launch(Dispatchers.IO) {

				launch(Dispatchers.Unconfined){
					Log.e(Tag, "Unconfined 非受限的    : I'm working in thread ${Thread.currentThread().name}") //DefaultDispatcher-worker-1
					delay(500)
					Log.e(Tag, "Unconfined 非受限的    : After delay in thread ${Thread.currentThread().name}") //kotlinx.coroutines.DefaultExecutor
				}

				//todo:  父协程的上下文，主 runBlocking 协程
				launch {
					Log.e(Tag, "main runBlocking 受限的 : I'm working in thread ${Thread.currentThread().name}") //DefaultDispatcher-worker-1
					delay(1000)
					Log.e(Tag, "main runBlocking 受限的 : After delay in thread ${Thread.currentThread().name}") //DefaultDispatcher-worker-3
				}





			}








		}

		//todo:调试协程与线程
		btn_24.setOnClickListener {
			runBlocking {

				val a = async {
					log("I'm computing a piece of the answer")
					6
				}
				val b = async {
					log("I'm computing another piece of the answer")
					7
				}
				log("The answer is ${a.await() * b.await()}")



			}
		}

		//todo 在不同线程间跳转
		btn_25.setOnClickListener {
			runBlocking {
				var launch = launch(Dispatchers.Default) {
					val one = somethingUsefulOneAsync()
					val two = somethingUsefulTwoAsync()
					// 但是等待结果必须调用其它的挂起或者阻塞
					// 当我们等待结果的时候，这里我们使用 `runBlocking { …… }` 来阻塞主线程
					var result = runBlocking {
						Log.e(Tag, "The answer is ${one.await() + two.await()}----${Thread.currentThread().name}")
						one.await() + two.await()
					}


					launch(Dispatchers.Main){
						Log.e(Tag, "结果是 $result ----${Thread.currentThread().name} ")

					}
				}
			}
		}

		//todo:测试代码
		btn_26.setOnClickListener {

			/*var progressDialog = ProgressDialog(this@MainActivity)
			progressDialog.show()

			GlobalScope.launch{

				Log.e(Tag, "当前线程 ----${Thread.currentThread().name} ")
				delay(3000)
				Log.e(Tag, "挂起之后   当前线程 ----${Thread.currentThread().name} ")
				progressDialog.dismiss()

				withContext(Dispatchers.Main){
					Log.e(Tag, "  withContext 当前线程 ----${Thread.currentThread().name} ")
					btn_26.text = "测试完成"
				}



				//launch(Dispatchers.Main){
				//	Log.e(Tag, "  launch 当前线程 ----${Thread.currentThread().name} ")
				//	btn_26.text = "测试完成"
				//}


			}*/


			//todo  runBlocking 里面的 delay 会阻塞线程，而 launch 之类的不会
			runBlocking {
				Log.e(Tag, "当前线程 ----${Thread.currentThread().name} ")// ----main  -- 1
				btn_26.text = "测试开始"
				var progressDialog = ProgressDialog(this@MainActivity)
				progressDialog.show()
				//withContext(Dispatchers.IO){
				//	Log.e(Tag, "  withContext 当前线程 ----${Thread.currentThread().name} ")
				//	delay(3000)
				//}
				/*launch(Dispatchers.Default){
					Log.e(Tag, "  withContext 当前线程 ----${Thread.currentThread().name} ")
						delay(3000)
				}*/


				//todo 测试 GlobalScope.launch
				// ==== 启动 工作 协程 这里不能操作UI
				GlobalScope.launch {
					Log.e(Tag, "  GlobalScope.launch 当前线程 ----${Thread.currentThread().name} ")//----DefaultDispatcher-worker-1   -- 3
					delay(3000)
					Log.e(Tag, "  GlobalScope.launch 延迟结束 当前线程 ----${Thread.currentThread().name} ")//----DefaultDispatcher-worker-1 -- 4
					//== 切回主线程
					launch(Dispatchers.Main) {
						Log.e(Tag, "  launch(Dispatchers.Default) 当前线程 ----${Thread.currentThread().name} ")// ----main -- 5
						progressDialog.dismiss()
						btn_26.text = "测试完成"
					}
				}
				Log.e(Tag, "协程结束    当前线程 ----${Thread.currentThread().name} ")// ----main -- 2
			}

		}


		//todo 测试GlobalScope.async
		btn_27.setOnClickListener {

			//todo Dispatchers.Unconfined - 没指定，就是在当前线程 当前主线程
			GlobalScope.launch(Dispatchers.Unconfined){

				btn_27.text = "测试开始"

				var progressDialog = ProgressDialog(this@MainActivity)
				progressDialog.show()


				Log.e(Tag, "协程开始--------${Thread.currentThread().name}")//----main  ```1

				var deferred = GlobalScope.async {
					delay(1000)
					Log.e(Tag, "This is async --------${Thread.currentThread().name}")//-DefaultDispatcher-worker-1 ```4
					"测试"
				}
				Log.e(Tag, "协程  start --------${Thread.currentThread().name}")//----main  ```2
				var result = deferred.await()
				Log.e(Tag,"GlobalScope.async 结果：$result--------${Thread.currentThread().name}")//-DefaultDispatcher-worker-1 ```5
				Log.e(Tag, "GlobalScope.async END--------${Thread.currentThread().name}")//-DefaultDispatcher-worker-1 ```6
				//== 切回主线程
				launch(Dispatchers.Main){
					progressDialog.dismiss()
					btn_27.text = "测试完成" //

				}

			}
			Log.e(Tag, "主线程位于协程之后的代码执行--------${Thread.currentThread().name}")//---main ```3

		}

		//todo 测试runBlocking
		btn_28.setOnClickListener {1
			Log.e(Tag, "----Start----${Thread.currentThread().name}")
			var runBlocking = runBlocking {
				Log.e(Tag, "协程开始--------${Thread.currentThread().name}")
				delay(1000)  //阻塞当前线程
				btn_28.text = "测试完成" //

				123
			}


			Log.e(Tag, "----End---$runBlocking-----${Thread.currentThread().name}")
		}


		//todo 测试async
		btn_29.setOnClickListener {

			//runBlocking(Dispatchers.Main){
			//	Log.e(Tag, "---- Start --------${Thread.currentThread().name}")
			//	var progressDialog = ProgressDialog(this@MainActivity)
			//	progressDialog.show()
			//
			//
			//	var async = async(Dispatchers.IO) {
			//		Log.e(Tag, "async执行 --------${Thread.currentThread().name}")
			//		var a = 0
			//		repeat(1000000_000){
			//			++a
			//		}
			//
			//
			//		//delay(5000)
			//		a
			//	}
			//	Log.e(Tag, "----等待 --------${Thread.currentThread().name}")
			//	Log.e(Tag, "----End---${async.await()}-----${Thread.currentThread().name}")
			//
			//	progressDialog.dismiss()
			//}


			/*GlobalScope.launch(Dispatchers.Main) {
				Log.e(Tag, "---- Start --------${Thread.currentThread().name}")
				var progressDialog = ProgressDialog(this@MainActivity)
				progressDialog.show()


				var async = async() {
					Log.e(Tag, "async执行 --------${Thread.currentThread().name}")
					var a = 0
					//repeat(100000_000){
					//	//++a*3+(a-3)*10
					//	++a
					//}


					delay(5000)
					a
					Log.e(Tag, "async结束 --------${Thread.currentThread().name}")
				}
				Log.e(Tag, "----等待 --------${Thread.currentThread().name}")
				Log.e(Tag, "----End---${async.await()}-----${Thread.currentThread().name}")
				Log.e(Tag, "----等待 结束--------${Thread.currentThread().name}")
				progressDialog.dismiss()


			}*/


			runBlocking<Unit> {
				launch { // 默认继承 parent coroutine 的 CoroutineDispatcher，指定运行在 main 线程
					Log.e(Tag, "main runBlocking: I'm working in thread ${Thread.currentThread().name}")
					delay(1000)
					Log.e(Tag, "main runBlocking: After delay in thread ${Thread.currentThread().name}")
				}
				GlobalScope.launch(Dispatchers.Main) {
					Log.e(Tag, "Unconfined      : I'm working in thread ${Thread.currentThread().name}")
					delay(1000)
					Log.e(Tag, "Unconfined      : After delay in thread ${Thread.currentThread().name}")
				}
			}
		}
		///////////////////////////////////////////////////////////////////////////
		// =========================== 异步流 Flow =================================
		///////////////////////////////////////////////////////////////////////////

		//todo 挂起函数
		btn_30.setOnClickListener {
			runBlocking<Unit> {


				var time = measureTimeMillis {
					foo().forEach { value ->
						log("----$value")
					}
				}

				log("---- 时间：$time")

			}
		}

		//todo 流
		btn_31.setOnClickListener {
			/*runBlocking<Unit> {
				// 启动并发的协程以验证主线程并未阻塞
				//launch {
				//	for (k in 1..3) {
				//		//println("I'm not blocked $k")
				//		log("I'm not blocked $k")
				//		//delay(2000)
				//	}
				//}
				// 收集这个流
				foo_1().collect {value->
					log("结果：$value")
				}

			}*/
			/*GlobalScope.launch(Dispatchers.Main) {
				// 启动并发的协程以验证主线程并未阻塞
				launch {
					for (k in 1..3) {
						//println("I'm not blocked $k")
						log("I'm not blocked $k")
						delay(2000)//挂起2秒
					}
				}
				 //收集这个流
				foo_1().collect {value->
					log("结果：$value")
				}

			}*/




			GlobalScope.launch(Dispatchers.Main+CoroutineName("Hello")) {
				log("协程开始0")
				log("协程开始1")
				log("协程开始2")
				var withContext = withContext(Dispatchers.IO) {
					log("withContext---0")
					log("withContext---1")
					log("withContext---2")
					delay(3000)
					log("withContext---3")


					"ssss"
				}

				log("协程开始---3")

				delay(3000)
				log("协程开始---4")
				val job = coroutineContext[Job]
				log("${coroutineContext[Job]}------${this.coroutineContext}")
				log("-----------------------------")

				coroutineJob()
			}



			GlobalScope.launch(MyContinuationInterceptor()){

			}




		}























	}

	suspend inline fun Job.Key.currentJob() = coroutineContext[Job]

	suspend fun coroutineJob() {
		GlobalScope.launch{
			log(" 内部 "+Job.currentJob().toString())
		}
		log(" 外部 "+Job.currentJob().toString())
	}

	///////////////////////////////////////////////////////////////////////////
	// =========================== 其他代码 ==========================
	///////////////////////////////////////////////////////////////////////////

	suspend fun doSomethingUsefulOne(): Int {
		//Log.e(Tag, "doSomethingUseful One 该线程：----${Thread.currentThread().name}")
		//delay(4000L) // 假设我们在这里做了些有用的事
		Thread.sleep(4000)
		Log.e(Tag, "doSomethingUseful One 该线程：----${Thread.currentThread().name}")
		return 13
		//return 3/0
	}

	suspend fun doSomethingUsefulTwo(): Int {
		//Log.e(Tag, "doSomethingUseful Two 该线程：----${Thread.currentThread().name}")

		//delay(2000L) // 假设我们在这里也做了一些有用的事
		Thread.sleep(4000)
		Log.e(Tag, "doSomethingUseful Two 该线程：----${Thread.currentThread().name}")
		return 29
		//return 3 / 0
	}

	/**
	 * 注意，这些 xxxAsync 函数不是 挂起 函数。它们可以在任何地方使用。 然而，它们总是在调用它们的代码中意味着异步（这里的意思是 并发 ）执行。
	 * @return Deferred<Int>
	 */
	// somethingUsefulOneAsync 函数的返回值类型是 Deferred<Int>
	fun somethingUsefulOneAsync() = GlobalScope.async {
		Log.e(Tag, "计算1 ----${Thread.currentThread().name} ")
		doSomethingUsefulOne()
	}

	/**
	 * 注意，这些 xxxAsync 函数不是 挂起 函数。它们可以在任何地方使用。 然而，它们总是在调用它们的代码中意味着异步（这里的意思是 并发 ）执行。
	 * @return Deferred<Int>
	 */
	// somethingUsefulTwoAsync 函数的返回值类型是 Deferred<Int>
	fun somethingUsefulTwoAsync() = GlobalScope.async {
		Log.e(Tag, "计算2 ----${Thread.currentThread().name} ")

		doSomethingUsefulTwo()
	}

	//todo:使用 async 的结构化并发
	suspend fun  concurrentSum(): Int {
		var result = coroutineScope {
			val one = GlobalScope.async<Int> {
				Log.e(Tag, " 结构化并发 1----${Thread.currentThread().name} ")//---- DefaultDispatcher-worker-1
				doSomethingUsefulOne()

			}
			val two = async<Int> {
				Log.e(Tag, " 结构化并发 2----${Thread.currentThread().name} ") //----main
				doSomethingUsefulTwo()
			}


			one.await() + two.await()
			//one.await()
		}

		return result
	}

	suspend fun failedConcurrentSum(): Int = coroutineScope {
		val one = async<Int> {
			try {
				Log.e(Tag, " 结构化并发 1----${Thread.currentThread().name} ")
				delay(4000) // 模拟一个长时间的运算
				42
			} finally {
				Log.e(Tag, "First child was cancelled")
			}
		}
		val two = async<Int> {
			Log.e(Tag, "Second child throws an exception")
			throw ArithmeticException()
		}
		one.await() + two.await()
	}

	fun log(msg: String) = Log.e(Tag, "[${Thread.currentThread().name}] $msg")



	suspend fun foo(): List<Int> {
		delay(3000) // 假装我们在这里做了一些异步的事情
		return listOf(1, 2, 3)
	}



	fun foo_1(): Flow<Int> = flow { // 流构建器
		for (i in 1..3) {
			delay(2000) // 假装我们在这里做了一些有用的事情
			emit(i) // 发送下一个值
		}
	}


}

















