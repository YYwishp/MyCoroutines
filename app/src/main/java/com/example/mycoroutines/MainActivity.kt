package com.example.mycoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.logging.Logger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)



		btn_first.setOnClickListener {
			//GlobalScope.launch 执行在子线程
			GlobalScope.launch { // 在后台启动一个新的协程并继续
				delay(5000L) // 非阻塞的等待 5 秒钟（默认时间单位是毫秒）
				println("World! ----${Thread.currentThread().name}") // 在延迟后打印输出 子线程
			}
			println("Hello,----${Thread.currentThread().name}") // 协程已在等待时主线程还在继续 主线程
		}



		btn_2.setOnClickListener {
			runBlocking<Unit> { // 开始执行主协程
				GlobalScope.launch { // 在后台启动一个新的协程并继续
					//delay(1000L)
					println("World!----${Thread.currentThread().name}")//子线程
				}
				println("Hello0,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
				println("Hello1,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
				println("Hello2,----${Thread.currentThread().name}") // 主协程在这里会立即执行  主线程
				runBlocking {     // 但是这个表达式阻塞了主线程
					println("主线程 开始 延迟----${Thread.currentThread().name}")//子线程
					delay(2000L)  // ……我们延迟 2 秒来保证 JVM 的存活 主线程
					println("主线程 结束 延迟----${Thread.currentThread().name}")//子线程
				}
			}
		}


		btn_3.setOnClickListener {
			runBlocking {
				val job = GlobalScope.launch { // 启动一个新协程并保持对这个作业的引用
					delay(3000L)
					println("World!----${Thread.currentThread().name}") //子线程 DefaultDispatcher-worker-2
				}
				println("Hello,----${Thread.currentThread().name}") //主线程
				job.join() // 等待直到子协程执行结束
			}
		}


		btn_4.setOnClickListener {
			runBlocking {
				launch {
					//delay(1000L)
					println("World!----${Thread.currentThread().name}")
				}
				println("Hello1,----${Thread.currentThread().name}")
				println("Hello2,----${Thread.currentThread().name}")
				println("Hello3,----${Thread.currentThread().name}")
				println("Hello4,----${Thread.currentThread().name}")


			}

			println("Hello44444,----${Thread.currentThread().name}")

		}


		btn_5.setOnClickListener {
			runBlocking {
				launch {
					//delay(200)
					println("Task from runBlocking----${Thread.currentThread().name}")



				}


				// 创建一个协程作用域
				coroutineScope {
					launch {
						//delay(555)
						println("Task from nested launch----${Thread.currentThread().name}")
					}

					//delay(100L)
					println("Task from coroutine scope0----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					println("Task from coroutine scope1----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					println("Task from coroutine scope2----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出
					println("Task from coroutine scope3----${Thread.currentThread().name}") // 这一行会在内嵌 launch 之前输出

				}
				println("Coroutine scope is over----${Thread.currentThread().name}") // 这一行在内嵌 launch 执行完毕后才输出
			}
		}


		btn_6.setOnClickListener {
			suspend fun doWorld() {
				delay(1000)
				println("World!----${Thread.currentThread().name}")
			}

			runBlocking {
				launch {
					doWorld()
				}
				println("Hello,----${Thread.currentThread().name}")
			}
		}

		btn_7.setOnClickListener {
			runBlocking {
				repeat(100) {
					launch {
						delay(1000)
						println("$it----${Thread.currentThread().name}")
					}
				}
			}
		}




		btn_8.setOnClickListener {
			runBlocking {


				GlobalScope.launch {
					repeat(10) {
						println("I'm sleeping $it ...----${Thread.currentThread().name} ")
						delay(500L)
					}
				}
				println("Hello,----${Thread.currentThread().name}")
				delay(1300)
				println("World!----${Thread.currentThread().name}")
			}
		}




		//=== 取消协程的执行
		btn_9.setOnClickListener {
			runBlocking {
				var job = launch {
					repeat(1000) {
						println("job: I'm sleeping $it ...----${Thread.currentThread().name} ")  //主线程
						delay(500L)
					}
				}



				delay(1300L) // 延迟一段时间
				println("main: I'm tired of waiting----${Thread.currentThread().name}!")
				job.cancel() // 取消该作业
				job.join() // 等待作业执行结束
				println("main: Now I can quit.----${Thread.currentThread().name}")
			}
		}


		//=== 取消是协作的
		btn_10.setOnClickListener {
			runBlocking {
				val startTime = System.currentTimeMillis()
				val job = launch(Dispatchers.Default){
					var nextPrintTime = startTime
					var i = 0
					while (i < 5) { // 一个执行计算的循环，只是为了占用 CPU
						// 每秒打印消息两次
						if (System.currentTimeMillis() >= nextPrintTime) {
							println("job: I'm sleeping ${i++} ...----${Thread.currentThread().name}")//子线程
							nextPrintTime += 500L
						}
					}
				}




				delay(1300L) // 等待一段时间
				println("main: I'm tired of waiting!----${Thread.currentThread().name}")
				job.cancelAndJoin() // 取消一个作业并且等待它结束  《如果协程正在执行计算任务，并且没有检查取消的话，那么它是不能被取消的》

				println("main: Now I can quit.----${Thread.currentThread().name}")
			}




		}


		//=== 使计算代码可取消 isActive 是一个可以被使用在 CoroutineScope 中的扩展属性。
		btn_11.setOnClickListener {

			runBlocking {


				val startTime = System.currentTimeMillis()
				val job = launch(Dispatchers.Default) {
					var nextPrintTime = startTime
					var i = 0
					while (isActive) { // 可以被取消的计算循环 当1300L时间过了，isActive就返回false
						// 每秒打印消息两次
						if (System.currentTimeMillis() >= nextPrintTime) {
							println("job: I'm sleeping ${i++} ...----${Thread.currentThread().name} ") // DefaultDispatcher-worker-1
							nextPrintTime += 500L
						}
					}
				}
				delay(1300L) // 等待一段时间
				println("main: I'm tired of waiting!----${Thread.currentThread().name} ")
				job.cancelAndJoin() // 取消该作业并等待它结束
				println("main: Now I can quit.----${Thread.currentThread().name}")

			}

		}

		//=== 在 finally 中释放资源
		btn_12.setOnClickListener {
			runBlocking {
				val job = launch {
					try {
						repeat(1000) { i ->
							println("job: I'm sleeping $i ...----${Thread.currentThread().name} ") //主线程
							delay(500L)
						}
					} finally {
						//delay(500L)
						println("job: I'm running finally ----${Thread.currentThread().name} ")

					}
				}
				delay(1300L) // 延迟一段时间
				println("main: I'm tired of waiting!----${Thread.currentThread().name} ")
				job.cancelAndJoin() // 取消该作业并且等待它结束
				println("main: Now I can quit.----${Thread.currentThread().name} ")
			}
		}


		//=== 运行不能取消的代码块
		btn_13.setOnClickListener {
			runBlocking {
				val job = launch {
					try {
						repeat(1000) { i ->
							println("job: I'm sleeping $i ...----${Thread.currentThread().name}")
							delay(500L)
						}
					} finally {
						withContext(NonCancellable) {
							println("job: I'm running finally----${Thread.currentThread().name}")
							delay(1000L)
							println("job: And I've just delayed for 1 sec because I'm non-cancellable----${Thread.currentThread().name}")
						}
					}
				}
				delay(1300L) // 延迟一段时间
				println("main: I'm tired of waiting!----${Thread.currentThread().name}")
				job.cancelAndJoin() // 取消该作业并等待它结束
				println("main: Now I can quit.----${Thread.currentThread().name}")
			}
		}


		//=== 超时  withTimeout
		btn_14.setOnClickListener {
			runBlocking {
				withTimeout(1300L) {
					repeat(1000) { i ->
						println("I'm sleeping $i ...----${Thread.currentThread().name} ")
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
						println("I'm sleeping $i ....----${Thread.currentThread().name} ")
						delay(500L)
					}
					"Done" // 在它运行得到结果之前取消它
				}
				println("Result is $result.----${Thread.currentThread().name} ")
			}
		}



		//todo:默认顺序调用
		btn_16.setOnClickListener {
			runBlocking {
				var time = measureTimeMillis {
					val one = doSomethingUsefulOne()
					val two = doSomethingUsefulTwo()
					println("The answer is ${one + two}----${Thread.currentThread().name} ")//main
				}

				println("Completed in $time ms----${Thread.currentThread().name} ")//main
			}
		}

		//todo; 使用 async 并发
		btn_17.setOnClickListener {
			runBlocking<Unit> {
				val time = measureTimeMillis {
					val one = async { doSomethingUsefulOne() }
					val two = async { doSomethingUsefulTwo() }
					println("The answer is ${one.await() + two.await()}")
				}
				println("Completed in $time ms")
			}

		}

		//todo: 惰性启动的 async
		btn_18.setOnClickListener {
			runBlocking<Unit> {
				val time = measureTimeMillis {
					val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
					val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
					// 执行一些计算
					one.start() // 启动第一个
					two.start() // 启动第二个
					println("The answer is ${one.await() + two.await()}----${Thread.currentThread().name} ")
				}
				println("Completed in $time ms----${Thread.currentThread().name} ")
			}

		}

		//todo：async 风格的函数
		btn_19.setOnClickListener {
			val time = measureTimeMillis {
				// 我们可以在协程外面启动异步执行
				println("计算开始  ----${Thread.currentThread().name} ")
				val one = somethingUsefulOneAsync()
				val two = somethingUsefulTwoAsync()
				// 但是等待结果必须调用其它的挂起或者阻塞
				// 当我们等待结果的时候，这里我们使用 `runBlocking { …… }` 来阻塞主线程
				var result = runBlocking {
					println("The answer is ${one.await() + two.await()}----${Thread.currentThread().name}")
					one.await() + two.await()
				}
				println(result)

			}
			println("Completed in $time ms ----${Thread.currentThread().name}  ")
		}



	}


	suspend fun doSomethingUsefulOne(): Int {
		delay(1000L) // 假设我们在这里做了些有用的事
		return 13
	}

	suspend fun doSomethingUsefulTwo(): Int {
		delay(3000L) // 假设我们在这里也做了一些有用的事
		return 29
	}

	/**
	 * 注意，这些 xxxAsync 函数不是 挂起 函数。它们可以在任何地方使用。 然而，它们总是在调用它们的代码中意味着异步（这里的意思是 并发 ）执行。
	 * @return Deferred<Int>
	 */
	// somethingUsefulOneAsync 函数的返回值类型是 Deferred<Int>
	fun somethingUsefulOneAsync() = GlobalScope.async {
		println("计算1 ----${Thread.currentThread().name} ")
		doSomethingUsefulOne()
	}

	/**
	 * 注意，这些 xxxAsync 函数不是 挂起 函数。它们可以在任何地方使用。 然而，它们总是在调用它们的代码中意味着异步（这里的意思是 并发 ）执行。
	 * @return Deferred<Int>
	 */
	// somethingUsefulTwoAsync 函数的返回值类型是 Deferred<Int>
	fun somethingUsefulTwoAsync() = GlobalScope.async {
		println("计算2 ----${Thread.currentThread().name} ")

		doSomethingUsefulTwo()
	}
}

















