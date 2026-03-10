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
import android.widget.Toast
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
                           Toast.makeText(this@MainActivity, "Você clicou $counter vezes", Toast.LENGTH_LONG).show()

                       }
                   }).also { messenger ->
                       replyTo = messenger }
               })
           }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.v(getString(R.string.app_name), "cliente esta desconectado desse serviço.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        incrementBoundServiceIntent = Intent().apply {
            component =  ComponentName(
                "br.edu.ifsp.scl.sdm.entityservicecommunication",
                "br.edu.ifsp.scl.sdm.entityservicecommunication.IncrementBoundService"
            )
        }
        if(!bindService(
                incrementBoundServiceIntent,
            incrementBoundServiceConnection,
                BIND_AUTO_CREATE //vinculando ao serviço que foi criado 36:00min
        )){
            Toast.makeText(this, "Não foi possível vincular ao serviço", Toast.LENGTH_LONG).show()
            finish()
        }

        with(amb) {
            mainTb.apply {
                getString(R.string.app_name).also { setTitle(it) }
                setSupportActionBar(this)
            }
            incrementBt.setOnClickListener {
                ibsMessenger.send(Message.obtain().apply {
                    data.putInt("VALUE", counter)
                })

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(incrementBoundServiceConnection)
    }
}