package com.example.mycoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.logging.Logger
import kotlin.concurrent.thread

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
					repeat(1000) {
						println("I'm sleeping $it ...----${Thread.currentThread().name} ")
						delay(500L)
					}
				}
				println("Hello,----${Thread.currentThread().name}")
				delay(1300)
				println("World!----${Thread.currentThread().name}")
			}
		}











	}
}

















