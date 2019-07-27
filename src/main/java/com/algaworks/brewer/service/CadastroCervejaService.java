package com.algaworks.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.storage.FotoStorage;

@Service
public class CadastroCervejaService {

	@Autowired
	private Cervejas cervejas;

//	@Autowired
//	private ApplicationEventPublisher publisher;

	@Autowired
	private FotoStorage fotoStorage;

	@Transactional
	public void salvar(Cerveja cerveja) {
		cervejas.save(cerveja);

		/* Lança o evento de cerveja salva. */
//		publisher.publishEvent(new CervejaSalvaEvent(cerveja));
	}

	@Transactional
	public void excluir(Cerveja cerveja) {
		try {
			String foto = cerveja.getFoto();

			cervejas.delete(cerveja);
			/* Já tenta deletar do banco, que se der erro, ele já lança a exception. */
			cervejas.flush();

			/* Se conseguiu excluir do banco, apaga a imagem. */
			fotoStorage.excluir(foto);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Já foi usada em alguma venda.");
		}

	}

}
