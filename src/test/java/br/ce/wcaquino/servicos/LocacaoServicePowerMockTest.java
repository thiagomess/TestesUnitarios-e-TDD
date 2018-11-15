package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.excpetion.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class) //Informa que esta usando PoweMock
@PrepareForTest({LocacaoService.class, DataUtils.class}) // A classe q ira testar
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
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes =  Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0));
		
		//MOCKANDO O CONSTRUTOR DA CLASSE DATE
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 04, 2017));
				
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		
        error.checkThat(locacao.getValor(), CoreMatchers.is(8.0)); //forma completa 
        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());//usando matcher proprio @PrepareForTest a classe DateUtils, ele passa 
		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));//usando matcher proprio @PrepareForTest a classe DateUtils, ele passa
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 04, 2017)), CoreMatchers.is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 04, 2017)), CoreMatchers.is(true));
	}
	
	
	
	@Test
	public void naoDeveDevolverFilmeNoDomingo() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));

		//MOCKANDO O CONSTRUTOR DA CLASSE DATE
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 04, 2017));
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);

		// verificacao
		Assert.assertTrue(ehSegunda);
		Assert.assertThat(locacao.getDataRetorno(), MatchersProprios.caiNumaSegunda()); //Usando Matcher proprio
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments(); //Verificando quantas vezes chamou o metodo

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
