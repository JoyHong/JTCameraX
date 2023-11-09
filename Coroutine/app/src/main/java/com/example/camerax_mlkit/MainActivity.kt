/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.camerax_mlkit

import android.annotation.SuppressLint
import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import java.math.MathContext.UNLIMITED
import java.util.concurrent.CancellationException
import javax.inject.Scope
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var button10: Button
    private lateinit var button11: Button
    private lateinit var button12: Button
    private lateinit var button13: Button
    private lateinit var button14: Button
    private lateinit var button15: Button
    private lateinit var button16: Button
    private lateinit var button17: Button
    private lateinit var button18: Button
    private lateinit var button19: Button
    private lateinit var button20: Button
    private lateinit var button21: Button
    private lateinit var button22: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button1=findViewById(R.id.demo1)
        button1.setOnClickListener {
            GlobalScopeDemo()
        }
        button2=findViewById(R.id.demo2)
        button2.setOnClickListener { runBlockingdemo() }
        button3=findViewById(R.id.demo3)
        button3.setOnClickListener { coroutinScopedemo() }
        button4=findViewById(R.id.demo4)
        button4.setOnClickListener {
                supervisorScopedemo()
        }
        button5=findViewById(R.id.demo5)
        button5.setOnClickListener {
            Userdefineddemo()
        }
        button6=findViewById(R.id.demo6)
        button6.setOnClickListener {
            launchdemo()
        }
        button7=findViewById(R.id.demo7)
        button7.setOnClickListener {
            jobdemo()
        }
        button8=findViewById(R.id.demo8)
        button8.setOnClickListener { asyncdemo() }
        button9=findViewById(R.id.demo9)
        button9.setOnClickListener {
            CoroutineContextjobdemo()
        }
        button10=findViewById(R.id.demo10)
        button10.setOnClickListener {
            CoroutineDispatcherdemo()
        }
        button11=findViewById(R.id.demo11)
        button11.setOnClickListener {
            CoroutineNamedemo()
        }
        button12=findViewById(R.id.demo12)
        button12.setOnClickListener {
            CoroutineCanceldemo()
        }
        button13=findViewById(R.id.demo13)
        button13.setOnClickListener {
            CoroutineCanceldemo1()
        }
        button14=findViewById(R.id.demo14)
        button14.setOnClickListener {NonCancellabledemo() }
        button15=findViewById(R.id.demo15)
        button15.setOnClickListener { canceldemo() }
        button16=findViewById(R.id.demo16)
        button16.setOnClickListener { withTimeoutdemo() }
        button17=findViewById(R.id.demo17)
        button17.setOnClickListener {
            CoroutineExceptionHandlerdemo()
        }
        button18=findViewById(R.id.demo18)
        button18.setOnClickListener { SupervisorJobdemo() }
        button19=findViewById(R.id.demo19)
        button19.setOnClickListener {
            lifecycleScope.launch {
                 takephoto()

                Log.d(TAG, "RefreshUIdemo: 刷新UI")
            }
            Log.d(TAG, "onCreatedemo: 11111111111")
        }
        button20=findViewById(R.id.demo20)
        button20.setOnClickListener {
            CoroutineStartdemo()
        }
        button21=findViewById(R.id.demo21)
        button21.setOnClickListener {
            FLowdemo()
        }
        button22=findViewById(R.id.demo22)
        button22.setOnClickListener {
            Channeldemo()
        }
    }
}
//coroutinScope
//CoroutineScope是 Kotlin 协程中用来管理协程生命周期的接口，可以被视为是协程的作用域
//CoroutineScope 本身并不运行协程，它只是确保你不会失去对协程的追踪.
@OptIn(DelicateCoroutinesApi::class)
fun GlobalScopeDemo() {
    GlobalScope.launch {//全局协程作用域，在这个范围内启动的协程可以一直运行直到应用停止运行
        //身不会阻塞当前线程，且启动的协程相当于守护线程，不会阻止 JVM 结束运行
        launch {
            delay(400)
            Log.d(TAG, "GlobalScopeDemo: 4")
        }
        launch {
            delay(300)
            Log.d(TAG, "GlobalScopeDemo: 3")
        }
        Log.d(TAG, "GlobalScopeDemo:2")
    }
    Log.d(TAG, "GlobalScopeDemo: 1")
    Thread.sleep(500)
}
fun runBlockingdemo() {
  runBlocking {//runBlocking会阻塞其所在线程,,返回值是泛型T
        // runBlocking 本身带有阻塞线程的意味，但其内部运行的协程又是非阻塞的
        launch {

                delay(100)
                Log.d(TAG, "runBlockingdemo: 1")

        }
        launch {

                delay(100)
                Log.d(TAG, "runBlockingdemo: 2")

        }
        launch {

                delay(1200)
                Log.d(TAG, "runBlockingdemo: 3")
        }
      Log.d(TAG, "runBlockingdemo: 0")

    }
    Log.d(TAG, "runBlockingdemo:4")
}
fun coroutinScopedemo() {
    runBlocking {
        launch {
            delay(100)
            Log.d(TAG, "coroutinScopedemo:2")
        }
        try {
            coroutineScope {//用于创建一个独立的协程作用域，直到所有启动的协程都完成后才结束自身
                //runBlocking 方法会阻塞当前线程，而 coroutineScope不会，而是会挂起并释放底层线程以供其它协程使用
                launch {
                    delay(500)
                    Log.d(TAG, "coroutinScopedemo: 3")
                }
                delay(50)
                Log.d(TAG, "coroutinScopedemo: 1")
            }
        }catch (e:Exception){
            Log.e(TAG, "coroutinScopedemo: $e" )
        }
    }
}
fun supervisorScopedemo(){//作用域的特点就是抛出的异常不会连锁取消同级协程和父协程
    runBlocking{
        launch {
            delay(100)
            Log.d(TAG, "supervisorScopedemo: 1")
        }
            supervisorScope {
                launch {
                    delay(500)
                    Log.d(TAG, "supervisorScopedemo: 2----抛出异常")
                    try {
                        throw Exception("Failed")
                    }
                    catch (e:Exception){
                        Log.e(TAG, "supervisorScopedemo: $e" )
                    }
                }
                launch {
                    delay(600)
                    Log.d(TAG, "supervisorScopedemo: 3")
                }
            }
    }
    Log.d(TAG, "supervisorScopedemo: 4")
}
class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {//用户自定义CoroutineScope
    fun onCreate() {
        launch {
            repeat(5) {
                delay(200L * it)
                Log.d(TAG, "Userdefineddemo: $it")
            }
        }
        Log.d(TAG, "Userdefineddemo: created")
    }
    fun onDestroy() {
        cancel()
        Log.d(TAG, "Userdefineddemo:Destory")
    }

}
fun Userdefineddemo () {
    runBlocking {
        val activity = Activity()
        activity.onCreate()
        delay(1000)
        activity.onDestroy()
        delay(1000)
    }
}


//CoroutineBuilder:
//即协程构建器，协程在 CoroutineScope 的上下文中通过 launch、async 等协程构建器来进行声明并启动

//launch：Job 启动一个协程但不会阻塞调用线程,必须要在协程作用域(CoroutineScope)中才能调用,返回值是一个Job。
//launch 函数共包含三个参数：
//context。用于指定协程的上下文
//start。用于指定协程的启动方式，默认值为 CoroutineStart.DEFAULT，还有ATOMIC，LAZY ，UNDISPATCHED

// 即协程会在声明的同时就立即进入等待调度的状态，即可以立即执行的状态。可以通过将其设置为CoroutineStart.LAZY来实现延迟启动，即懒加载
//block。用于传递协程的执行体，即希望交由协程执行的任务

fun CoroutineStartdemo(){
  runBlocking {
       var job= launch(start =CoroutineStart.DEFAULT)//协程创建后立即开始调度，虽然是立即调度，不是立即执行，有可能在执行前被取消。
        {
            Log.d(TAG, "CoroutineStartdemo: default")
        }
        job.cancel()
        var job1=launch(start =CoroutineStart.LAZY)//主动调用Job的start、join或者await等函数时才会开始调度
        {
            Log.d(TAG, "CoroutineStartdemo: Lazy")
        }
        var job2=launch(start = CoroutineStart.ATOMIC){//在执行到第一个挂起点的时候是不响应取消操作的
            Log.d(TAG, "CoroutineStartdemo:ATOMIC 挂起前")
            delay(100)
            Log.d(TAG, "CoroutineStartdemo: ATOMIC挂起后")
        }
        job2.cancel()
        var job3=launch(start = CoroutineStart.UNDISPATCHED){
            //会立即执行，不用经过任何调度器
                Log.d(TAG, "CoroutineStartdemo: UNDISPATCHED挂起前")
                delay(100)
                Log.d(TAG, "CoroutineStartdemo: UNDISPATCHED挂起后")
            }
        job3.cancel()
      job.invokeOnCompletion {//invokeOnCompletion:这个是Job接口中的一个方法，一旦job完成就会被回调
          Log.d(TAG, "CoroutineStartdemo: job complete callback")

      }
      delay(500)
      job1.cancelAndJoin()

    }
}
@OptIn(ExperimentalTime::class)
fun launchdemo(){//A和 B是并行交叉执行的
    val time= measureTime {
    runBlocking {
        launch{
            repeat(3){
                delay(100)
                Log.d(TAG, "launchdemo: A---$it")
            }
        }
        launch {
            repeat(3){

                delay(  100)
                Log.d(TAG, "launchdemo: B--$it")
            }
        }
    }
}
    Log.d(TAG, "launchdemo: $time")
}

//Job 是协程的句柄，Job 是一个接口类型
// 使用 launch 或 async 创建的每个协程都会返回一个 Job 实例，该实例唯一标识协程并管理其生命周期

//阻塞等待直到此 Job 结束运行
//public suspend fun join()
@OptIn(DelicateCoroutinesApi::class)
fun jobdemo(){
        //将协程设置为延迟启动
            val job = GlobalScope.launch(start = CoroutineStart.LAZY) {
                for (i in 0..100) {
                    delay(100)
                }
            }
        job.invokeOnCompletion {
    //当 Job 结束运行时（不管由于什么原因）回调此方法，可用于接收可能存在的运行异常
            Log.d(TAG, "jobdemo: invokeOnCompletion:$it")
        }
    //当 Job 处于活动状态时为 true,如果 Job 未被取消或没有失败，则均处于 active 状态
    Log.d(TAG, "jobdemo: 1. job.isActive：${job.isActive}")
    //当 Job 正常结束或者由于异常结束，均返回 true
    Log.d(TAG, "jobdemo: 1.job.isCancelled:${job.isCancelled}")
    //当 Job 被主动取消或者由于异常结束，均返回 true
    Log.d(TAG, "jobdemo: 1.job.isCompleted:${job.isCompleted}")
    //返回的是Boolean
    //如果此调用的确启动了 Job，则返回 true
    //如果 Job 调用前就已处于 started 或者是 completed 状态，则返回 false
        job.start()
    Log.d(TAG, "jobdemo: 2. job.isActive：${job.isActive}")
    Log.d(TAG, "jobdemo: 2.job.isCancelled:${job.isCancelled}")
    Log.d(TAG, "jobdemo: 2.job.isCompleted:${job.isCompleted}")
    //休眠四百毫秒后再主动取消协程
        Thread.sleep(400)
    //用于取消 Job，可同时通过传入 Exception 来标明取消原因
        job.cancel(CancellationException("test"))
    Log.d(TAG, "jobdemo: 3. job.isActive：${job.isActive}")
    Log.d(TAG, "jobdemo: 3.job.isCancelled:${job.isCancelled}")
    Log.d(TAG, "jobdemo: 3.job.isCompleted:${job.isCompleted}")
}

//async,
//async 函数的返回值是一个 Deferred 对象。Deferred 是一个接口类型，继承于 Job 接口，async:Deferred<T>
// 所以 Job 包含的属性和方法 Deferred 都有，其主要是在 Job 的基础上扩展了 await()方法
//await() 是一个挂起函数，通常用于获取 Deferred 对象的结果,调用await（）会等待返回结果，也是不会继续方法剩余代码的，但是注意它不会阻塞线程的。
@OptIn(ExperimentalTime::class)
fun asyncdemo() {
    val time = measureTime {
        runBlocking {
            val asyncA = async {
                delay(3000)
                1
            }
            val asyncB = async {

                delay(4000)
                2
            }
            Log.d(TAG, "asyncdemo: before await()")
            Log.d(TAG, "asyncdemo: ${asyncA.await() + asyncB.await()}")
            Log.d(TAG, "asyncdemo: waiting await()finish")
        }
    }
    Log.d(TAG, "asyncdemo: $time")
}

//CoroutineContext
//CoroutineContext 使用以下元素集定义协程的行为：
//Job：控制协程的生命周期
//CoroutineDispatcher：将任务指派给适当的线程
//CoroutineName：协程的名称，可用于调试
//CoroutineExceptionHandler：处理未捕获的异常


//协程中的 Job 是其上下文 CoroutineContext 中的一部分，,job 实际上是 CoroutineScope.coroutineContext.job
// 可以通过 coroutineContext[Job] 表达式从上下文中获取到，我们可以通过控制 Job 来控制 CoroutineScope 的生命周期
fun CoroutineContextjobdemo(){
    val job=Job()
    val  scope= CoroutineScope(job+Dispatchers.IO)
    runBlocking {
        Log.d(TAG, "CoroutineContextjobdemo: job is$job")
        val job =scope.launch {
            try {
                delay(3000)
            }catch (e:CancellationException){
                Log.d(TAG, "CoroutineContextjobdemo: job is cancelled")
                throw e
            }
            Log.d(TAG, "CoroutineContextjobdemo: end")
            }
        delay(1000)
        Log.d(TAG, "CoroutineContextjobdemo: scope is ${scope.coroutineContext[Job]}")
        scope.coroutineContext[Job]?.cancel()

        }
    }

//CoroutineDispatcher
//CoroutineContext 包含一个 CoroutineDispatcher（协程调度器）用于指定执行协程的目标载体，
// 即 运行于哪个线程。CoroutineDispatcher 可以将协程的执行操作限制在特定线程上，也可以将其分派到线程池中，或者让它无限制地运行
//Dispatchers.Default。默认调度器，适合用于执行占用大量 CPU 资源的任务
//Dispatchers.IO。适合用于执行磁盘或网络 I/O 的任务
//Dispatchers.Unconfined。对执行协程的线程不做限制，可以直接在当前调度器所在线程上执行
//Dispatchers.Main。使用此调度程序可用于在 Android 主线程上运行协程，只能用于与界面交互和执行快速工作

fun CoroutineDispatcherdemo(){

    runBlocking <Unit>{
    launch {
        Log.d(TAG, "CoroutineDispatcherdemo: main runBlocking")
    }
        launch(Dispatchers.Default) {
            Log.d(TAG, "CoroutineDispatcherdemo: Default")
            launch(Dispatchers.Unconfined){
                Log.d(TAG, "CoroutineDispatcherdemo: Unconfined1")
            }
        }
        launch(Dispatchers.IO){
            Log.d(TAG, "CoroutineDispatcherdemo: IO")
            launch(Dispatchers.Unconfined){
                Log.d(TAG, "CoroutineDispatcherdemo: Unconfined2")
            }
        }
        launch(newSingleThreadContext("my Thread")){
            Log.d(TAG, "CoroutineDispatcherdemo: my Thread")
            launch(Dispatchers.Unconfined){
                Log.d(TAG, "CoroutineDispatcherdemo: Unconfined4")
            }
        }
        launch(Dispatchers.Unconfined){
            android.util.Log.d(android.content.ContentValues.TAG, "CoroutineDispatcherdemo: Uncondined3")
        }
        GlobalScope.launch {
            Log.d(TAG, "CoroutineDispatcherdemo: GlobalScope")
    }
    }
}

//withContext(),支持在不引入回调的情况下控制任何代码的执行线程池
suspend fun withContextdemo(){
    withContext(Dispatchers.IO){
        //控制线程池
        //接下去的代码都会在IO线程池内运行
        Log.d(TAG, "withContextdemo: I/O")
    }
}
//CoroutineName
//CoroutineName 用于为协程指定一个名字，方便调试和定位问题
fun CoroutineNamedemo(){
    runBlocking(CoroutineName("RunBlocking")){
        Log.d(TAG, "CoroutineNamedemo: ${coroutineContext[CoroutineName]}")
        launch(CoroutineName("MainCoroutineName")){
            Log.d(TAG, "CoroutineNamedemo: ${coroutineContext[CoroutineName]}")
        }
    }
}

//组合上下文元素
//当需要为协程上下文定义多个元素，此时就可以用 +
// 我们可以同时为协程指定 Dispatcher 和 CoroutineName
fun CoroutineContextmanydemo(){
    runBlocking {
        launch (Dispatchers.IO+CoroutineName("test")){

        }
    }
}

//协程的取消
//可取消的挂起函数在取消时会抛出 CancellationException
fun CoroutineCanceldemo() {
    runBlocking {
        val job = launch {
            repeat(1000) { i ->
                Log.d(TAG, "CoroutineCanceldemo: sleeping -$i")
                delay(500)
            }
        }
        delay(1300)
        Log.d(TAG, "CoroutineCanceldemo: Waiting")
        job.cancel()///为 cancel() 函数调用后会马上返回而不是等待协程结束后再返回，所以此时协程不一定就是已经停止运行了
        job.join()//等待job的完成
        //job.cancelAndJoin()等于上面两种
        Log.d(TAG, "CoroutineCanceldemo: quit")
    }
}
//协程可能无法取消
//外部只是相当于发起一个停止运行的请求，需要依靠协程响应请求后主动停止运行
//如果协程在执行计算任务前没有判断自身是否已被取消的话，此时就无法取消协程
fun CoroutineCanceldemo1(){
    runBlocking {
        val startTime=System.currentTimeMillis()
        val job=launch(Dispatchers.Default){
            var nextPrintTime=startTime
            var i=0
            while (i<5){
//                if (isActive){//加上判断，协程可以正常的取消
                    if (System.currentTimeMillis()>= nextPrintTime){
                        Log.d(TAG, "CoroutineCanceldemo1:  sleeping--${i++}")
                        nextPrintTime+=500
                    }
//                }else{
//                    return@launch
//                }
            }
        }
        delay(1300)
        Log.d(TAG, "CoroutineCanceldemo1: waiting")
        job.cancelAndJoin()
        Log.d(TAG, "CoroutineCanceldemo1: quit")
    }
}

//NonCancellable用于创建一个无法取消的协程作用域
fun NonCancellabledemo() {
    runBlocking {
        Log.d(TAG, "NonCancellabledemo: start")
    val launchA = launch {
        try {
            repeat(5) {
                delay(50)
                Log.d(TAG, "NonCancellabledemo: launchA--$it")

            }
        } finally {
            delay(50)
            Log.d(TAG, "NonCancellabledemo: launchA is Completed")
        }
    }
    val launchB = launch {
        try {
            repeat(5) {
                delay(50)
                Log.d(TAG, "NonCancellabledemo: launchB--$it")
            }
        } finally {
            withContext(NonCancellable) {//即使协程取消了也可以运行
                delay(2000)
                Log.d(TAG, "NonCancellabledemo: launchB is Completed")
            }
        }
    }
    delay(200)
    launchA.cancel()
    launchB.cancel()
        Log.d(TAG, "NonCancellabledemo: end")   
}
}


//父协程和子协程
//当一个协程在另外一个协程的协程作用域中启动时，它将通过 CoroutineScope.coroutineContext 继承其上下文，新启动的协程就被称为子协程，
// 子协程的 Job 将成为父协程 Job 的子 Job。父协程总是会等待其所有子协程都完成后才结束自身，
// 所以父协程不必显式跟踪它启动的所有子协程，也不必使用 Job.join 在末尾等待子协程完成


//传播取消操作
//一般情况下，协程的取消操作会通过协程的层次结构来进行传播：如果取消父协程或者父协程抛出异常，那么子协程都会被取消
//如果子协程被取消，则不会影响同级协程和父协程，但如果子协程抛出异常则也会导致同级协程和父协程被取消

fun canceldemo() {
    runBlocking {
        val request = launch {
            val job1 = launch {
                repeat(10) {
                    delay(300)
                    Log.d(TAG, "canceldemo: job1--$it")
                    if (it == 2) {
                        Log.d(TAG, "canceldemo: job1 canceled")
                        cancel()//子级被取消了并不影响父级和同级的协程
                    }
                }
            }
            val job2 = launch {
                repeat(10) {
                    delay(300)
                    Log.d(TAG, "canceldemo: job2--$it")
                }
            }
        }
        delay(1600)
        Log.d(TAG, "canceldemo: job2 canceled")
        request.cancel()//取消父级的协程
        delay(1000)
    }
}

//withTimeout 函数用于指定协程的运行超时时间，如果超时则会抛出 TimeoutCancellationException，从而令协程结束运行

fun withTimeoutdemo(){
  runBlocking {
      Log.d(TAG, "withTimeoutdemo: start")
      try {
          withTimeout(300){
              repeat(5){
                  delay(100)
                  Log.d(TAG, "withTimeoutdemo: $it")
              }
          }
      }catch (e:Exception){
          Log.e(TAG, "withTimeoutdemo: $e")
      }
     
      Log.d(TAG, "withTimeoutdemo: end")
  }

}

//异常
//launch 将异常视为未捕获异常，类似于 Java 的 Thread.uncaughtExceptionHandler，当发现异常时就会马上抛出。
// async 期望最终通过调用 await 来获取结果 (或者异常)，所以默认情况下它不会抛出异 它会静默地将异常丢弃
// 直到调用 async.await() 才会得到目标值或者抛出存在的异常
private val ioScope = CoroutineScope(Dispatchers.IO)
private fun fetchDocs() {
    ioScope.async {
        delay(500)
        Log.d(TAG, "fetchDocs: 111111111111")
        throw AssertionError()
    }
}


//CoroutineExceptionHandler
//主动捕获异常，CoroutineExceptionHandler 只会在预计不会由用户处理的异常上调用，因此在 async 中使用它没有任何效果
fun CoroutineExceptionHandlerdemo() {
    runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "CoroutineExceptionHandlerdemo: $exception")
    }
    val job = GlobalScope.launch(handler) {
        throw AssertionError()
    }
    val deferred = GlobalScope.async(handler) {
        throw ArithmeticException()
    }
    joinAll(job, deferred)
}
}


//SupervisorJob 取消操作只会向下传播，一个子协程的运行失败不会影响到同级协程和父协程
fun SupervisorJobdemo() {
    runBlocking {
    val supervisor = SupervisorJob()
     with(CoroutineScope( supervisor)) {
        val firstChild = launch(CoroutineExceptionHandler { _, _ -> }) {
            Log.d(TAG, "SupervisorJobdemo: First Child is failing")
            throw AssertionError("First child is cancelled")

        }
        val secondChild=launch {
            firstChild.join()
            Log.d(TAG, "SupervisorJobdemo: First child is cancelled: ${firstChild.isCancelled}, but second one is still active")
            try {
                delay(Long.MAX_VALUE)
            } finally {
                Log.d(TAG, "SupervisorJobdemo: Second child is cancelled because supervisor is cancelled")
            }
            
        }
        firstChild.join()
        Log.d(TAG, "SupervisorJobdemo: Cancelling supervisor")
        supervisor.cancel()
        secondChild.join()
    }
}
}


 suspend fun takephoto() {
//    withContext(Dispatchers.IO){
        repeat(3){
            delay(1000)
            Log.d(TAG, "takephotodemo:$it")
    }
//        }
    }

//Flow
fun FLowdemo(){//只有调用终止操作符以后，Flow才会工作(Flow是冷的)
    //Flow有两个生命周期回调函数:
    // onStart
    // onCompletion，回调触发的情形
    // Flow正常执行完毕；
    //Flow当中出现异常；
    //Flow被取消。
    //可以用catch捕获在上游的异常
    val scope= CoroutineScope(Dispatchers.IO)
    runBlocking {
        launch {
          val flow=  flow {//或者listOf(1,2,3,4,5).asFlow()  flowOf(1,2,3,4,5)
                for (i in 1..5) {
                    delay(100)
                    emit(i)//发射数据 上流
                }
            }.flowOn(Dispatchers.IO)//切换context，只对他的上游有作用，假如多次调用flowOn则当前flowOn的范围是到上一个flowOn
              .filter { it>2 }//三个中转站
                .onCompletion {
                    Log.d(TAG, "FLowdemo: Completion")
                }
                .onStart {//会返回一个新的Flow对象,优先级很高
                    Log.d(TAG, "FLowdemo: start")
                    emit(2)
                }
                .map { it*2 }
                .take(2)
//              .launchIn(scope)//指定了在flowon之后的代码在哪个线程，这个函数中调用了collect
                .collect {  Log.d(TAG, "valuedemo :${it}") }//收集数据
            //终止操作符collect终止操作符，代表数据流的终止
        // 集合中的操作符，比如first()、single()、fold、reduce...
        }
//        launch {
//            flow {//Flow它一次只处理一个数据
//            Log.d(TAG, "FLowdemo: send3")
//            emit(3)
//            Log.d(TAG, "FLowdemo: send4")
//            emit(4)
//            Log.d(TAG, "FLowdemo: send5")
//            emit(5)
//        } .filter {
//            Log.d(TAG, "FLowdemo:filter $it")
//            it>2
//        }.collect{
//            Log.d(TAG, "FLowdemo: collect$it")
//        }
//        }
    }
}

fun Channeldemo(){//Channel相较于Flow是热的，是指管有没有接收方，发送方都会工作"的模式
    runBlocking {
        val channel = Channel<Int>(capacity = 3, onBufferOverflow = BufferOverflow.DROP_LATEST){
            Log.d(TAG, "Channeldemo: 传递失败$it")//传递失败，onUndeliveredElement
        }
        //Channel适合在不同的协程之间传递信息,指定管道的容量是1
        //onBufferOverflow
        //这个是当指定了capacity的容量，等Channel的容量满了之后，Channel所应对的策略，这里主要有3种做法：
        //SUSPEND:当管道的容量满了以后，如果发送方继续发送数据，我们会挂起当前的send()方法。由于它是一个挂起函数，所以我们可以非阻塞的方式将发送方的流程挂起，等管道容量有空闲位置以后再恢复。这个逻辑非常好理解，就和Java实现的阻塞队列一样。
        //DROP_OLDEST:顾名思义，就是丢弃掉最旧的那个数据；
        //DROP_LATEST丢掉最新的数据，这里指还没有进入管道的数据；
        val job=launch {
            channel.send(1)
            Log.d(TAG, "Channeldemo: send 1")
            channel.send(2)
            Log.d(TAG, "Channeldemo: sned2")
            channel.send(3)
            Log.d(TAG, "Channeldemo: send 3")
            channel.close()//若未关闭管道则程序无法终止可以用produce{}
        }

       val job1= launch {
           //isClosedForReceive和isClosedForSend,判断在发送时和接收时Channel是否关闭
                Log.d(TAG, "Channeldemo: get${channel.receive()} from channel")
           channel.consumeEach {//
               Log.d(TAG, "Channeldemo: $it")
           }
        }
//        val channel1 = produce {
//            (1 .. 3).forEach {
//                send(it)
//                Log.d(TAG, "Channeldemo: produce send$it")
//            }
//        }
//
//        launch {
//            //在另一个协程中接收管道消息
//            for (i in channel1){
//                Log.d(TAG, "Channeldemo: produce receive$i")
//            }
//        }

    }

}









//        GlobalScope.launch(context = Dispatchers.IO) {//可以声明在I/O相关的线程上执行协程
//            //延时一秒
//            delay(1000)
//            Log.d(TAG, "onCreate: launch")
//        }
//        //主动休眠两秒，防止 JVM 过快退出
//        Thread.sleep(2000)
//        Log.d(TAG, "onCreate: end")

//        val continuation= suspend {//手动创建协程
//            println("In Coroutine")
//            5
//        }.createCoroutine(object :Continuation<Int>{
//            override fun resumeWith(result: Result<Int>) {
//                println("Coroutine end $result")
//            }
//            override val context=EmptyCoroutineContext
//        })
//        continuation.resume(Unit)

