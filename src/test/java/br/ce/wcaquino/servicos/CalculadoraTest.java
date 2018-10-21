package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.excpetion.NaoPodeDividirPorZeroException;

public class CalculadoraTest {

	private Calculadora calc;

	@Before
	public void setup() {
		calc = new Calculadora();
	}

	@Test
	public void deveSomarDoisNumeros() {
		// cenario
		int a = 4;
		int b = 5;

		// acao
		int resultado = calc.somar(a, b);

		// verificacao
		Assert.assertEquals(9, resultado);
	}

	@Test
	public void deveSubtrairDoisNumeros() {
		int a = 5;
		int b = 2;

		int resultado = calc.subtrair(a, b);

		Assert.assertEquals(3, resultado);
	}

	@Test
	public void deveSubtrairDividirDoisNumeros() throws NaoPodeDividirPorZeroException {
		int a = 4;
		int b = 2;

		int resultado = calc.dividir(a, b);

		Assert.assertEquals(2, resultado);
	}

	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void naoDeveSubtrairNumeroPorZero() throws NaoPodeDividirPorZeroException {
		int a = 4;
		int b = 0;

		int resultado = calc.dividir(a, b);

		Assert.assertEquals(4, resultado);
	}

}
