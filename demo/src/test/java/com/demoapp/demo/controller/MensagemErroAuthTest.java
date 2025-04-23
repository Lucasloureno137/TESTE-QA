package com.demoapp.demo.controller;

import com.demoapp.demo.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class MensagemErroAuthTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Verifica se a mensagem de senha inválida é exibida corretamente")
  void testMensagemErroSenhaInvalida() throws Exception {
    UserDTO user = new UserDTO();
    user.setEmail("teste@teste.com");
    user.setPassword("123"); // senha claramente inválida

    MvcResult result = mockMvc.perform(
        post("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user))
    ).andReturn();

    String resposta = result.getResponse().getContentAsString();

    assertTrue(resposta.contains("Senha não atende aos critérios mínimos de segurança"));
  }

  @Test
  @DisplayName("Verifica se a mensagem de e-mail inválido é exibida corretamente")
  void testMensagemErroEmailInvalidoSimples() throws Exception {
    UserDTO user = new UserDTO();
    user.setEmail("invalido.com");
    user.setPassword("Senha123!");

    MvcResult result = mockMvc.perform(
        post("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user))
    ).andReturn();

    String resposta = result.getResponse().getContentAsString();

    assertTrue(resposta.contains("E-mail inválido"));
  }

  @Test
  @DisplayName("Verifica se a mensagem de e-mail inválido é exibida corretamente (vários casos)")
  void testMensagemErroEmailInvalidoLista() throws Exception {
    List<String> emailsInvalidos = List.of(
        "teste@teste.com.br",
        "usuario_sem_arroba.com",
        "email@dominio"
    );

    for (String email : emailsInvalidos) {
      UserDTO user = new UserDTO();
      user.setEmail(email);
      user.setPassword("");

      MvcResult result = mockMvc.perform(
          post("/auth/signup")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(user))
      ).andReturn();

      String resposta = result.getResponse().getContentAsString();

      assertTrue(resposta.contains("E-mail inválido"));
    }
  }

  @Test
  @DisplayName("Verifica se a mensagem de e-mail inválido é exibida corretamente na recuperação de senha")
  void testMensagemErroEmailInvalidoRecuperarSenha() throws Exception {
    List<String> emailsInvalidos = List.of(
        "teste@teste.com.br",
        "usuario_sem_arroba.com",
        "email@dominio"
    );

    for (String email : emailsInvalidos) {
      UserDTO user = new UserDTO();
      user.setEmail(email);
      user.setPassword(""); // senha não necessária

      MvcResult result = mockMvc.perform(
          post("/auth/reset-password")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(user))
      ).andReturn();

      String resposta = result.getResponse().getContentAsString();

      assertTrue(resposta.contains("E-mail inválido"));
    }
  }
}
