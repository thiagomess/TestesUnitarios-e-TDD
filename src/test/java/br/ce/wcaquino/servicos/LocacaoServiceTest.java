package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.excpetion.FilmeSemEstoqueException;
import br.ce.wcaquino.excpetion.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

//@RunWith(MockitoJUnitRunner.class) //Pode iniciar aqui ou no before
public class LocacaoServiceTest {
	//DOC. HAMCREST http://hamcrest.org/JavaHamcrest/javadoc/1.3/index.html?help-doc.html
	
	@InjectMocks //injeta o mock aqui
	private LocacaoService service;
	@Mock //define que sera mock
	private SpcService spc;
	@Mock
	private EmailService email;
	@Mock
	private LocacaoDao dao;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
//		System.out.println("before");
		MockitoAnnotations.initMocks(this); //Pode iniciar aqui ou no inicio da classe
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
		//Adiciona logica para nao excecutar no Sabado
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY)); 
		
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
//        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true)); 
        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());//usando matcher proprio
//        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));//usando matcher proprio
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
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
				new Filme("Filme 3", 2, 4.0));

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		Assert.assertThat(locacao.getValor(), CoreMatchers.is(11.0));

	}
   	
	@Test
	public void devePagar50PctNoFilme4() throws LocadoraException, FilmeSemEstoqueException {

		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
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
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0));
		Double esperado = 14.0;
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		Assert.assertEquals(esperado, locacao.getValor());
 
	}
 	
	@Test
	public void devePagar0PctNoFilme6() throws LocadoraException, FilmeSemEstoqueException {

		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0),
				new Filme("Filme 6", 2, 4.0));
		Double esperado = 14.0;
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		Assert.assertEquals(esperado, locacao.getValor());

	}
	
	@Test
	public void naoDeveDevolverFilmeNoDomingo() throws LocadoraException, FilmeSemEstoqueException {
		// Adiciona logica para excecutar no Sabado
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);

		// verificacao
		Assert.assertTrue(ehSegunda);
		Assert.assertThat(locacao.getDataRetorno(), MatchersProprios.caiNumaSegunda()); //Usando Matcher proprio

	}
	
	@Test
	public void deveLancarExceptionUsuarioNegativado() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
		
		Mockito.when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true); //mockando o dado esperado
		
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuario negativado");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesEmAtraso() {
		//cenario envia email para o usuario criado na lotacao, que simula um usuario com atraso.
		
		Usuario usuario = new Usuario("Usuario 1");
		Usuario usuario2 = new Usuario("Usuario em dia");
		List<Locacao>locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().comUsuario(usuario).atrasada().agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora()
				);
		
		//quando o dao obter as Locacoes, eu passo o mock
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtraso();
		
		//verifico se o email, foi enviado para o usuario que estava com atraso
		Mockito.verify(email).notificarAtraso(usuario); //verifica se este esta em atraso
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2); //verifica se esse nunca recebeu o email
		Mockito.verifyNoMoreInteractions(email); //verifica se mais ninguem recebeu o email
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("problema com o SPC");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = LocacaoBuilder.umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		
		//validacao
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class); //instancia
		Mockito.verify(dao).salvar(argumentCaptor.capture()); //caputra o valor que foi utilizado no metodo salvar
		Locacao locacaoRetornada = argumentCaptor.getValue(); //instancia com o valor capturado
		
		Assert.assertEquals(12.0, locacaoRetornada.getValor(), 0.01);
		Assert.assertThat( locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		Assert.assertTrue(DataUtils.isMesmaData(locacaoRetornada.getDataRetorno(), DataUtils.obterDataComDiferencaDias(3)));
	}
	
	
	
}
