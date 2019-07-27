package com.algaworks.brewer.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;

/* Thread que faz o salvamento da foto no storage, para não travar o browser do cliente. */
public class FotoStorageRunnable implements Runnable {

	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;

	public FotoStorageRunnable(MultipartFile[] files, DeferredResult<FotoDTO> resultado, FotoStorage fotoStorage) {
		this.files = files;
		this.resultado = resultado;
		this.fotoStorage = fotoStorage;
	}

	@Override
	public void run() {

		/*
		 * Atribui o novo nome, pois precisa passar para a view, para conseguir
		 * recuperar a foto quando for salvar, para conseguir mover da pasta temporária
		 * para a pasta permanente.
		 */
		String nomeFoto = this.fotoStorage.salvar(files);
		String contentType = files[0].getContentType();

		/*
		 * O Spring somente retorna quando eu seto o resultado. Como liberamos as
		 * threads do tomcat para responder a outros clientes, quando eu seto o
		 * resultado, eu sinalizo ao tomcat para recuperar uma thread (dentro das
		 * threads do servidor que são usadas para lidar com as requisições do clientes)
		 * para responder so browser, que está esperando. Dessa forma, melhoro a
		 * disponibilidade da minha aplicação.
		 */
		resultado.setResult(new FotoDTO(nomeFoto, contentType, fotoStorage.getUrl(nomeFoto)));

	}
}
