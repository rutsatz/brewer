package com.algaworks.brewer.controller.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.algaworks.brewer.controller.ErrosController;
import com.algaworks.brewer.model.Venda;

/** Validador customizado para a venda. */
@Component
public class VendaValidator implements Validator {

    /**
     * Esse método supports, retorna um booleano, em que digo qual classe que deve
     * ser validada. Ou seja, se a classe que estou recebendo, deve ser validada.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        /*
         * Estou dizendo que deve validar a classe Venda. Eu suporto a validação da
         * classe Venda.
         */
        return Venda.class.isAssignableFrom(clazz);
    }

    /** Esse método que implementa a validação. */
    @Override
    public void validate(Object target, Errors errors) {

        /**
         * Utilitário do Spring para a validação. Eu passo os Errors, o campo que quero
         * validar, um código de erro e a mensagem. Nesse caso, estou rejeitando se o
         * cliente não for informado.
         */
        ValidationUtils.rejectIfEmpty(errors, "cliente.codigo", "", "Selecione um cliente na pesquisa rápida");

        Venda venda = (Venda) target;

        validarSeInformouApenasHorarioEntrega(errors, venda);

        validarSeInformouItens(errors, venda);
        
        validarValorTotalNegativo(errors, venda);
    }

    private void validarValorTotalNegativo(Errors errors, Venda venda) {
        if(venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
            errors.reject("", "Valor total não pode ser negativo");
        }
    }

    private void validarSeInformouItens(Errors errors, Venda venda) {
        /*
         * Agora, como não é um campo específico que preciso validar, eu posso passar
         * somente a mensagem de erro.
         */
        if (venda.getItens().isEmpty()) {
            errors.reject("", "Adicione pelo menos uma cerveja na venda");
        }
    }

    private void validarSeInformouApenasHorarioEntrega(Errors errors, Venda venda) {
        /* Se somente a hora foi informada. */
        if (venda.getHorarioEntrega() != null && venda.getDataEntrega() == null) {
            /*
             * Agora eu rejeito o valor diretamente. Ali em cima, com o ValidationUtils, eu
             * usei um utilitário.
             * Então eu passo o campo que foi rejeitado, o código do erro e a mensagem.
             */
            errors.rejectValue("dataEntrega", "", "Informe uma data da entrega para um horário");
        }
    }

}
