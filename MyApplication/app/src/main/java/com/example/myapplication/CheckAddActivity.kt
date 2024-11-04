package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckAddActivity : BottomSheetDialogFragment() {
    private lateinit var titleEditText: EditText
    private lateinit var periodSpinner: Spinner
    private lateinit var weekDaySpinner: Spinner
    private lateinit var monthDaySpinner: Spinner
    private lateinit var saveButton: Button

    private lateinit var db: LocalDatabase

    // 시작 레이아웃 뷰 설정
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        return inflater.inflate(R.layout.checklist_add, container, false)

    }

    // 뷰 생성 시 실행되는 함수
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupPeriodSpinner()
        setupSaveButton()

    }
    
    // 뷰 id 지정
    private fun initializeViews(view: View) {
        titleEditText = view.findViewById(R.id.CheckPageEditID)
        periodSpinner = view.findViewById(R.id.CheckPageperiodSpinner)
        weekDaySpinner = view.findViewById(R.id.CheckPageweekDaySpinner)
        monthDaySpinner = view.findViewById(R.id.CheckPagemonthDaySpinner)
        saveButton = view.findViewById(R.id.CheckPagebuttonSave)
    }

    // 주기 선택 Spinner 리스트 설정
    private fun setupPeriodSpinner() {
        val periods = arrayOf("선택하세요", "매일", "매주", "매월")
        val periodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periods)
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        periodSpinner.adapter = periodAdapter

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (periods[position]) {
                    "매주" -> {
                        setSpinnerVisibility(weekDaySpinnerVisible = true, monthDaySpinnerVisible = false)
                        setupWeekDaySpinner()
                    }
                    "매월" -> {
                        setSpinnerVisibility(weekDaySpinnerVisible = false, monthDaySpinnerVisible = true)
                        setupMonthDaySpinner()
                    }
                    else -> setSpinnerVisibility(weekDaySpinnerVisible = false, monthDaySpinnerVisible = false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 주기 선택 Spinner 주기에 따른 Visibility 설정
    private fun setSpinnerVisibility(weekDaySpinnerVisible: Boolean, monthDaySpinnerVisible: Boolean) {
        weekDaySpinner.visibility = if (weekDaySpinnerVisible) View.VISIBLE else View.GONE
        monthDaySpinner.visibility = if (monthDaySpinnerVisible) View.VISIBLE else View.GONE
    }

    // 주기에 따른 주마다 선택할 요일
    private fun setupWeekDaySpinner() {
        val weekDays = arrayOf("일", "월", "화", "수", "목", "금", "토")
        val weekDayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weekDays)
        weekDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weekDaySpinner.adapter = weekDayAdapter
    }

    // 주기에 따른 달마다 선택할 날짜
    private fun setupMonthDaySpinner() {
        val monthDays = (1..31).map { it.toString() }.toTypedArray()
        val monthDayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthDays)
        monthDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthDaySpinner.adapter = monthDayAdapter
    }

    // 저장 버튼 입력 로직
    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val period = periodSpinner.selectedItem.toString()
            val weekDay = if (weekDaySpinner.visibility == View.VISIBLE) weekDaySpinner.selectedItem.toString() else ""
            val monthDay = if (monthDaySpinner.visibility == View.VISIBLE) monthDaySpinner.selectedItem.toString() else ""

            if (isInputValid(title, period)) {
                saveToDatabase(title, period, weekDay, monthDay)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "내용과 주기를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 저장 시 미입력 방지 구문
    private fun isInputValid(title: String, period: String): Boolean {
        return title.isNotEmpty() && period != "선택하세요"
    }

    // 생성한 일정 DB 저장 로직
    private fun saveToDatabase(title: String, period: String, weekDay: String, monthDay: String) {
        // Room DB에 데이터를 저장하는 함수
        val ChecklistItemData  = Checklist(
            cTitle = title,
            isChecked = false,
            period = period,
            weekDay = if (weekDay.isEmpty()) null else weekDay,  // 요일 값이 비어있으면 null, 아니면 해당 요일 값 사용
            monthDay = if (monthDay.isEmpty()) null else monthDay // 날짜 값이 비어있으면 null, 아니면 해당 날짜 값 사용
        )

        // IO 스레드에서 비동기로 데이터베이스 작업을 수행하기 위해 CoroutineScope 시작
        CoroutineScope(Dispatchers.IO).launch {
            db.getChecklistDao().insertChecklistItem(ChecklistItemData) // ChecklistItemData 객체를 DB에 저장
            println("데이터 저장 완료: 제목: $title, 주기: $period, 요일: $weekDay, 날짜: $monthDay") // 저장 완료 로그 출력
        }
    }

}
