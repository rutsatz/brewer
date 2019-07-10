package com.algaworks.brewer.service.event.cerveja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.storage.FotoStorage;

@Component
public class CervejaListener {

	@Autowired
	private FotoStorage fotoStorage;

	/*
	 * Adiciona o listener no evento. Sempre que o CervejaSalvaEvent for publicado,
	 * o método será executado. Eu também posso colocar uma condição, para somente
	 * executar o método se aquela condição por atendida. Nesse exemplo, o #evento
	 * indica que é a variável evento que é recebida por parâmetro. Eles devem ter o
	 * mesmo nome.
	 */
	@EventListener(condition = "#evento.temFoto()")
	public void cervejaSalva(CervejaSalvaEvent evento) {
		fotoStorage.salvar(evento.getCerveja().getFoto());
	}

}
