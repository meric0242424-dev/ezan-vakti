package com.ezanvakti.app.ui.kible

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ezanvakti.app.data.local.PrefsManager
import com.ezanvakti.app.databinding.FragmentKibleBinding
import com.ezanvakti.app.util.QiblaCalculator

class KibleFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentKibleBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var qiblaBearing = 0.0
    private var currentDialRotation = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKibleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val location = PrefsManager(requireContext()).getLocation()
        if (location != null) {
            qiblaBearing = QiblaCalculator.bearingToQibla(location.latitude, location.longitude)
            binding.textKibleDegree.text = "Kıble: ${qiblaBearing.toInt()}°"
        } else {
            binding.textStatus.text = "Konum bulunamadı, önce Ana Sayfa'yı açın"
        }

        if (accelerometer == null || magnetometer == null) {
            binding.textStatus.text = "Cihazınızda pusula sensörü bulunamadı"
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> System.arraycopy(event.values, 0, gravity, 0, 3)
            Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(event.values, 0, geomagnetic, 0, 3)
        }

        val rotationMatrix = FloatArray(9)
        val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
        if (success) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            val azimuth = Math.toDegrees(orientation[0].toDouble()).let { (it + 360) % 360 }

            // rotate dial opposite to device heading so "N" always points true north
            val dialTarget = (-azimuth).toFloat()
            // rotate needle so it always points towards Qibla regardless of device heading
            val needleTarget = (qiblaBearing - azimuth).toFloat()

            binding.compassDial.rotation = dialTarget
            binding.compassNeedle.rotation = needleTarget
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
