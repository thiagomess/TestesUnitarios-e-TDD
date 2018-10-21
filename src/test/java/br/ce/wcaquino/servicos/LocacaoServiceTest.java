package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
	
	private LocacaoService service;
	
	private static Integer numero = 0; //desse modo a variavel nao é reinicializada a cada teste
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
//		System.out.println("before");
		service = new LocacaoService();
		numero++;
//		System.out.println("contador: "+numero);
		
	}
	@After
	public void tearDown() {
//		System.out.println("after");
	}
	@BeforeClass
	public static void setupClas() {
//		System.out.println("before class");
	}
	@AfterClass
	public static void tearDownClass() {
//		System.out.println("after class");
	}
	
	
	@Test
	public void deveAlugarFilme() throws Exception {
		// Teste com Junit de condicao
		
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0));
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
/*		Assert.assertEquals(4.0, locacao.getValor(), 0.01);
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
*/
		
        error.checkThat(locacao.getValor(), CoreMatchers.is(8.0)); //forma completa 
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true)); 
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
	}
	
	
	
    //METODO 1 PARA  QND APENAS A EXCECAO FOR IMPORTANTE
    @Test(expected=FilmeSemEstoqueException.class) //Captura a exception aqui
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 0, 4.0));

		// acao
		service.alugarFilme(usuario, filmes);
	}	
	
    //METODO 2, FORMA MAIS ROBUSTA E PRINCIPALMENTE QND A MSG É IMPORTANTE
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0));

		// acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail("Deveria lançar excpetion"); // Para evitar falso positivo coloca como fail, 
                                                        //caso nao de fail na linha acima
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio")); //Pega a mensagem da 
                                                                                    //excpetion e compara
		}
	}	

    //METODO 3, FORMA NOVA IDEAL QND A EXCEPTION E A MSG SAO IMPORTANTES
   	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class); //captura a exception
		exception.expectMessage("Filme vazio"); //captura a mensagem do erro e compara

		// acao
		service.alugarFilme(usuario, null);
	}	
	
   	@Test
   	public void devePagar75PctNoFilme3() throws LocadoraException, FilmeSemEstoqueException {
   		
	// cenario
	Usuario usuario = new Usuario("Usuario 1");
	List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), 
										new Filme("Filme 3", 2, 4.0));
	
	// acao
	Locacao locacao = service.alugarFilme(usuario, filmes);
	
	Assert.assertThat(locacao.getValor(), CoreMatchers.is(11.0));
	
   	}
   	
   	@Test
   	public void devePagar50PctNoFilme4() throws LocadoraException, FilmeSemEstoqueException {
   		
	// cenario
	Usuario usuario = new Usuario("Usuario 1");
	List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), 
										new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));
	Double esperado = 13.0;
	// acao
	Locacao locacao = service.alugarFilme(usuario, filmes);
	
	Assert.assertEquals(esperado, locacao.getValor());
	
   	}
   	
 	@Test
   	public void devePagar25PctNoFilme5() throws LocadoraException, FilmeSemEstoqueException {
   		
	// cenario
	Usuario usuario = new Usuario("Usuario 1");
	List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), 
										new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), 
										new Filme("Filme 5", 2, 4.0));
	Double esperado = 14.0;
	// acao
	Locacao locacao = service.alugarFilme(usuario, filmes);
	
	Assert.assertEquals(esperado, locacao.getValor());
	
   	}
 	
	@Test
   	public void devePagar0PctNoFilme6() throws LocadoraException, FilmeSemEstoqueException {
   		
	// cenario
	Usuario usuario = new Usuario("Usuario 1");
	List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), 
										new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), 
										new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0));
	Double esperado = 14.0;
	// acao
	Locacao locacao = service.alugarFilme(usuario, filmes);
	
	Assert.assertEquals(esperado, locacao.getValor());
	
   	}
	
	@Test
   	public void naoDeveDevolverFilmeNoDomingo() throws LocadoraException, FilmeSemEstoqueException {
   		
	// cenario
	Usuario usuario = new Usuario("Usuario 1");
	List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0));
	
	// acao
	Locacao locacao = service.alugarFilme(usuario, filmes);
	boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
	
	//verificacao
	Assert.assertTrue(ehSegunda);
	
   	}
	
	
	
	
	
}
