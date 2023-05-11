package com.example.vamosrachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DecimalFormat
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, TextWatcher {
    private var tts: TextToSpeech? = null
    private lateinit var resultText: TextView
    private lateinit var valueToDivide: EditText
    private lateinit var peopleQuantity: EditText
    private var resultDivision: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        // float buttons
        val buttonPlay: FloatingActionButton = findViewById(R.id.play_tts)
        val buttonShare: FloatingActionButton = findViewById(R.id.share_value)
        // text fields
        valueToDivide = findViewById(R.id.value_to_divide)
        valueToDivide.addTextChangedListener(this)
        peopleQuantity = findViewById(R.id.people_quantity)
        peopleQuantity.addTextChangedListener(this)
        // text view
        resultText = findViewById(R.id.valor_resultado)

        buttonPlay.setOnClickListener {
            var speak: String = ""
            val people = peopleQuantity.text.toString()
            val decimalValue = DecimalFormat("#.##")
            if (people.isEmpty()) {
                speak = "Nenhuma divisão foi realizada"
            } else {
                speak = "O resultado da divisão foi ${decimalValue.format(resultDivision)} reais para cada uma das ${decimalValue.format(people.toDouble())}."
            }
            tts?.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null)
        }


    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val money = valueToDivide.text.toString()
        val people = peopleQuantity.text.toString()

        if(money.isEmpty() || people.isEmpty()) {
            Log.d("PDM23", "Valor zerado")
            resultText.setText("R$ 0.00")
        } else if ((money.toDouble()).equals(0) || people.toDouble().equals(0)) {
            Log.d("PDM23", "Divisão por 0")
            resultText.setText("R$ 0.00")
        } else {
            Log.d("PDM23", "Valor alterado")
            resultDivision = (money.toDouble()/people.toDouble())
            val decimalValue = DecimalFormat("#.##")
            resultText.setText("R$ ${decimalValue.format(resultDivision)}")
        }
    }

    fun onShareClick (view: View) {
        val people = peopleQuantity.text.toString()
        val decimalValue = DecimalFormat("#.##")
        if (people.isEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Valor zerado.")
            builder.setMessage("O valor da divisão entre os membros do grupo está zerado. Por favor preencha os espaços.")
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        } else {
            val textToShare = "O valor da divisão ficou R$ ${decimalValue.format(resultDivision)} para cada pessoa, que foram ${decimalValue.format(people.toDouble())} pessoas."
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, textToShare)
            startActivity(Intent.createChooser(intent, "Compartilhar o valor da divisão com..."))
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            Log.d("PDM23", "Sucesso na inicialização")
        } else {
            Log.d("PDM23", "Erro na inicialização do TTS")
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
