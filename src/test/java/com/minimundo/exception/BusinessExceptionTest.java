package com.minimundo.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void testConstrutorComMensagem() {
        String mensagem = "Erro de negócio";
        BusinessException exception = new BusinessException(mensagem);

        assertEquals(mensagem, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstrutorComMensagemECausa() {
        String mensagem = "Erro de negócio";
        Throwable causa = new RuntimeException("Causa original");
        BusinessException exception = new BusinessException(mensagem, causa);

        assertEquals(mensagem, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    void testStackTrace() {
        BusinessException exception = new BusinessException("Erro de negócio");
        StackTraceElement[] stackTrace = exception.getStackTrace();

        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }
} 