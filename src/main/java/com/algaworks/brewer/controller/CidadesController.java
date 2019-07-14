package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.exception.NomeCidadeJaCadastradaException;

@Controller
@RequestMapping("/cidades")
public class CidadesController {

	@Autowired
	private Cidades cidades;

	@Autowired
	private Estados estados;

	@Autowired
	private CadastroCidadeService cadastroCidadeService;

	@RequestMapping("/nova")
	public ModelAndView nova(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados", estados.findAll());
		return mv;
	}

	/*
	 * Habilita o cache para o método, evitando a consulta no banco toda vez que
	 * selecionar um estado diferente. Ele faz o cacheamento do método, então se o
	 * método foi chamado uma vez, ele fica em cache e não é mais executado. E como
	 * ele sabe se o método foi executado? Pelo parâmetro. Então, para aquele
	 * parâmetro, ele chama o método somente uma vez. Por parâmetro eu passo um nome
	 * do local onde o cache via ficar salvo. Quando eu precisar invalidar o cache,
	 * é esse nome que eu vou usar. O cache deve estar habilitado e configurado para
	 * funcionar.
	 *
	 * Colocar coisas em cache que não costumam mudar.
	 *
	 * Eu também posso configurar a chave do cache, para quando for invalidar,
	 * invalidar somente uma parte do cache, que realmente foi alterada, e deixar o
	 * resto do cache que não foi mexido, deixar intacto. Para isso, passo no key o
	 * nome do parâmetro com o #.
	 */
	@Cacheable(value = "cidades", key = "#codigoEstado")
	/**
	 * Passa o defaultValue = -1, pois se eu não selecionar nenhum valor no combo,
	 * ele pesquisa por codigo -1, que não existe, e não retorna nada.
	 */
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
			@RequestParam(name = "estado", defaultValue = "-1") Long codigoEstado) {
		return cidades.findByEstadoCodigo(codigoEstado);
	}

	/*
	 * Quando chamar o método para salvar uma nova cidade, deve invalidar o cache
	 * das cidades. Eu posso fazer isso com o @CacheEvict, passando o nome do cache
	 * que quero invalidar. Assim, na próxima requisição, ele vai executar o método
	 * novamente.
	 *
	 * Se estou configurando o cache por chave, na hora de invalidar, eu devo passar
	 * a chave que foi configurada no cache. Eu passo a chave, com o #, e posso
	 * navegar nos objetos, se for preciso. Se eu navegar nos objetos, eles podem
	 * estar null. Então para evitar um null pointer, eu adiciono uma condition,
	 * para somente invalidar se a condição for verdadeira. Eu passo o # e posso
	 * chamar um método que retorne booleano.
	 */
	@CacheEvict(value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
	@PostMapping("/nova")
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return nova(cidade);
		}

		try {
			cadastroCidadeService.salvar(cidade);
		} catch (NomeCidadeJaCadastradaException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return nova(cidade);
		}

		attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
		return new ModelAndView("redirect:/cidades/nova");
	}

	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cidade/PesquisaCidades");
		mv.addObject("estados", estados.findAll());

		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidades.filtrar(cidadeFilter, pageable),
				httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}

}
