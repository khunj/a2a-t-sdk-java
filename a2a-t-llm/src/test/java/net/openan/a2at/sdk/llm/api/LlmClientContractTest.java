package net.openan.a2at.sdk.llm.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import net.openan.a2at.sdk.llm.LLMClient;
import net.openan.a2at.sdk.llm.model.LLMResponse;
import net.openan.a2at.sdk.llm.model.StructuredGenerationRequest;
import org.junit.jupiter.api.Test;

class LlmClientContractTest {

    @Test
    void llmClientExposesPublicPathConstructors() {
        Constructor<?>[] constructors = LLMClient.class.getConstructors();

        assertEquals(2, constructors.length);
        for (Constructor<?> constructor : constructors) {
            assertTrue(Modifier.isPublic(constructor.getModifiers()));
            assertEquals(Path.class, constructor.getParameterTypes()[0]);
        }
    }

    @Test
    void llmClientExposesStructuredMethodReturningUnifiedResponse() throws NoSuchMethodException {
        Method method = LLMClient.class.getMethod("structured", StructuredGenerationRequest.class);

        assertEquals(LLMResponse.class, method.getReturnType());
        assertTrue(Modifier.isPublic(method.getModifiers()));
    }
}
