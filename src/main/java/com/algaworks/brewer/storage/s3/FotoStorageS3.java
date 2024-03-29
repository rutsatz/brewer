package com.algaworks.brewer.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import net.coobird.thumbnailator.Thumbnails;

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageS3.class);

	private static final String BUCKET = "awbrewerteste";

	@Autowired
	private AmazonS3 amazonS3;

	@Override
	public String salvar(MultipartFile[] files) {
		/* Nome final do arquivo, com o UUID. */
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			/* Adiciona o sufixo no arquivo, para evitar o problema de repetir o nome. */
			novoNome = renomearArquivo(arquivo.getOriginalFilename());

			try {

				/*
				 * Para permitir que os usuários possam ler os arquivos, preciso alterar a
				 * permissão de leitura para público. Se não fizer isso, não será possível abrir
				 * os arquivos somente com a url.
				 */
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

				enviarFoto(novoNome, arquivo, acl);

				enviarThumbnail(novoNome, arquivo, acl);
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando o arquivo no S3", e);
			}
		}
		return novoNome;
	}

	@Override
	public byte[] recuperar(String foto) {
		InputStream is = amazonS3.getObject(BUCKET, foto).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Não conseguiu recuperar foto do S3", e);
		}
		return null;
	}

	@Override
	public byte[] recuperarThumbnail(String foto) {
		return recuperar(FotoStorage.THUMBNAIL_PREFIX + foto);
	}

	@Override
	public void excluir(String foto) {
		/* Já apaga a foto e o thumbnail direto. */
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto, FotoStorage.THUMBNAIL_PREFIX + foto));
	}

	@Override
	public String getUrl(String foto) {
		if (!StringUtils.isEmpty(foto)) {
			return "https://awbrewerteste.s3-sa-east-1.amazonaws.com/" + foto;
		}
		return null;
	}

	private ObjectMetadata enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList acl)
			throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		/*
		 * Tenho que seta o tamanho do arquivo, pq se não, ele tenta calcular e pode
		 * calcular a mais.
		 */
		metadata.setContentLength(arquivo.getSize());

		amazonS3.putObject(new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(), metadata)
				/* Passo as permissões para o PutObjectRequest. */
				.withAccessControlList(acl));
		return metadata;
	}

	private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
		/* Aqui, para o Thumbnail, é um pouco diferente. */
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(arquivo.getInputStream()).size(40, 68).toOutputStream(os);

		byte[] array = os.toByteArray();
		InputStream is = new ByteArrayInputStream(array);

		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		/*
		 * Tenho que seta o tamanho do arquivo, pq se não, ele tenta calcular e pode
		 * calcular a mais.
		 */
		thumbMetadata.setContentLength(array.length);

		amazonS3.putObject(new PutObjectRequest(BUCKET, THUMBNAIL_PREFIX + novoNome, is, thumbMetadata)
				.withAccessControlList(acl));
	}

}
