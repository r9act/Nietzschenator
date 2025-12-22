package com.mishkin.nietzschenator.application.promptBuilder;

public interface PromptBuilder<T> {

    String build(T input);
}

