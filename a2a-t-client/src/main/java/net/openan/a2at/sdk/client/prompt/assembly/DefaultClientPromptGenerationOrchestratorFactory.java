package net.openan.a2at.sdk.client.prompt.assembly;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.openan.a2at.sdk.client.prompt.orchestration.DefaultClientPromptGenerationOrchestrator;
import net.openan.a2at.sdk.core.model.PromptGenerationConfig;
import net.openan.a2at.sdk.llm.LLMClient;
import net.openan.a2at.sdk.prompt.resources.model.ScenarioDefinition;

/**
 * Default assembly entry point for client prompt generation orchestration.
 *
 * @since 2026-06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultClientPromptGenerationOrchestratorFactory {

    /**
     * Creates the default client prompt-generation orchestrator from the minimum required inputs.
     *
     * @param llmClient LLM client used for scenario recognition and slot extraction
     * @param scenarios supported scenario definitions
     * @param language locale identifier to load resources from
     * @param scenarioSystemPrompt system prompt for scenario recognition
     * @param scenarioUserPrompt user prompt for scenario recognition
     * @param slotSystemPrompt system prompt for slot extraction
     * @param slotUserPrompt user prompt for slot extraction
     * @return assembled default client prompt-generation orchestrator
     */
    public static DefaultClientPromptGenerationOrchestrator create(
            LLMClient llmClient,
            List<ScenarioDefinition> scenarios,
            String language,
            String scenarioSystemPrompt,
            String scenarioUserPrompt,
            String slotSystemPrompt,
            String slotUserPrompt) {
        return create(
                llmClient,
                scenarios,
                new PromptGenerationConfig(
                        language, scenarioSystemPrompt, scenarioUserPrompt, slotSystemPrompt, slotUserPrompt));
    }

    /**
     * Creates the default client prompt-generation orchestrator from one shared prompt-generation config bundle.
     *
     * @param llmClient LLM client used for scenario recognition and slot extraction
     * @param scenarios supported scenario definitions
     * @param config shared prompt-generation config bundle
     * @return assembled default client prompt-generation orchestrator
     */
    public static DefaultClientPromptGenerationOrchestrator create(
            LLMClient llmClient, List<ScenarioDefinition> scenarios, PromptGenerationConfig config) {
        return ClientPromptGenerationOrchestratorBuilder.builder()
                .llmClient(llmClient)
                .scenarios(scenarios)
                .language(config.language())
                .scenarioSystemPrompt(config.scenarioSystemPrompt())
                .scenarioUserPrompt(config.scenarioUserPrompt())
                .slotSystemPrompt(config.slotSystemPrompt())
                .slotUserPrompt(config.slotUserPrompt())
                .build();
    }
}
