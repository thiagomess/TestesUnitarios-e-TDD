package br.ce.wcaquino.servicos;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.excpetion.FilmeSemEstoqueException;
import br.ce.wcaquino.excpetion.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	//DOC. HAMCREST http://hamcrest.org/JavaHamcrest/javadoc/1.3/index.html?help-doc.html
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testeLocacao() throws Exception {
		// Teste com Junit de condicao

		// cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 4.0);

		// acao
		Locacao locacao = service.alugarFilme(usuario, filme);

		// verificacao
/*		Assert.assertEquals(4.0, locacao.getValor(), 0.01);
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
*/
		
        error.checkThat(locacao.getValor(), CoreMatchers.is(4.0)); //forma completa 
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true)); 
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
	}
	
	
	
    //METODO 1 PARA  QND APENAS A EXCECAO FOR IMPORTANTE
    @Test(expected=FilmeSemEstoqueException.class) //Captura a exception aqui
	public void testeLocacao_filmeSemEstoque() throws Exception {

		// cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 4.0);

		// acao
		service.alugarFilme(usuario, filme);
	}	
	
    //METODO 2, FORMA MAIS ROBUSTA E PRINCIPALMENTE QND A MSG É IMPORTANTE
	@Test
	public void testeLocacao_usuarioVazio() throws FilmeSemEstoqueException {

		// cenario
		LocacaoService service = new LocacaoService();
		Filme filme = new Filme("Filme 1", 2, 4.0);

		// acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail("Deveria lançar excpetion"); // Para evitar falso positivo coloca como fail, 
                                                        //caso nao de fail na linha acima
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio")); //Pega a mensagem da 
                                                                                    //excpetion e compara
		}
	}	

    //METODO 2, FORMA NOVA IDEAL QND A EXCEPTION E A MSG SAO IMPORTANTES
   	@Test
	public void testeLocacao_filmeVazio() throws FilmeSemEstoqueException, LocadoraException {

		// cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class); //captura a exception
		exception.expectMessage("Filme vazio"); //captura a mensagem do erro e compara

		// acao
		service.alugarFilme(usuario, null);
	}	
	
	
	
	
	
	
}
