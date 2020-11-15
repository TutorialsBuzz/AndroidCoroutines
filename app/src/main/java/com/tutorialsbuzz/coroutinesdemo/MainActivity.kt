package com.tutorialsbuzz.coroutinesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName.toString()
    private var job = Job()

    //coroutine Exception
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "$exception handled !")
    }

    //coroutine context
    val coroutineContext: CoroutineContext get() = Dispatchers.Main + job + handler

    //coroutine scope
    private val coroutineScope = CoroutineScope(coroutineContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchdataBtn.setOnClickListener {

            coroutineScope.launch(Dispatchers.Main) {
                Log.d(TAG, "Inside Thread ${Thread.currentThread().name}")

                //async returning Deferred < dataType >
                val responseData = fetchData()
                displayData(responseData.await());

                /**
                //async returning dataType
                val responseData1 = fetchDataAwait()
                displayData(responseData1);

                //withContext returning dataType
                val responseData2 = fetchDatawithContext()
                displayData(responseData2);
                 */
            }
        }
    }

    /**
     * In this example, fetchData() called on the main thread but it suspends the coroutine before it starts
     * the network request. When the network request completes , fetchData() resumes the suspended coroutine instead of using a
     * callback to notify the main thread.
     */

    fun fetchData(): Deferred<String?> {
        val loginUrl = "https://api.github.com/users/tutorialsbuzz"

        return coroutineScope.async(Dispatchers.IO) {
            val url = URL(loginUrl)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            val inputAsString =
                urlConnection.inputStream?.bufferedReader().use { it?.readText() }
            inputAsString
        }

    }

    fun displayData(data: String?) {
        resultText.setText(data)
    }

    /**
     * Another ways of By calling await() on deferred
     */

    suspend fun fetchDataAwait(): String? {
        val loginUrl = "https://api.github.com/users/tutorialsbuzz"

        val response = coroutineScope.async(Dispatchers.IO) {
            Log.d(TAG, "Inside Thread ${Thread.currentThread().name}")

            val url = URL(loginUrl)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            val inputAsString =
                urlConnection.inputStream?.bufferedReader().use { it?.readText() }
            inputAsString
        }.await()
        return response
    }


    /*
    * Another ways of By Returing dataType using withContext
    */

    suspend fun fetchDatawithContext(): String? {
        val loginUrl = "https://api.github.com/users/tutorialsbuzz"

        return withContext(Dispatchers.IO) {
            val url = URL(loginUrl)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            val inputAsString =
                urlConnection.inputStream?.bufferedReader().use { it?.readText() }
            inputAsString
        }

    }

}