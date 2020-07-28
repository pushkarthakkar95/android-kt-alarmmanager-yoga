package com.mlkitexample.yogachallenge.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mlkitexample.yogachallenge.R
import com.mlkitexample.yogachallenge.databinding.ActivityMainBinding
import com.mlkitexample.yogachallenge.model.Model
import com.mlkitexample.yogachallenge.repository.Resource
import com.mlkitexample.yogachallenge.repository.Status
import com.mlkitexample.yogachallenge.service.AlarmReceiver
import com.mlkitexample.yogachallenge.viewmodel.MainActivityViewModel
import com.mlkitexample.yogachallenge.viewmodel.MainActivityViewModelFactory
import kotlinx.android.synthetic.main.content_main.view.*

class MainActivity : AppCompatActivity() {
    val NOTIFICATION_ID = 0
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel
    lateinit var viewModelFactory: MainActivityViewModelFactory
    lateinit var countdown_timer: CountDownTimer
    var isRunning = false
    var time_in_milli_seconds = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        setSupportActionBar(binding.toolbar)
        viewModelFactory = MainActivityViewModelFactory()
        viewModel = ViewModelProvider(this,viewModelFactory).get(MainActivityViewModel::class.java)

    }

    override fun onResume() {
        super.onResume()
        if(intent.getBooleanExtra("getPose",false)){//users came to app via notification
            binding.contentId.yogaPoseImageView.visibility = View.VISIBLE
            binding.contentId.timer.visibility = View.GONE
            viewModel.yogaImageMutableLiveData.observe(this, Observer {
                handleResponse(it)
            })
            viewModel.setup()

        }else{//user opened the app themselves
            val hours = fetchPropFromPrefs(getString(R.string.hours_str))
            if(hours==100){//no time set
                binding.contentId.yogaPoseImageView.visibility = View.GONE
                binding.contentId.timer.visibility = View.GONE
                Toast.makeText(this,"Please use the settings icon to set time for challenges",Toast.LENGTH_LONG).show()
            }else{
                showTimer(hours)
            }
        }
    }

    private fun showTimer(hours:Int){
        binding.contentId.yogaPoseImageView.visibility = View.GONE
        binding.contentId.timer.visibility = View.VISIBLE
        val mins = fetchPropFromPrefs(getString(R.string.min_str))
        time_in_milli_seconds = viewModel.getDifferenceInTime(hours,mins)
        startTimer(time_in_milli_seconds)
    }

    private fun startTimer(time_in_seconds: Long) {
        if(isRunning){
            countdown_timer.cancel()
        }
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                binding.contentId.timer.text = "You will be notified when your challenge is ready!!"
                isRunning = false
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()
        isRunning = true

    }

    private fun updateTextUI() {
        val second: Long = time_in_milli_seconds / 1000 % 60
        val minute: Long = time_in_milli_seconds / (1000 * 60) % 60
        val hour: Long = time_in_milli_seconds / (1000 * 60 * 60) % 24

        binding.contentId.timer.text = "Next challenge in $hour hours $minute minutes and $second seconds"
    }

    private fun fetchPropFromPrefs(str: String): Int{
        val prefs = getSharedPreferences("yogachallenge.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        return prefs.getInt(str,100)
    }


    private fun handleResponse(response: Resource<Model.Result>){
        when(response.status){
            Status.LOADING -> handleLoading()
            Status.ERROR -> handleErrorResponse(response.message)
            Status.SUCCESS -> handleSuccessfulResponse(response.data)
        }
    }

    private fun handleLoading(){
        //Ad a progress dialog here
    }

    private fun handleErrorResponse(errorMessage:String?){
        if(errorMessage!=null){
            Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show()
        }else{
            showGenericError()
        }
    }

    private fun showGenericError(){
        Toast.makeText(this,"Something went wrong, please try again later",Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccessfulResponse(successfulResponse: Model.Result?){
        if(successfulResponse!=null){
            if(successfulResponse.hits.isNullOrEmpty()){
                showGenericError()
            }else{
                val url = viewModel.getRandomYogaPose(successfulResponse.hits)
                val imgUri = url.toUri().buildUpon()?.scheme("https")?.build()
                Glide.with(binding.contentId.yogaPoseImageView.context)
                    .load(imgUri)
                    .into(binding.contentId.yogaPoseImageView)
            }
        }else{
            showGenericError()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_time){
            showTimePicker()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun showTimePicker(){
        val newFragment =
            TimePickerFragment()
        newFragment.show(supportFragmentManager,"timePicker")
    }

    fun processTimePickerResult(triggerTime: Long){
        val notifyIntent = Intent(this, AlarmReceiver::class.java)
        val notifyPendingIntent = PendingIntent.getBroadcast(this,NOTIFICATION_ID, notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(notifyPendingIntent)
        val repeatInterval = AlarmManager.INTERVAL_DAY
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime,
            repeatInterval,notifyPendingIntent)
        showTimer(fetchPropFromPrefs(getString(R.string.hours_str)))
    }
}
