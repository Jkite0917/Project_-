package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// WeatherListItem 데이터 클래스: 각 날씨 항목에 대한 데이터를 저장
data class WeatherListItem(
    val wNo: Long,          // 기본 키
    val contents: String,   // 내용
    val weather: String,    // 날씨 설명 (String 타입으로 저장됨)
    val time: String,       // 알림 시간
    val isNotified: Boolean = false // 알림 여부 (중복 방지용)
)

// WeatherListAdapter 클래스: 날씨 항목 리스트를 RecyclerView에 표시하기 위한 어댑터
class WeatherListAdapter(
    private val items: MutableList<WeatherListItem>,  // WeatherListItem 리스트
    private val onDeleteClick: (Long) -> Unit         // 항목 삭제 클릭 시 호출할 콜백 함수
) : RecyclerView.Adapter<WeatherListAdapter.WeatherListViewHolder>() {

    // ViewHolder 클래스: 각 날씨 항목 뷰를 재사용하기 위한 클래스
    inner class WeatherListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contents: TextView = itemView.findViewById(R.id.textview_weatherItem_text)  // 내용 텍스트뷰
        private val weather: ImageView = itemView.findViewById(R.id.imageview_weatherItem_selectedWeatherIcon) // 날씨 아이콘 이미지뷰
        private val time: TextView = itemView.findViewById(R.id.textview_weatherItem_selectedTime)  // 알림 시간 텍스트뷰
        private val deleteButton: Button = itemView.findViewById(R.id.button_weatherItem_delete)    // 삭제 버튼

        // 항목에 데이터를 바인딩하는 함수
        fun bind(item: WeatherListItem) {
            // WeatherListItem의 데이터를 뷰에 설정
            contents.text = item.contents
            time.text = item.time

            // weather 설명에 따른 아이콘 설정
            weather.setImageResource(getWeatherIconId(item.weather))

            // isNotified 상태에 따라 항목의 투명도 조정 (알림이 전송된 경우 흐리게 표시)
            itemView.alpha = if (item.isNotified) 0.5f else 1.0f

            // 삭제 버튼 클릭 리스너 설정
            deleteButton.setOnClickListener {
                onDeleteClick(item.wNo) // 삭제 시 해당 아이템의 기본 키 전달
            }
        }

        // weather 설명에 따른 아이콘을 가져오는 함수
        private fun getWeatherIconId(weatherDescription: String): Int {
            return when (weatherDescription) {
                "clear sky" -> R.drawable.weather_sun_icon
                "partly cloudy" -> R.drawable.weather_cloud_icon
                "rain" -> R.drawable.weather_rain_icon
                "thunderstorm" -> R.drawable.weather_thunder_icon
                "snow" -> R.drawable.weather_snow_icon
                "clouds" -> R.drawable.weather_suncloud_icon
                else -> R.drawable.weather_sun_icon // 예외적인 경우 맑음 아이콘 반환
            }
        }
    }

    // ViewHolder 생성: item_weatherlist 레이아웃을 인플레이트하여 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weatherlist, parent, false)
        return WeatherListViewHolder(view)
    }

    // ViewHolder에 데이터 바인딩: 주어진 위치의 아이템 데이터를 ViewHolder에 바인딩
    override fun onBindViewHolder(holder: WeatherListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // 항목 수 반환
    override fun getItemCount(): Int = items.size
}
