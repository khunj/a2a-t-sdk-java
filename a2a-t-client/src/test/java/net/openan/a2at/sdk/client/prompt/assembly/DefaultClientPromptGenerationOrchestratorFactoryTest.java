package net.openan.a2at.sdk.client.prompt.assembly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.openan.a2at.sdk.client.model.PromptGenerationResult;
import net.openan.a2at.sdk.client.prompt.orchestration.DefaultClientPromptGenerationOrchestrator;
import net.openan.a2at.sdk.llm.LLMClient;
import net.openan.a2at.sdk.llm.adapter.LLMAdapter;
import net.openan.a2at.sdk.llm.model.LLMResponse;
import net.openan.a2at.sdk.llm.model.LlmUsage;
import net.openan.a2at.sdk.llm.model.StructuredGenerationRequest;
import net.openan.a2at.sdk.prompt.resources.model.ScenarioDefinition;
import org.junit.jupiter.api.Test;

class DefaultClientPromptGenerationOrchestratorFactoryTest {

    @Test
    void createBuildsPromptGenerationOrchestratorWithStructuredDefaultDependencies() throws IOException {
        RecordingAdapter adapter = new RecordingAdapter(
                "{\"matched\":true,\"scenario_code\":\"energy_saving\",\"error_message\":null}",
                "{\"slots\":{\"site\":\"Site A\",\"additional_notes\":\"critical\",\"limit\":\"5\",\"severity\":\"high\"},\"slot_errors\":[]}");
        LLMClient llmClient = buildClient(adapter);

        DefaultClientPromptGenerationOrchestrator orchestrator =
                DefaultClientPromptGenerationOrchestratorFactory.create(
                        llmClient,
                        List.of(new ScenarioDefinition(
                                "energy_saving", "Energy Saving", "Energy analysis", "Analyze site power")),
                        "en-US",
                        "Identify the best matching scenario.",
                        "Choose from the provided scenario list.",
                        "Extract slots from the input.",
                        "Return slots as JSON.");

        PromptGenerationResult result = orchestrator.generateTaskPrompt(java.util.Map.of(
                "site", "Site A",
                "additional_notes", "critical",
                "limit", "5",
                "severity", "high"));

        assertTrue(result.success());
        assertEquals(
                normalizeLineEndings("Site: Site A\nNotes: critical"),
                normalizeLineEndings(result.promptText().trim()));
        assertEquals(2, adapter.requestCount);
    }

    private static String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }

    private static LLMClient buildClient(RecordingAdapter adapter) throws IOException {
        Path envFile = Files.createTempFile("a2at-client-prompt-factory", ".env");
        Files.writeString(
                envFile,
                """
                A2AT_LLM_PROVIDER=openai_compatible
                A2AT_LLM_MODEL=test-model
                A2AT_LLM_API_KEY=test-key
                """);
        return new LLMClient(envFile, adapter);
    }

    private static final class RecordingAdapter implements LLMAdapter {

        private final java.util.List<String> payloads;

        private int requestCount;

        private RecordingAdapter(String... payloads) {
            this.payloads = new java.util.ArrayList<>(java.util.List.of(payloads));
        }

        @Override
        public LLMResponse structured(StructuredGenerationRequest request) {
            requestCount++;
            return new LLMResponse(payloads.remove(0), "test-model", new LlmUsage(1, 1, 2), Map.of());
        }
    }
}
