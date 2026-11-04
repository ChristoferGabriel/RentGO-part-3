package com.example.rentgo.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentgo.data.repository.ReservasRepository
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReservasUiState(
    val reservas: List<Reserva> = emptyList(),
    val carregando: Boolean = true
) {
    fun reservaPorId(id: Int): Reserva? = reservas.find { it.id == id }
}

class ReservasViewModel(private val repository: ReservasRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservasUiState())
    val uiState: StateFlow<ReservasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.reservas
                .catch { e ->
                    Log.e(TAG, "Erro ao buscar as reservas", e)
                    emit(emptyList())
                }
                .collect { lista ->
                    _uiState.update { it.copy(reservas = lista, carregando = false) }
                }
        }
    }

    fun adicionarReserva(carro: Carro, dias: Int) {
        val nova = Reserva(
            carroId = carro.id,
            nomeCarro = carro.nome,
            precoDia = carro.precoDia,
            dias = dias
        )
        executar("inserir a reserva do carro ${carro.nome}") { repository.inserir(nova) }
    }

    fun removerReserva(reserva: Reserva, aoTerminar: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.deletar(reserva)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao deletar a reserva ${reserva.id}", e)
            }
            aoTerminar()
        }
    }

    private fun executar(acao: String, bloco: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                bloco()
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao $acao", e)
            }
        }
    }

    private companion object {
        const val TAG = "ReservasViewModel"
    }
}

class ReservasViewModelFactory(private val repository: ReservasRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservasViewModel::class.java)) {
            return ReservasViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}

@Composable
fun reservasViewModel(): ReservasViewModel {
    val context = LocalContext.current
    return viewModel(factory = ReservasViewModelFactory(ReservasRepository.criar(context)))
}
