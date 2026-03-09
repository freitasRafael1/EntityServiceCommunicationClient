package br.edu.ifsp.scl.sdm.entityservicecommunicationclient

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.sdm.entityservicecommunicationclient.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var incrementBoundServiceIntent: Intent
    private var counter = 0
    private lateinit var ibsMessenger: Messenger

    private val incrementBoundServiceConnection = object: ServiceConnection {
       override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
           Log.v(getString(R.string.app_name), "cliente esta vinculado a esse serviço")
           service?.also{
               ibsMessenger = Messenger(service)

               ibsMessenger.send(Message.obtain().apply {
                   Messenger(object: Handler(Looper.myLooper()!!) {
                       override fun handleMessage(msg: Message) {
                           super.handleMessage(msg)
                           counter = msg.data.getInt("VALUE")
                           TOAS

                       }
                   })
               })
           }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        with(amb) {
            mainTb.apply {
                getString(R.string.app_name).also { setTitle(it) }
                setSupportActionBar(this)
            }
            incrementBt.setOnClickListener {

            }
        }
    }
}