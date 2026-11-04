package com.example.rentgo.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentgo.data.repository.CarrosRepository
import com.example.rentgo.model.Carro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val QUANTIDADE_OFERTAS = 4

data class CarrosUiState(
    val carros: List<Carro> = emptyList(),
    val carregando: Boolean = true
) {

    val ofertas: List<Carro>
        get() = carros.sortedWith(compareBy<Carro>({ it.precoDia }, { it.id })).take(QUANTIDADE_OFERTAS)

    val favoritos: List<Carro>
        get() = carros.filter { it.favorito }

    fun carroPorId(id: Int): Carro? = carros.find { it.id == id }
}

class CarrosViewModel(private val repository: CarrosRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CarrosUiState())
    val uiState: StateFlow<CarrosUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            repository.carros
                .catch { e ->
                    Log.e(TAG, "Erro ao buscar os carros", e)
                    emit(emptyList())
                }
                .collect { lista ->
                    _uiState.update { it.copy(carros = lista, carregando = false) }
                }
        }
    }

    fun adicionarCarro(carro: Carro) = executar("inserir ${carro.nome}") { repository.inserir(carro) }

    fun atualizarCarro(carro: Carro) = executar("atualizar ${carro.nome}") { repository.atualizar(carro) }

    fun removerCarro(carro: Carro) = executar("deletar ${carro.nome}") { repository.deletar(carro) }

    fun alternarFavorito(carro: Carro) = atualizarCarro(carro.copy(favorito = !carro.favorito))

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
        const val TAG = "CarrosViewModel"
    }
}

class CarrosViewModelFactory(private val repository: CarrosRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarrosViewModel::class.java)) {
            return CarrosViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}

@Composable
fun carrosViewModel(): CarrosViewModel {
    val context = LocalContext.current
    return viewModel(factory = CarrosViewModelFactory(CarrosRepository.criar(context)))
}
