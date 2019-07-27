package com.algaworks.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {

	public final String THUMBNAIL_PREFIX = "thumbnail.";

	public String salvar(MultipartFile[] files);

//	public byte[] recuperarFotoTemporaria(String nome);

//	public void salvar(String foto);

	public byte[] recuperar(String foto);

	public byte[] recuperarThumbnail(String fotoCerveja);

	public void excluir(String foto);

	public String getUrl(String foto);

	/* Com o default, defino um método na interface. */
	default String renomearArquivo(String nomeOriginal) {
		return UUID.randomUUID().toString() + "_" + nomeOriginal;
	}

}
