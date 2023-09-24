package com.zosen.ecg

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.R
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.zosen.ecg.databinding.ActivityMainBinding
import java.util.Arrays


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var analytics: FirebaseAnalytics


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        analytics = Firebase.analytics
        mostraranuncio()

        MobileAds.initialize(
            this
        ) { }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        binding.button.setOnClickListener {

                val builder = AlertDialog.Builder(this@MainActivity)
                val view: View = LayoutInflater.from(this@MainActivity).inflate(com.zosen.ecg.R.layout.info, null)

            builder.setView(view)

                val texto = view.findViewById<TextView>(com.zosen.ecg.R.id.textView9)
                texto.text = "Realiza un calculo para estimar tus gastos de gasolina, principalmente en viajes largos, " +
                        "coloca los datos del precio de gasolina, y los kilometros que recorre tu vehiculo por litro," +
                        "debajo selecciona como deseas realizar el calculo, de acuerdo a los litros que tienes," +
                        "segun lo que quiereas colocarle en pesos, o bien la distancia que recorreras"
                builder.create()
                builder.show()
        }

        pre()

        /**Selectores spiners**/
        val opciones = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)

        opciones.addAll(Arrays.asList("Selecciona una opcion", "Litros", "Precio", "Kilometros"))

        binding.spinner.adapter = opciones
        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val seleccion = opciones.getItem(p2)
                loaddata()
                binding.textView8.text = "Escribe la cantidad de " + seleccion.toString()+ " que deseas calcular"

                if((binding.textView.text?.isEmpty() ?: binding.textView2.text?.isEmpty() ?: binding.textView3.text?.isEmpty()) == true)
                {
                    Toast.makeText(this@MainActivity, "Faltan los datos principales", Toast.LENGTH_SHORT).show()
                }else{

                    when (seleccion) {
                        "Selecciona una opcion"-> sinselec()
                        "Litros"-> calcxlto()
                        "Precio" -> calcxprec()
                        "Kilometros" -> calcxkm()
                        else -> Toast.makeText(this@MainActivity, "Debes seleccionar una opcion", Toast.LENGTH_LONG).show()
                    }
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun sinselec() {
        Toast.makeText(this@MainActivity, "Selecciona una opcion", Toast.LENGTH_LONG).show()
        binding.guardarbtn.setOnClickListener {
            muestra()
            Toast.makeText(this@MainActivity, "primero selecciona una opcion", Toast.LENGTH_LONG).show()
        }
    }

    private fun loaddata() {
        val litro = binding.textView.text.toString()
        val precio = binding.textView2.text.toString()
        val km = binding.textView3.text.toString()
        val preferences = getSharedPreferences("gas", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()

        editor.putString("lt", litro)
        editor.putString("p", precio)
        editor.putString("k", km)

        editor.putBoolean("loaddata", true)
        editor.apply()
    }

    private fun pre() {
        val preferences = getSharedPreferences("gas", Context.MODE_PRIVATE)
        binding.textView.setText(preferences.getString("lt", "1"))
        binding.textView2.setText(preferences.getString("p", "20"))
        binding.textView3.setText(preferences.getString("k", "10"))
    }

    private fun calcxkm() {
        Toast.makeText(this@MainActivity, "Coloca los kilometros", Toast.LENGTH_LONG).show()

        binding.guardarbtn.setOnClickListener {
            muestra()

            if (binding.editTextText.text?.isEmpty() ==true){
                Toast.makeText(this, "Debes agregar valores", Toast.LENGTH_SHORT).show()
            }
            else{

                val kmcalc = binding.editTextText.text

                val litro = binding.textView.text
                val precio = binding.textView2.text
                val km = binding.textView3.text

                val litrostot = kmcalc.toString().toFloat() * litro.toString().toFloat()
                val oper1: Float
                oper1 = litrostot / km.toString().toFloat()

                /**lts**/
                binding.textView5.text = "Con " + oper1.toString() + " Litros"
                val preciotot = kmcalc.toString().toFloat() * precio.toString().toFloat()
                val oper2: Float
                oper2 = preciotot / km.toString().toFloat()

                /**precio**/
                binding.textView6.text = "Inviertes " + oper2.toString() + " Pesos"

                /**kms**/
                binding.textView7.text = "Recorres " + kmcalc.toString() + " Kilometros"
            }


        }

    }

    private fun calcxprec() {
        Toast.makeText(this@MainActivity, "Coloca el precio", Toast.LENGTH_LONG).show()

        binding.guardarbtn.setOnClickListener {
            muestra()

            if (binding.editTextText.text?.isEmpty() ==true){
                Toast.makeText(this, "Debes agregar valores", Toast.LENGTH_SHORT).show()
            }else{
                val preciocalc = binding.editTextText.text

                val litro = binding.textView.text
                val precio = binding.textView2.text
                val km = binding.textView3.text

                val litrotot = preciocalc.toString().toFloat() * litro.toString().toFloat()
                val oper1: Float
                oper1 = litrotot / precio.toString().toFloat()
                /**lts**/
                binding.textView5.text = "Con " + oper1.toString() + " Litros"

                val precioto = preciocalc.toString().toFloat() * km.toString().toFloat()
                val oper2: Float
                oper2 = precioto / precio.toString().toFloat()
                /**precio**/
                binding.textView6.text = "Recorres " + oper2.toString() + " Kilometros"

                /**kms**/
                binding.textView7.text = "Inviertes " + preciocalc.toString() + " Pesos"

            }
        }

    }

    private fun calcxlto() {
        Toast.makeText(this@MainActivity, "Coloca los litros", Toast.LENGTH_LONG).show()


        binding.guardarbtn.setOnClickListener {
            muestra()

            if (binding.editTextText.text?.isEmpty() ==true){
                Toast.makeText(this, "Debes agregar valores", Toast.LENGTH_SHORT).show()
            }else{

                val ltocalc = binding.editTextText.text

                val litro = binding.textView.text
                val precio = binding.textView2.text
                val km = binding.textView3.text

                /**lts**/
                binding.textView5.text = "Con $ltocalc Litros"
                val preciotot = ltocalc.toString().toFloat() * precio.toString().toFloat()
                val oper2: Float
                oper2 = preciotot / litro.toString().toFloat()

                /**precio**/
                binding.textView6.text = "Inviertes " + oper2.toString() + " Pesos"

                val kmtotal = ltocalc.toString().toFloat() * km.toString().toFloat()
                val oper1: Float
                oper1 = kmtotal / litro.toString().toFloat()
                /**kms**/
                binding.textView7.text = "Recorres " + oper1.toString() + " Kilometros"
            }
        }
    }

    private fun mostraranuncio() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-7448805164496488/9234848490", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError?.toString()?.let { Log.d(ContentValues.TAG, it) }
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(ContentValues.TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun muestra() {
        mInterstitialAd?.show(this) }

}