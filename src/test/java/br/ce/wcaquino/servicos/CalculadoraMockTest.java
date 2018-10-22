package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {
	
	//Sempre que usa um Matcher no mockito, deve usar o outro valor como Matcher tbm
	@Test
	public void teste(){
		Calculadora calc = Mockito.mock(Calculadora.class);
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
//		Mockito.when(calc.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
		
		System.out.println(calc.somar(1, 100000));
	}
}
