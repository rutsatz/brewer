package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.storage.FotoStorageRunnable;

/*
 * Anota como RestController, que é uma classe de conveniência, que adiciona as
 * anotações Controller e ResponseBody. Desse forma, não precisa adicionar o
 * ResponseBody nos métodos, o próprio Spring já adiciona.
 */
@RestController
@RequestMapping("/fotos")
public class FotosController {

	@Autowired
	private FotoStorage fotoStorage;

	/*
	 * Recebo os arquivos por parâmetro. Usa o RequestParam para dizer de qual campo
	 * pegar os dados, pois o arquivo é enviado no campo "files[]". O DeferredResult
	 * é um retorno assíncrono, para liberar a thread do servidor, que recebeu a
	 * requisição, para outros usuários.
	 */
	@PostMapping
	public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile[] files) {
		DeferredResult<FotoDTO> resultado = new DeferredResult<>();

		/*
		 * Roda a operação pesada dentro da Thread e o spring fica esperando a
		 * sinalização de tarefa terminada para retornar a requisição ao cliente,
		 * enquanto a thread do servidor é liberada para atender outro cliente. Quando a
		 * tarefa é finalizada, o spring recupera uma thread para retornar ao cliente.
		 */
		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage));
		thread.start();

		return resultado;
	}

	/*
	 * Quando coloco os :, passo uma expressão regular, ai o Spring vai ler a URL
	 * inteira. Se não fizer isso, ele não vai ler o .png do nome da foto.
	 */
//	@GetMapping("/temp/{nome:.*}")
//	public byte[] recuperarFotoTemporaria(@PathVariable String nome) {
//		return fotoStorage.recuperarFotoTemporaria(nome);
//	}

	/*
	 * Quando coloco os :, passo uma expressão regular, ai o Spring vai ler a URL
	 * inteira. Se não fizer isso, ele não vai ler o .png do nome da foto.
	 */
	@GetMapping("/{nome:.*}")
	public byte[] recuperar(@PathVariable String nome) {
		return fotoStorage.recuperar(nome);
	}

}
