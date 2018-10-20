package br.ce.wcaquino.servicos;

import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

import org.junit.Assert;

public class AssertTest {
	
	@Test
	public void test() {
		
		//Todas as assertivas tem a versao de negacao NOT
		
		Assert.assertTrue(true); 
		Assert.assertFalse(false);
		
		Assert.assertEquals(1, 1); //compara dois valores
		Assert.assertEquals(0.51234, 0.512, 0.001); // quando o valor é float ou double, é necessario colocar um delta com a margem de erro
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		
		//Quando estiver no valor primitivo, é necessario utilizar o ValueOf
		int i =5;
		Integer i2 =5;
		Assert.assertEquals(Integer.valueOf(i), i2); 
		Assert.assertEquals(i,  i2.intValue());
		
		//Verificando palavras ignorando maiuscula e startWith
		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola")); //ignora maiuscula
		Assert.assertTrue("bola".startsWith("bo")); //verifica se começa igual
		
		Usuario u1 = new Usuario("Usuario 1");
		Usuario u2 = new Usuario("Usuario 1");
		Usuario u3 = u2;
		
		Assert.assertEquals(u1, u2); //Verifica se o usuario é igual. Sendo necessario criar o Metodo Equals na classe Usuario
		Assert.assertSame(u3, u2); //Verifica se o objeto é da mesma instancia
		Assert.assertNotSame(u1, u2); //verifica se os objetos NAO sao da mesma instancia
		
		Assert.assertNotNull(u1); //Verifica se o objeto NAO é null
		
	}

}
