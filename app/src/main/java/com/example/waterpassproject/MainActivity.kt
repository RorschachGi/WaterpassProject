package com.example.waterpassproject

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.waterpassproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var sManager: SensorManager

    private var magnetic = FloatArray(9)

    private var gravity = FloatArray(9)

    //Данные с акселерометра
    private var accrs = FloatArray(3)

    //Данные с сенсора MagneticField
    private var magf = FloatArray(3)

    private var values = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Инициализируем SensorManager
        sManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorSecond = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val sListener = object: SensorEventListener{
            //Обработка данных
            override fun onSensorChanged(event: SensorEvent?) {
                when(event?.sensor?.type){
                    Sensor.TYPE_ACCELEROMETER-> accrs = event.values.clone()
                    Sensor.TYPE_MAGNETIC_FIELD-> magf = event.values.clone()
                }

                //Вычисление поворота
                SensorManager.getRotationMatrix(gravity, magnetic, accrs, magf)
                val outGravity = FloatArray(9)
                //Преобразование координатной системы
                SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X,SensorManager.AXIS_Z, outGravity)
                SensorManager.getOrientation(outGravity, values)

                val degree = values[2] * 57.2958f
                val rotate = 270 + degree
                binding.lRotation.rotation = rotate
                val rData = 90 + degree
                val color = if(rData.toInt() == 0){
                    Color.GREEN
                }else{
                    Color.RED
                }
                binding.lRotation.setBackgroundColor(color)
                binding.tvSensor.text = rData.toInt().toString()


            }
            //Точность измерений
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }
        //Регистрируем слушатель
        sManager.registerListener(sListener, sensor, SensorManager.SENSOR_DELAY_NORMAL )
        sManager.registerListener(sListener, sensorSecond, SensorManager.SENSOR_DELAY_NORMAL)
    }
}