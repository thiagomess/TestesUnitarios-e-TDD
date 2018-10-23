package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {
	
	
	@Mock
	private Calculadora calcMock;
	
	@Spy // consegue acessar classes com implementação, os mocks somente interface
	private Calculadora calcSpy;
	
	@Mock
	private EmailService email;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockSpy(){
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
//		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5); //Desse modo ele entra no metodo somar
		Mockito.doReturn(5).when(calcSpy).somar(1, 2); //Desse modo ele nao entra no metodo somar que tem retorno
		Mockito.doNothing().when(calcSpy).imprime();//desse modo ele nao entra no metodo void
		
		System.out.println("Mock:" + calcMock.somar(1, 2));
		System.out.println("Spy:" + calcSpy.somar(1, 2));
		
		System.out.println("Mock");
		calcMock.imprime();
		System.out.println("Spy");
		calcSpy.imprime();
	}
	
	
	
	
	
	//Sempre que usa um Matcher no mockito, deve usar o outro valor como Matcher tbm
	@Test
	public void teste(){
		Calculadora calc = Mockito.mock(Calculadora.class);
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
//		Mockito.when(calc.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
		
//		System.out.println(calc.somar(1, 100000));
	}
	
	@Test
	public void teste2(){
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		//capturando argumento
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		Assert.assertEquals(5, calc.somar(134345, -234));
//		System.out.println(argCapt.getAllValues());//exibe todos os argumentos capturados
	}
}
