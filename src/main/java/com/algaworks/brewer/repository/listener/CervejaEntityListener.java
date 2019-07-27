package com.algaworks.brewer.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

public class CervejaEntityListener {

	@Autowired
	private FotoStorage fotoStorage;

	/*
	 * Para cada cerveja carregada do banco, chama esse método. A cerveja é final
	 * pois eu não posso fazer algo do tipo cerveja = null.
	 */
	@PostLoad
	public void postLoad(final Cerveja cerveja) {

		/*
		 * Como essa é uma entidade gerenciada pela JPA e não pelo Spring, o FotoStorage
		 * não vai estar disponível e vai dar um NullPointerException. Para isso, eu
		 * posso chamar o método do Spring abaixo, que ele vai resolver as dependencias
		 * dessa classe para o contexto que eu passar, nesse caso, o this. Então ele vai
		 * ver que o FotoStorage não existe, e vai injetar pra mim.
		 */
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

		cerveja.setUrlFoto(fotoStorage.getUrl(cerveja.getFotoOuMock()));
		cerveja.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock()));
	}

}
