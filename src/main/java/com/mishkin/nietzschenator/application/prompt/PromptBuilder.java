package com.mishkin.nietzschenator.application.prompt;

public interface PromptBuilder<T> {

    String build(T input);
}

