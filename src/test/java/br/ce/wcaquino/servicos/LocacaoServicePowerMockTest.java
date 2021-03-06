package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.excpetion.FilmeSemEstoqueException;
import br.ce.wcaquino.excpetion.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class) //Informa que esta usando PoweMock
//@PrepareForTest({LocacaoService.class, DataUtils.class}) // A classe q ira testar
@PrepareForTest({LocacaoService.class}) // A classe q ira testar
public class LocacaoServicePowerMockTest {
	
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
		service = PowerMockito.spy(service); // Informa que o service sera gerenciado pelo powerMockito
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0));
		
		//MOCKANDO O CONSTRUTOR DA CLASSE DATE
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 04, 2017));
		
		//Mockando construtor STATIC
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 28);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, 2017);
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		//FIM
				
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		
        error.checkThat(locacao.getValor(), CoreMatchers.is(8.0)); //forma completa 
//        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());//usando matcher proprio @PrepareForTest a classe DateUtils, ele passa 
//		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));//usando matcher proprio @PrepareForTest a classe DateUtils, ele passa
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 04, 2017)), CoreMatchers.is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 04, 2017)), CoreMatchers.is(true));
	}
	
	
	
	@Test
	public void naoDeveDevolverFilmeNoDomingo() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));

		//MOCKANDO O CONSTRUTOR DA CLASSE DATE / TEM QUE ALTERAR NA LINHA 52 e 80
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
		
		//Mockando construtor STATIC
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 29);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, 2017);
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		//FIM
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);

		// verificacao
		Assert.assertTrue(ehSegunda);
		Assert.assertThat(locacao.getDataRetorno(), MatchersProprios.caiNumaSegunda()); //Usando Matcher proprio
//		Teste para Construtor
//		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments(); //Verificando quantas vezes chamou o metodo
		
		PowerMockito.verifyStatic(Mockito.times(2));
		Calendar.getInstance();

	}
	
	//Mockando o resultado de um metodo privado para nao ter que entrar nele
	@Test
	public void deveAlugarUmFIlme_SemCalcularValor() throws Exception {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
		
		//Mockando um metodo privado calcularValorLocacao
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}
	
	//Acessando um metodo privado com powerMock
		@Test
		public void deveTestarCalcularValorLocacao() throws Exception {
			List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
			
			//Entrando em um metodo privado. whitebox do pacote org.powermock.reflect.Whitebox
			Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
			Assert.assertThat(valor, CoreMatchers.is(4.0));
		}

	
}
