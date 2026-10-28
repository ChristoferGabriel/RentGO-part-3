package com.example.rentgo.navigation

object Rotas {
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"

    const val HOME = "home"
    const val CARROS = "carros"
    const val AGENDAMENTO = "agendamento"
    const val FAVORITOS = "favoritos"
    const val PERFIL = "perfil"

    const val ARG_CARRO_ID = "carroId"
    const val DETALHE_CARRO = "detalhe_carro/{$ARG_CARRO_ID}"
    const val ESCOLHER_DIARIAS = "escolher_diarias/{$ARG_CARRO_ID}"

    const val PAGAMENTO = "pagamento"
    const val RESERVA_CONCLUIDA = "reserva_concluida"

    const val ARG_RESERVA_ID = "reservaId"
    const val DETALHE_RESERVA = "detalhe_reserva/{$ARG_RESERVA_ID}"

    const val CARTOES = "cartoes"
    const val ARG_CARTAO_ID = "cartaoId"
    const val DETALHE_CARTAO = "detalhe_cartao/{$ARG_CARTAO_ID}"

    fun detalheCarro(id: Int) = "detalhe_carro/$id"
    fun escolherDiarias(id: Int) = "escolher_diarias/$id"
    fun detalheReserva(id: Int) = "detalhe_reserva/$id"
    fun detalheCartao(id: Int) = "detalhe_cartao/$id"
}
