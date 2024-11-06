package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WeatherAddActivity(private val onSave: (WeatherListItem) -> Unit) : BottomSheetDialogFragment() {

    private var selectedWeatherIcon: Int = 0 // 선택한 날씨 아이콘의 리소스 ID 저장
    private var isWeatherIconSelected: Boolean = false // 날씨 버튼 선택 여부 저장
    private var selectedTimeButton: Button? = null // 선택된 시간 버튼 참조
    private lateinit var editTExt: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃을 인플레이트
        val view = inflater.inflate(R.layout.weather_add, container, false)

        val gridWeather: GridLayout = view.findViewById(R.id.WeatherAddGridLayout)
        editTExt = view.findViewById(R.id.WeatherAddEditText)

        // 날씨 아이콘 버튼 선택 처리
        gridWeather.children.forEach { child ->
            if (child is ImageButton) {
                child.setOnClickListener {
                    // 선택된 아이콘의 drawable ID 저장
                    selectedWeatherIcon = when (child.id) {
                        R.id.WeatherAddSunIcon -> R.drawable.weather_sun_icon
                        R.id.WeatherAddCloudIcon -> R.drawable.weather_cloud_icon
                        R.id.WeatherAddRainIcon -> R.drawable.weather_rain_icon
                        R.id.WeatherAddThunderIcon -> R.drawable.weather_thunder_icon
                        R.id.WeatherAddSnowIcon -> R.drawable.weather_snow_icon
                        R.id.WeatherAddSuncloudIcon -> R.drawable.weather_suncloud_icon
                        else -> 0
                    }

                    // 날씨 아이콘 선택됨을 표시
                    isWeatherIconSelected = true

                    // 선택된 날씨 아이콘 버튼 강조 표시
                    highlightSelectedWeatherButton(child, gridWeather)
                }
            }
        }

        // 시간 선택 버튼들
        val timeButtons = listOf(
            view.findViewById<Button>(R.id.WeatherAddBeforeDayButton),
            view.findViewById<Button>(R.id.WeatherAddTimeNowButton),
            view.findViewById<Button>(R.id.WeatherAddAllDayButton)
        )

        timeButtons.forEach { button ->
            button.setOnClickListener { selectTimeButton(button, timeButtons) }
        }

        // 저장 버튼 클릭 시 입력 내용 저장
        view.findViewById<Button>(R.id.WeatherAddSaveButton).setOnClickListener {
            val contents = editTExt.text.toString()
            val selectedTime = selectedTimeButton?.text?.toString() ?: ""

            when {
                !isWeatherIconSelected -> {
                    Toast.makeText(requireContext(), "날씨 버튼을 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
                selectedTimeButton == null -> {
                    Toast.makeText(requireContext(), "시간 버튼을 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
                contents.isEmpty() -> {
                    Toast.makeText(requireContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // 새로운 아이템 생성 및 저장 콜백 호출
                    val newItem = WeatherListItem(
                        wNo = 0L,                        // 처음에는 기본값으로 설정
                        contents = contents,             // 입력한 내용
                        weather = selectedWeatherIcon,   // 선택된 날씨 아이콘
                        time = selectedTime              // 선택된 시간
                    )
                    onSave(newItem)
                    dismiss() // 다이얼로그 닫기
                }
            }
        }

        return view // 인플레이트한 뷰 반환
    }

    // 시간 버튼을 업데이트하는 함수
    private fun selectTimeButton(button: Button, timeButtons: List<Button>) {
        // 모든 버튼 스타일 초기화
        timeButtons.forEach { btn ->
            btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent)) // 기본 투명색으로 초기화
            btn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        }

        // 선택된 버튼 강조 표시
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.buttonC)) // 버튼 강조 색상 적용
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        selectedTimeButton = button // 선택된 버튼을 저장
    }

    // 날씨 버튼을 강조 표시하는 함수
    private fun highlightSelectedWeatherButton(selectedButton: ImageButton, gridWeather: GridLayout) {
        // 모든 날씨 버튼 스타일 초기화
        gridWeather.children.forEach { child ->
            if (child is ImageButton) {
                child.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)
                child.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent)) // 기본 투명색으로 초기화
            }
        }

        // 선택된 날씨 버튼 강조 표시
        selectedButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)
        selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.buttonC)) // 버튼 강조 색상 적용
    }
}
