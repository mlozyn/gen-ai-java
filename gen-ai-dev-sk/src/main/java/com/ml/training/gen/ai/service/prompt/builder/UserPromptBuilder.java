package com.ml.training.gen.ai.service.prompt.impl.sk.builder;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class UserPromptBuilder {

    private static final KernelFunction<String> PROMPT =
        KernelFunctionFromPrompt.<String>createFromPrompt(
                """
                    {{$input}}
                    
                    Respond to the question above in up to four concise sentences
                    """.stripIndent()
            )
            .build();

    private final Kernel kernel;

    private String message;

    public UserPromptBuilder(@NonNull final Kernel kernel) {
        this.kernel = kernel;
    }

    public UserPromptBuilder withMessage(@NonNull final String message) {
        this.message = message;
        return this;
    }

    public String build() {
        Assert.hasLength(message, "User message must not be empty");

        final var arguments = KernelFunctionArguments.builder()
            .withVariable("input", message)
            .build();

        final FunctionResult<String> result = PROMPT.invokeAsync(kernel)
            .withArguments(arguments)
            .block();

        return result.getResult();
    }

}
