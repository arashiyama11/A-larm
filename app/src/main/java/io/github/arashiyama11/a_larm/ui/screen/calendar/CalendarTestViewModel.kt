package io.github.arashiyama11.a_larm.ui.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.arashiyama11.a_larm.domain.models.CalendarEvent
import io.github.arashiyama11.a_larm.domain.usecase.AlarmRulesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CalendarTestViewModel @Inject constructor(
    private val alarmRulesUseCase: AlarmRulesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarTestUiState())
    val uiState: StateFlow<CalendarTestUiState> = _uiState.asStateFlow()

    init {
        refreshEvents()
    }

    fun refreshEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val today = LocalDate.now()
                val events = alarmRulesUseCase.getCalendarEvents(today)
                _uiState.value = _uiState.value.copy(
                    events = events,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "不明なエラーが発生しました"
                )
            }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingConnection = true)
            
            try {
                // 接続テストの実装（実際のAPI呼び出しなど）
                kotlinx.coroutines.delay(1000) // シミュレーション用
                _uiState.value = _uiState.value.copy(
                    connectionStatus = true,
                    isTestingConnection = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    connectionStatus = false,
                    isTestingConnection = false,
                    error = e.message ?: "接続に失敗しました"
                )
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = granted)
    }

    fun getPermissionsToRequest(): Array<String> {
        return arrayOf("android.permission.READ_CALENDAR")
    }
}

data class CalendarTestUiState(
    val events: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = false,
    val isTestingConnection: Boolean = false,
    val connectionStatus: Boolean? = null,
    val hasPermission: Boolean = false,
    val error: String? = null
)
